/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.script;

import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.mozilla.javascript.*;
import org.mozilla.javascript.tools.shell.Main;

/**
 *
 * @author pdpi
 */
class ProtovisScript implements Script {

    private static final Log logger = LogFactory.getLog(ProtovisScript.class);
    private Map<String, String> params;
    private String source;
    private Scriptable scope;
    private String[] dependencies;

    ProtovisScript() {
    }

    ProtovisScript(String source) {
        this.source = source;
    }

    @Override
    public void setScope(Scriptable scope) {
        this.scope = scope;
    }

    @Override
    public String execute() {
        return execute((Map<String, String>) null);
    }

    @Override
    public String execute(Map<String, String> params) {
        Context cx = ContextFactory.getGlobal().enterContext();
        try {
            // env.js has methods that pass the 64k Java limit, so we can't compile
            // to bytecode. Interpreter mode to the rescue!
            cx.setOptimizationLevel(-1);
            cx.setLanguageVersion(Context.VERSION_1_5);
            OutputStream bytes = new ByteArrayOutputStream();
            Main.setErr(new PrintStream(bytes));

            Object wrappedParams;
            if (params != null) {
                wrappedParams = Context.javaToJS(params, scope);
            } else {
                wrappedParams = Context.javaToJS(new HashMap<String, String>(), scope);
            }
            ScriptableObject.defineProperty(scope, "params", wrappedParams, 0);

            try {
                cx.evaluateReader(scope, new FileReader(source), "<file>", 1, null);
            } catch (IOException ex) {
                logger.error("Failed to read " + source + ": " + ex.toString());
            }
            Object result = cx.evaluateString(scope, "output", "<cmd>", 1, null);
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
