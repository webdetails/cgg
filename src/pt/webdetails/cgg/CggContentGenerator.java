/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import pt.webdetails.cgg.charts.Chart;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import org.w3c.dom.Document;
import pt.webdetails.cgg.scripts.ScriptFactory;
import pt.webdetails.cgg.scripts.Script;
import pt.webdetails.cgg.charts.SVGChart;

/**
 *
 * @author pdpi
 */
public class CggContentGenerator extends BaseContentGenerator {

    public static final String CDW_EXTENSION = ".cdw";
    public static final String PLUGIN_NAME = "cgg";
    public static final String PLUGIN_PATH = "system/" + CggContentGenerator.PLUGIN_NAME + "/";
    private static final Log logger = LogFactory.getLog(CggContentGenerator.class);
    private static final String MIME_XML = "text/xml";
    private static final String MIME_HTML = "text/html";
    private static final String MIME_SVG = "image/svg+xml";
    private static final String MIME_PNG = "image/png";

    private enum methods {

        DRAW, REFRESH, DRAWCDW, LISTCDW, GETCDW
    }

    private enum outputTypes {

        SVG, PNG, PDF
    }

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
                    default:
                        logger.error("No method passed to content generator");
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

            HashMap<String, Object> params = new HashMap<String, Object>();
            Iterator inputParams = requestParams.getParameterNames();
            while (inputParams.hasNext()) {
                String paramName = inputParams.next().toString();
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

            IPentahoSession session = PentahoSessionHolder.getSession();
            final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);
            // Get the paths ot the necessary files: dependencies and the main script.
            ISolutionFile systemPath = solutionRepository.getSolutionFile(PLUGIN_PATH, 0);
            String solutionRoot = PentahoSystem.getApplicationContext().getSolutionRootPath();
            String scriptName = requestParams.getStringParameter("script", "");
            String scriptType = requestParams.getStringParameter("type", "svg");
            String outputType = requestParams.getStringParameter("outputType", "png");

            String multiChartOverflow = requestParams.getStringParameter("multiChartOverflow", "");
            if(multiChartOverflow != null && !multiChartOverflow.isEmpty()) {
              params.put("multiChartOverflow", multiChartOverflow);
            }
            
            final String attachmentName = requestParams.getStringParameter("attachmentName", null);
            if (attachmentName != null) {
                final HttpServletResponse response = (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");
                String filename = attachmentName.indexOf(".") > 0 ? attachmentName : attachmentName + "." + outputType;
                response.setHeader("content-disposition", "attachment; filename=" + filename);
            }


            Long width = requestParams.getLongParameter("width", 0L);
            Long height = requestParams.getLongParameter("height", 0L);
            logger.debug("Starting:" + new Date().getTime());
            try {
                ScriptFactory factory = ScriptFactory.getInstance();
                factory.setSystemPath(solutionRoot + "/" + systemPath.getSolutionPath() + "/libs/");
                Script script = factory.createScript(scriptName, scriptType, width, height);
                logger.debug("Script created:" + new Date().getTime());
                Chart chart = script.execute(params);
                logger.debug("Script executed:" + new Date().getTime());
                getOutput(chart, outputTypes.valueOf(outputType.toUpperCase()), out);
                logger.debug("Image exported:" + new Date().getTime());
            } catch (Exception e) {
                logger.error(e);
            }
        } catch (Exception ex) {
            Logger.getLogger(CggContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getOutput(Chart chart, outputTypes outputType, OutputStream out) {


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
                break;
            default:
        }
    }


    private void refresh(IParameterProvider requestParams, OutputStream out) {
        ScriptFactory.getInstance().clearCachedScopes();
    }

 

    private void setResponseHeaders(final String mimeType, final String attachmentName) {
        // Make sure we have the correct mime type
        final HttpServletResponse response = (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");
        response.setHeader("Content-Type", mimeType);
        if (attachmentName != null) {
            response.setHeader("content-disposition", "attachment; filename=" + attachmentName);
        } // We can't cache this requests
        response.setHeader("Cache-Control", "max-age=0, no-store");
    }
}
