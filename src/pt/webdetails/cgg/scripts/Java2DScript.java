/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg.scripts;

import java.awt.image.BufferedImage;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.mozilla.javascript.*;
import pt.webdetails.cgg.charts.Chart;
import pt.webdetails.cgg.charts.Java2DChart;

/**
 *
 * @author pdpi
 */
class Java2DScript extends BaseScript {

    private static final Log logger = LogFactory.getLog(Java2DScript.class);
    private BufferedImage imageBuffer;
    private long width, height;

    Java2DScript() {
    }

    Java2DScript(GenericPath source, long width, long height) {
        super(source);
        this.width = width;
        this.height = height;
    }

    @Override
    public Chart execute() {
        return execute((Map<String, Object>) null);
    }

    @Override
    public Chart execute(Map<String, Object> params) {
        ContextFactory.getGlobal().enter();
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
