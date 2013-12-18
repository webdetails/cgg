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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import sun.print.resources.serviceui;

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
        IParameterProvider requestParams = parameterProviders.get(IParameterProvider.SCOPE_REQUEST);

        String script = requestParams.getStringParameter("script","");
        String type = requestParams.getStringParameter("type","svg");
        String outputType = requestParams.getStringParameter("outputType","png");
        String attachmentName = requestParams.getStringParameter("attachmentName","");
        String multiChartOverflow = requestParams.getStringParameter("multiChartOverflow", "");
        String cccVersion = requestParams.getStringParameter("cccVersion", "");

        String widthAsStr = requestParams.getStringParameter("width","0");
        Long width = 0L;
        try {
          width = Long.parseLong(widthAsStr);
        } catch (NumberFormatException nfe) {}

        String heightAsStr = requestParams.getStringParameter("height","0");
        Long height = 0L;
        try {
          height = Long.parseLong(heightAsStr);
        } catch (NumberFormatException nfe) {}

        HttpServletRequest  request  = getRequest();
        HttpServletResponse response = getResponse();

        CggService service = new CggService();

        if(response == null) {
            service.setOutputStream(out);
        }
        service.draw(script, type, outputType, attachmentName, multiChartOverflow, cccVersion, height, width, response, request);
    }

}
