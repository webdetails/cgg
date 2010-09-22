/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IContentGenerator;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import pt.webdetails.cgg.script.ScriptFactory;
import pt.webdetails.cgg.script.Script;

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
            String scriptType = requestParams.getStringParameter("type", "base");
            logger.warn("Starting:" + new Date().getTime());
            Script script = ScriptFactory.getInstance().createScript(scriptName, scriptType);
            logger.warn("Script created:" + new Date().getTime());
            String svg = script.execute(params);
            logger.warn("Script executed:" + new Date().getTime());
            Chart chart = new Chart(svg);
            chart.toPNG(out);
            logger.warn("Image exported:" + new Date().getTime());
        } catch (Exception ex) {
            Logger.getLogger(CggContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void refresh(IParameterProvider requestParams, OutputStream out) {
        ScriptFactory.getInstance().clearCachedScopes();
    }

    private String getCdaResultSet() throws Exception {
        IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, userSession);
        IContentGenerator cda = pluginManager.getContentGenerator("cda", userSession);
        // If CDA is present, we're going to pull a resultset
        if (cda != null) {
            // Basic setup
            cda.setParameterProviders(parameterProviders);
            cda.setOutputHandler(outputHandler);
            // We need to arrange for a callback object that will serve as a communications channel
            ArrayList<Object> output = new ArrayList<Object>();
            HashMap<String, Object> channel = new HashMap<String, Object>();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            // The outputstream provides CDA with a sink we can later retrieve data from
            channel.put("output", outputStream);
            // Setup the desired function to call on CDA's side of things.
            channel.put("method", "doQuery");
            // Call CDA
            output.add(channel);
            cda.setCallbacks(output);
            cda.createContent();
            // pass the output to the ComponentManager
            return outputStream.toString();
        } else {
            return null;
        }
    }
}
