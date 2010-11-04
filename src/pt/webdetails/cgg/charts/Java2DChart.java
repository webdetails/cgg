/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.charts;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

/**
 *
 * @author pdpi
 */
public class Java2DChart implements Chart {

    private static final Log logger = LogFactory.getLog(Java2DChart.class);
    private Document svg;
    private RenderedImage buffer;

    public Java2DChart(RenderedImage buffer) {
        this.buffer = buffer;
    }

    public void toPNG(OutputStream out) {
        try {
            ImageIO.write(buffer, "png", out);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
}
