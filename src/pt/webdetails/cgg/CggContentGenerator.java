/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg;

import pt.webdetails.cgg.charts.Chart;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cgg.scripts.ScriptFactory;
import pt.webdetails.cgg.scripts.Script;
import pt.webdetails.cgg.scripts.ScriptFactory.ScriptType;
import pt.webdetails.cgg.charts.SVGChart;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;

/**
 *
 * @author pdpi
 */
public class CggContentGenerator extends SimpleContentGenerator {

    
    private static final Log logger = LogFactory.getLog(CggContentGenerator.class);

    @Override
    public Log getLogger() {//TODO:?
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getPluginName() {
      return "cgg";
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void Draw(final OutputStream out) {
        draw(out);
    }


    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void draw(final OutputStream out) {
      CggService service = new CggService();
      service.draw(getResponse(), getRequest());
    }
   
}
