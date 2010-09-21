/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.script;

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

    private static ScriptFactory instance;

    public static synchronized ScriptFactory getInstance() {
        if (instance == null) {
            instance = new ScriptFactory();
        }
        return instance;
    }

    private ScriptFactory() {
    }

    public Script createScript(String path, String scriptType) {
        String solutionRoot = PentahoSystem.getApplicationContext().getSolutionRootPath();
        // Get necessary Pentaho environment: session and repository
        IPentahoSession session = PentahoSessionHolder.getSession();
        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);

        // Get the paths ot the necessary files: dependencies and the main script.
        ISolutionFile solutionFile = solutionRepository.getSolutionFile(path, 0);
        String[] dependencies;
        if (scriptType.equals("protovis")) {
        dependencies = new String[2];
        ISolutionFile env = solutionRepository.getSolutionFile("/system/" + CggContentGenerator.PLUGIN_NAME + "/lib/env.js", 0),
                protovis = solutionRepository.getSolutionFile("/system/" + CggContentGenerator.PLUGIN_NAME + "/lib/protovis.js", 0);
        dependencies[0] = solutionRoot + "/" + env.getSolutionPath() + "/" + env.getFileName();
        dependencies[1] = solutionRoot + "/" + protovis.getSolutionPath() + "/" + protovis.getFileName();
        } else {
        dependencies = new String[0];
        }
        Script script = new ProtovisScript(solutionRoot + solutionFile.getSolutionPath() + "/" + solutionFile.getFileName());
        script.setDependencies(dependencies);
        return script;
    }
}
