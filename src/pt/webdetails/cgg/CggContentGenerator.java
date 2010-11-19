/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import pt.webdetails.cgg.charts.Chart;
import java.io.OutputStream;
import java.lang.String;
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
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.services.solution.BaseContentGenerator;
import org.w3c.dom.Document;
import pt.webdetails.cgg.cdw.CdwFileNavigator;
import pt.webdetails.cgg.scripts.ScriptFactory;
import pt.webdetails.cgg.scripts.Script;

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

        DRAW, REFRESH, DRAWCDW, LISTCDW
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
                    case DRAWCDW:
                        drawCdw(requestParams, out);
                        break;
                    case REFRESH:
                        refresh(requestParams, out);
                        break;
                    case LISTCDW:
                        listCdw(requestParams, out);
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

            HashMap<String, String> params = new HashMap<String, String>();
            Iterator inputParams = requestParams.getParameterNames();
            while (inputParams.hasNext()) {
                String paramName = inputParams.next().toString();
                if (paramName.startsWith("param")) {
                    params.put(paramName.substring(5), requestParams.getStringParameter(paramName, ""));
                }
            }

            IPentahoSession session = PentahoSessionHolder.getSession();
            final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);
            // Get the paths ot the necessary files: dependencies and the main script.
            ISolutionFile systemPath = solutionRepository.getSolutionFile(PLUGIN_PATH, 0);
            String solutionRoot = PentahoSystem.getApplicationContext().getSolutionRootPath();
            String scriptName = requestParams.getStringParameter("script", "");
            String scriptType = requestParams.getStringParameter("type", "svg");
            Long width = requestParams.getLongParameter("width", 0L);
            Long height = requestParams.getLongParameter("height", 0L);
            logger.debug("Starting:" + new Date().getTime());
            ScriptFactory factory = ScriptFactory.getInstance();
            factory.setSystemPath(solutionRoot + "/" + systemPath.getSolutionPath() + "/libs/");
            Script script = factory.createScript(scriptName, scriptType, width, height);
            logger.debug("Script created:" + new Date().getTime());
            Chart chart = script.execute(params);
            logger.debug("Script executed:" + new Date().getTime());
            chart.toPNG(out);
            logger.debug("Image exported:" + new Date().getTime());
        } catch (Exception ex) {
            Logger.getLogger(CggContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void drawCdw(IParameterProvider requestParams, OutputStream out) {
        final IPentahoSession session = PentahoSessionHolder.getSession();
        final String scriptName = requestParams.getStringParameter("path", "");
        final String scriptId = requestParams.getStringParameter("id", "");
        final ISolutionRepository solutionRepository = PentahoSystem.get(ISolutionRepository.class, session);

        if (scriptName != null) {
            try {
                ISolutionFile cdw = solutionRepository.getSolutionFile(scriptName, 0);
                if (cdw == null) {
                    throw new FileNotFoundException(scriptName);
                }
                InputStream data = new ByteArrayInputStream(cdw.getData());
                Map<String, String> paramsMap = new HashMap<String, String>();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(data);

                XPath xpath = XPathFactory.newInstance().newXPath();
                String scriptPath;
                if (scriptId.equals("")) {
                    scriptPath = (String) xpath.evaluate("/cdw/charts/chart[1]/script", doc, XPathConstants.STRING);
                } else {
                    scriptPath = (String) xpath.evaluate("/cdw/charts/chart[@id='" + scriptId + "']/script", doc, XPathConstants.STRING);
                }
                paramsMap.put("script", scriptPath);
                paramsMap.put("type", requestParams.getStringParameter("type", "svg"));
                paramsMap.put("width", "" + requestParams.getLongParameter("width", 0L));
                paramsMap.put("width", "" + requestParams.getLongParameter("width", 0L));
                IParameterProvider params = new SimpleParameterProvider(paramsMap);
                draw(params, out);
            } catch (Exception ex) {
                logger.error("Failed to parse CDW file: " + ex.toString());
            }
        }
    }

    private void refresh(IParameterProvider requestParams, OutputStream out) {
        ScriptFactory.getInstance().clearCachedScopes();
    }

    private void listCdw(IParameterProvider requestParams, OutputStream out) {

        final HttpServletResponse response = (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");
        response.setHeader("Content-Type", MIME_XML);

        final String contextPath = ((HttpServletRequest) parameterProviders.get("path").getParameter("httprequest")).getContextPath();
        final CdwFileNavigator nav = new CdwFileNavigator(userSession, contextPath);
        try {
            final String json = nav.getCdwFilelist("navigator", "metrics", "");
            out.write(json.getBytes("UTF8"));
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
