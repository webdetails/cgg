/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg;

import pt.webdetails.cgg.charts.Chart;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import pt.webdetails.cgg.datasources.CdaDatasource;
import pt.webdetails.cgg.scripts.ScriptFactory;
import pt.webdetails.cgg.scripts.Script;

/**
 *
 * @author pdpi
 */
public class CggContentGenerator extends BaseContentGenerator {

    private static final Log logger = LogFactory.getLog(CggContentGenerator.class);

    private enum methods {

        DRAW, REFRESH
    }
    private static final String MIME_HTML = "text/html";
    private static final String MIME_SVG = "image/svg+xml";
    private static final String MIME_PNG = "image/png";
    public static final String PLUGIN_NAME = "cgg";

    @Override
    public Log getLogger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void createContent() throws Exception {
        try {
            final IParameterProvider requestParams = parameterProviders.get(IParameterProvider.SCOPE_REQUEST);
            final IParameterProvider pathParams = parameterProviders.get("path");
            OutputStream out = outputHandler.getOutputContentItem("response", "content", "", instanceId, MIME_PNG).getOutputStream(null);
            final String method = pathParams.getStringParameter("path", null);
            try {
                switch (methods.valueOf(method.replaceAll("/", "").toUpperCase())) {
                    case DRAW:
                        draw(requestParams, out);
                        break;
                    case REFRESH:
                        refresh(requestParams, out);
                        break;
                }
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(CggContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception ex) {
            Logger.getLogger(CggContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void draw(final IParameterProvider requestParams, final OutputStream out) {
        try {

            HashMap<String, String> params = new HashMap<String, String>();
            Iterator inputParams = requestParams.getParameterNames();
            while (inputParams.hasNext()) {
                String paramName = inputParams.next().toString();
                if (paramName.startsWith("param")) {
                    params.put(paramName.substring(5), requestParams.getStringParameter(paramName, ""));
                }
            }
            String scriptName = requestParams.getStringParameter("script", "");
            String scriptType = requestParams.getStringParameter("type", "svg");
            Long width = requestParams.getLongParameter("width", 0L);
            Long height = requestParams.getLongParameter("height", 0L);
            logger.debug("Starting:" + new Date().getTime());
            Script script = ScriptFactory.getInstance().createScript(scriptName, scriptType, width, height);
            logger.debug("Script created:" + new Date().getTime());
            Chart chart = script.execute(params);
            logger.debug("Script executed:" + new Date().getTime());
            chart.toPNG(out);
            logger.debug("Image exported:" + new Date().getTime());
        } catch (Exception ex) {
            Logger.getLogger(CggContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refresh(IParameterProvider requestParams, OutputStream out) {
        ScriptFactory.getInstance().clearCachedScopes();
    }

}
