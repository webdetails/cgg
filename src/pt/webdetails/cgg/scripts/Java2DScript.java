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
        super(source);
        this.width = width;
        this.height = height;
    }

    @Override
    public Chart execute() {
        return execute((Map<String, String>) null);
    }

    @Override
    public Chart execute(Map<String, String> params) {
        Context cx = Context.getCurrentContext();
        try {
            getGraphics();
            executeScript(params);
            return new Java2DChart(imageBuffer);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (Context.getCurrentContext() != null) {
                Context.exit();
            }
        }
        return null;
    }

    private void getGraphics() {
        imageBuffer = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_4BYTE_ABGR);
        Object wrappedGraphics = Context.javaToJS(imageBuffer.createGraphics(), scope);
        ScriptableObject.putProperty(scope, "graphics", wrappedGraphics);
    }
}
