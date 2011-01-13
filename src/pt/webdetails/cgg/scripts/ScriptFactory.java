/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.scripts;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;

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

        SVG, J2D
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

    public Script createScript(String path, String scriptType, long width, long height) {
        try {
            ScriptType st = ScriptType.valueOf(scriptType.toUpperCase());
            return createScript(path, st, width, height);
        } catch (IllegalArgumentException ex) {
            logger.error("No such script type: " + scriptType);
            return null;
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    public Script createScript(String path, ScriptType scriptType, long width, long height) throws FileNotFoundException {
        String solutionRoot = PentahoSystem.getApplicationContext().getSolutionRootPath();
        // Get necessary Pentaho environment: session and repository
        IPentahoSession session = PentahoSessionHolder.getSession();
        IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, session);
        Script script = null;
        try {
          Thread.currentThread().getContextClassLoader().loadClass("org.mozilla.javascript.Context");
        } catch (Exception e) {
            logger.error(e);
        }
        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);
        // Get the paths ot the necessary files: dependencies and the main script.
        ISolutionFile solutionFile = solutionRepository.getSolutionFile(path, 0);
        if (solutionFile == null) {
            throw new FileNotFoundException("Couldn't find " + path);
        }

        switch (scriptType) {
            case SVG:
                script = new SvgScript(solutionRoot + "/" + solutionFile.getSolutionPath() + "/" + solutionFile.getFileName());
                break;
            case J2D:
                script = new Java2DScript(solutionRoot + "/" + solutionFile.getSolutionPath() + "/" + solutionFile.getFileName(), width, height);
                break;
            default:
                script = null;
                break;

        }
        script.setScope(getScope(scriptType));

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


        String solutionRoot = PentahoSystem.getApplicationContext().getSolutionRootPath();
        // Get necessary Pentaho environment: session and repository
        IPentahoSession session = PentahoSessionHolder.getSession();
        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);
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
