/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.script;

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
    private Map<String, Scriptable> scopes;

    public static synchronized ScriptFactory getInstance() {
        if (instance == null) {
            instance = new ScriptFactory();
        }
        return instance;
    }

    private ScriptFactory() {
        scopes = new HashMap<String, Scriptable>();
    }

    public Script createScript(String path, String scriptType) {
        String solutionRoot = PentahoSystem.getApplicationContext().getSolutionRootPath();
        // Get necessary Pentaho environment: session and repository
        IPentahoSession session = PentahoSessionHolder.getSession();
        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);
        // Get the paths ot the necessary files: dependencies and the main script.
        ISolutionFile solutionFile = solutionRepository.getSolutionFile(path, 0);
        Script script = new ProtovisScript(solutionRoot + solutionFile.getSolutionPath() + "/" + solutionFile.getFileName());
        script.setScope(getScope(scriptType));
        return script;
    }

    private Scriptable getScope(String type) {

        if (!scopes.containsKey(type)) {
            scopes.put(type, createScope(type));
        }
        return scopes.get(type);
    }

    private Scriptable createScope(String type) {
        

        String solutionRoot = PentahoSystem.getApplicationContext().getSolutionRootPath();
        // Get necessary Pentaho environment: session and repository
        IPentahoSession session = PentahoSessionHolder.getSession();
        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);
        String[] dependencies;
        if (type.equals("protovis")) {
            dependencies = new String[2];
            ISolutionFile env = solutionRepository.getSolutionFile("/system/" + CggContentGenerator.PLUGIN_NAME + "/lib/env.js", 0),
                    protovis = solutionRepository.getSolutionFile("/system/" + CggContentGenerator.PLUGIN_NAME + "/lib/protovis.js", 0);
            dependencies[0] = solutionRoot + "/" + env.getSolutionPath() + "/" + env.getFileName();
            dependencies[1] = solutionRoot + "/" + protovis.getSolutionPath() + "/" + protovis.getFileName();
        } else {
            dependencies = new String[0];
        }

        Context cx = ContextFactory.getGlobal().enterContext();
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
        scopes = new HashMap<String, Scriptable>();
    }
}
