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

    private static final long serialVersionUID = 1L;
    
    private static final Log logger = LogFactory.getLog(CggContentGenerator.class);
    private static final String MIME_SVG = "image/svg+xml";


    private enum OutputType {

        SVG, PNG, PDF;
        
        public static OutputType parse(String value){
          return valueOf(StringUtils.upperCase(value));
        }
    }

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
        try {

            HashMap<String, Object> params = new HashMap<String, Object>();
            IParameterProvider requestParams = getRequestParameters();
            @SuppressWarnings("unchecked")
            Iterator<String> inputParams = requestParams.getParameterNames();
            while (inputParams.hasNext()) {
                String paramName = inputParams.next();
                if (paramName.startsWith("param")) {
                    String pName = paramName.substring(5);
                    Object[] p = requestParams.getArrayParameter(paramName, null);
                    if (p.length == 1) { // not *really* an array, is it?
                        params.put(pName, p[0]);
                    } else {
                        params.put(pName, p);
                    }
                }
            }

            String scriptName = requestParams.getStringParameter("script", "");
            String scriptTypeParam = requestParams.getStringParameter("type", "svg");
            String outputTypeParam = requestParams.getStringParameter("outputType", "png");
            
            OutputType outputType = OutputType.parse(outputTypeParam);
            ScriptType scriptType = ScriptType.parse(scriptTypeParam);

            final String attachmentName = requestParams.getStringParameter("attachmentName", null);
            if (attachmentName != null) {
                String fileName = attachmentName.indexOf(".") > 0 ? attachmentName : attachmentName + "." + outputTypeParam;
                setResponseHeaders(getMimeType(fileName), fileName);
            }


            Long width = requestParams.getLongParameter("width", 0L);
            Long height = requestParams.getLongParameter("height", 0L);
            logger.debug("Starting:" + new Date().getTime());
            
            try {
                Chart chart = evaluateChart(params, scriptName, scriptType, width, height);
                getOutput(chart, outputType, out);
                logger.debug("Image exported:" + new Date().getTime());
            } catch (Exception e) {
                logger.error(e);
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }
    }

    /**
     * @param params
     * @param scriptName
     * @param scriptType
     * @param width
     * @param height
     * @return
     * @throws FileNotFoundException 
     */
    private Chart evaluateChart(HashMap<String, Object> params, String scriptName, ScriptType scriptType, Long width, Long height) {
      ScriptFactory factory = ScriptFactory.getInstance();
      factory.setSystemPath(getPluginPath()  + "/libs/");
      
      try {
          Script script = factory.createScript(scriptName, scriptType, width, height);
      
          logger.debug("Script created:" + new Date().getTime());
          Chart chart = script.execute(params);
          logger.debug("Script executed:" + new Date().getTime());
          return chart;
      } catch (FileNotFoundException e) {
          logger.error(e);
          return null;
      }   
    }

    private void getOutput(Chart chart, OutputType outputType, OutputStream out) {


        switch (outputType) {
            case PDF:
                /*
                if (chart instanceof pt.webdetails.cgg.charts.SVGChart) {
                chart.toPDF(out);
                }
                 */
                break;
            case PNG:
                chart.toPNG(out);
                break;
            case SVG:
                setResponseHeaders(MIME_SVG, null);
                if (chart instanceof pt.webdetails.cgg.charts.SVGChart) {
                    ((SVGChart) chart).toSVG(out);
                }
                else {
                  logger.error("Trying to get SVG from non-SVG chart");
                }
                break;
            default:
        }
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void refresh(OutputStream out) {
        ScriptFactory.getInstance().clearCachedScopes();
    }

   
}
