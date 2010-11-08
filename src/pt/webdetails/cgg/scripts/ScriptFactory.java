/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.scripts;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import pt.webdetails.cgg.CggContentGenerator;

/**
 *
 * @author pdpi
 */
public class ScriptFactory {

    private static final Log logger = LogFactory.getLog(ScriptFactory.class);
    private static ScriptFactory instance;
    private Map<ScriptType, Scriptable> scopes;

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
        scopes = new HashMap<ScriptType, Scriptable>();
    }

    public Script createScript(String path, String scriptType, long width, long height) {
        try {
            ScriptType st = ScriptType.valueOf(scriptType.toUpperCase());
            return createScript(path, st, width, height);
        } catch (IllegalArgumentException ex) {
            logger.error("No such script type: " + scriptType);
            return null;
        }
    }

    public Script createScript(String path, ScriptType scriptType, long width, long height) {
        String solutionRoot = PentahoSystem.getApplicationContext().getSolutionRootPath();
        // Get necessary Pentaho environment: session and repository
        IPentahoSession session = PentahoSessionHolder.getSession();
        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);
        // Get the paths ot the necessary files: dependencies and the main script.
        ISolutionFile solutionFile = solutionRepository.getSolutionFile(path, 0);

        Script script;
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

        Context cx = ContextFactory.getGlobal().enterContext();
        switch (type) {
            case SVG:
                cx.setOptimizationLevel(-1);
                cx.setLanguageVersion(Context.VERSION_1_5);
                dependencies = new String[2];
                ISolutionFile env = solutionRepository.getSolutionFile("/system/" + CggContentGenerator.PLUGIN_NAME + "/lib/env.js", 0),
                 protovis = solutionRepository.getSolutionFile("/system/" + CggContentGenerator.PLUGIN_NAME + "/lib/protovis.js", 0);
                dependencies[0] = (solutionRoot + "/" + env.getSolutionPath() + "/" + env.getFileName()).replaceAll("\\\\", "/").replaceAll("/+", "/");
                dependencies[1] = (solutionRoot + "/" + protovis.getSolutionPath() + "/" + protovis.getFileName()).replaceAll("\\\\", "/").replaceAll("/+", "/");
                break;
            case J2D:
                dependencies = new String[0];
                break;
            default:
                dependencies = new String[0];
                break;
        }
        BaseScope scope = new BaseScope();
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
        scopes = new HashMap<ScriptType, Scriptable>();
    }
}
