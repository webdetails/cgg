/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.scripts;

import java.awt.image.BufferedImage;
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
import pt.webdetails.cgg.charts.Chart;
import pt.webdetails.cgg.charts.Java2DChart;

/**
 *
 * @author pdpi
 */
class Java2DScript extends BaseScript {

    private static final Log logger = LogFactory.getLog(Java2DScript.class);
    private Map<String, String> params;
    private String source;
    private BufferedImage imageBuffer;
    private String[] dependencies;
    private long width, height;

    Java2DScript() {
    }

    Java2DScript(String source, long width, long height) {
        this();
        this.width = width;
        this.height = height;
        // sanitize slashes
        this.source = source.replaceAll("\\\\", "/").replaceAll("/+", "/");
    }

    @Override
    public Chart execute() {
        return execute((Map<String, String>) null);
    }

    @Override
    public Chart execute(Map<String, String> params) {
        Context cx = Context.getCurrentContext();
        cx = cx != null ? cx : ContextFactory.getGlobal().enterContext();
        try {
            // env.js has methods that pass the 64k Java limit, so we can't compile
            // to bytecode. Interpreter mode to the rescue!
            cx.setOptimizationLevel(-1);
            cx.setLanguageVersion(Context.VERSION_1_5);
            OutputStream bytes = new ByteArrayOutputStream();
            Main.setErr(new PrintStream(bytes));

            getGraphics();
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
            bytes.flush();
            logger.error(bytes.toString());
            return new Java2DChart(imageBuffer);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            Context.exit();
        }
        return null;
    }

    private void getGraphics() {
        imageBuffer = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_4BYTE_ABGR);
        Object wrappedGraphics = Context.javaToJS(imageBuffer.createGraphics(), scope);
        ScriptableObject.putProperty(scope, "graphics", wrappedGraphics);
    }
}
