/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.script;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.Main;

/**
 *
 * @author pdpi
 */
class ProtovisScript implements Script {

    private static final Log logger = LogFactory.getLog(ProtovisScript.class);
    private Map<String, String> params;
    private String source;
    private Context cx;
    private Scriptable scope;
    private String[] dependencies;

    ProtovisScript() {
    }

    ProtovisScript(String source) {
        this.source = source;
    }

    @Override
    public void setDependencies(String[] files) {
        dependencies = files;
    }

    @Override
    public String execute() {
        return execute((Map<String, String>) null);
    }

    @Override
    public String execute(Map<String, String> params) {
        cx = ContextFactory.getGlobal().enterContext();
        try {
            // env.js has methods that pass the 64k Java limit, so we can't compile
            // to bytecode. Interpreter mode to the rescue!
            cx.setOptimizationLevel(-1);
            cx.setLanguageVersion(Context.VERSION_1_5);
            Global global = Main.getGlobal();
            OutputStream bytes = new ByteArrayOutputStream();
            Main.setErr(new PrintStream(bytes));
            if (!global.isInitialized()) {
                global.init(cx);
            }

            for (String file : dependencies) {
                Main.processSource(cx, file);
            }

            Object wrappedParams;
            if (params != null) {
                wrappedParams = Context.javaToJS(params, global);
            } else {
                wrappedParams = Context.javaToJS(new HashMap<String, String>(), global);
            }
            ScriptableObject.defineProperty(global, "params", wrappedParams, 0);

            Main.processSource(cx, source);
            Object result = cx.evaluateString(global, "output", "<cmd>", 1, null);
            String output = Context.toString(result);//.replaceAll("</?div.*?>", "");
            bytes.flush();
            logger.error(bytes.toString());
            return output;
        } catch (Exception e) {
            logger.error(e);
        } finally {
            Context.exit();
        }
        return "";
    }
}
