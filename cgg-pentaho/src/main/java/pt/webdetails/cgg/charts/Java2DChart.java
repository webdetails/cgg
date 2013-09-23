/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

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
