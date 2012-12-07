/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg.scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import java.util.UUID;
import javax.security.auth.callback.TextOutputCallback;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import pt.webdetails.cpf.repository.RepositoryAccess;

/**
 *
 * @author pdpi
 */
public class ScriptFactory {

    private static final Log logger = LogFactory.getLog(ScriptFactory.class);
    private static ScriptFactory instance;
    private Map<ScriptType, Scriptable> scopes;
    private String systemPath;

    public enum ScriptType {
        SVG, J2D;
        
        public static ScriptType parse(String value) throws IllegalArgumentException {
          return valueOf(StringUtils.upperCase(value));
        }
    }

    public static synchronized ScriptFactory getInstance() {
        if (instance == null) {
            instance = new ScriptFactory();
        }
        return instance;
    }

    private ScriptFactory() {
        scopes = new EnumMap<ScriptType, Scriptable>(ScriptType.class);
    }

    public Script createScript(String path, ScriptType scriptType, long width, long height) throws FileNotFoundException {
        try {
          Thread.currentThread().getContextClassLoader().loadClass("org.mozilla.javascript.Context");
        } catch (Exception e) {
            logger.error(e);
        }
        
        if(!path.startsWith("/system") && !RepositoryAccess.getRepository().resourceExists(path)){
          throw new FileNotFoundException("Couldn't find " + path);
        }
        
        
        if (!path.startsWith("/system")) {
          try {
          String script = RepositoryAccess.getRepository().getResourceAsString(path);
          
          String newPath = RepositoryAccess.getSystemDir() + "/cgg/.temp/c" + UUID.randomUUID().toString() + ".js";
          
          File f = new File(newPath);
          f.getParentFile().mkdirs();
          
          FileOutputStream fos = new FileOutputStream(f);
          fos.write(script.getBytes());
          fos.flush();
          fos.close();

          path = newPath;
          } catch (IOException e) {
            logger.error("IOException while copying script to system",e);
          }
          
        } else
          path = RepositoryAccess.getSolutionPath(path);
        
        Script script = null;
        switch (scriptType) {
            case SVG:
                script = new SvgScript(path);
                break;
            case J2D:
                script = new Java2DScript(path, width, height);
                break;
            default:
                script = null;
                break;

        }
        
        if(script!= null) {
          script.setScope(getScope(scriptType));
        }

        return script;
    }

    private synchronized Scriptable getScope(ScriptType type) {
        boolean cacheless = true;
        if (cacheless || !scopes.containsKey(type)) {
            scopes.put(type, createScope(type));
        }
        return scopes.get(type);
    }

    private synchronized Scriptable createScope(ScriptType type) {
        String[] dependencies;

        Context cx = ContextFactory.getGlobal().enter();
        switch (type) {
            case SVG:
                dependencies = new String[0];
                break;
            case J2D:
                dependencies = new String[0];
                break;
            default:
                dependencies = new String[0];
                break;
        }
        BaseScope scope = new BaseScope();
        scope.setSystemPath(systemPath);
        scope.init(cx);
        for (String file : dependencies) {
            try {
                cx.evaluateReader(scope, new FileReader(file), "<file>", 1, null);
            } catch (IOException ex) {
                logger.error("Failed to read " + file + ": " + ex.toString());
            }
        }
        Context.exit();
        return scope;
    }

    public void clearCachedScopes() {
        scopes = new EnumMap<ScriptType, Scriptable>(ScriptType.class);
    }

    public void setSystemPath(String systemPath) {
        this.systemPath = systemPath.replaceAll("\\\\", "/").replaceAll("/+", "/");
    }
}
