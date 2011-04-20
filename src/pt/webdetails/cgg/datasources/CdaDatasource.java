/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.datasources;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IContentGenerator;
import org.pentaho.platform.api.engine.IOutputHandler;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.output.SimpleOutputHandler;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;

/**
 *
 * @author pdpi
 */
public class CdaDatasource implements Datasource {

    private Map<String, Object> requestMap = new HashMap<String, Object>();
    private static final Log logger = LogFactory.getLog(CdaDatasource.class);

    public CdaDatasource() {
    }

    private String getQueryData() {

        IPentahoSession userSession = PentahoSessionHolder.getSession();
        IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, userSession);
        IContentGenerator cda;
        try {
            cda = pluginManager.getContentGenerator("cda", userSession);
        } catch (Exception e) {
            logger.error("Failed to acquire CDA plugin to query");
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOutputHandler outputHandler = new SimpleOutputHandler(outputStream, false);
        IParameterProvider requestParams = new SimpleParameterProvider(requestMap);
        Map<String,Object> pathMap = new HashMap<String,Object>();
        pathMap.put("path", "/doQuery");
        IParameterProvider pathParams = new SimpleParameterProvider(pathMap);
        Map<String, IParameterProvider> paramProvider = new HashMap<String, IParameterProvider>();
        paramProvider.put(IParameterProvider.SCOPE_REQUEST,requestParams);
        paramProvider.put("path",pathParams);

        try {
            cda.setSession(userSession);
            cda.setOutputHandler(outputHandler);
            cda.setParameterProviders(paramProvider);
            cda.createContent();
            return outputStream.toString();
        } catch (Exception e) {
            logger.error("Failed to execute query: " + e.toString());
            return null;
        }
    }

    public String execute() {
        return getQueryData();
    }

    public void setParameter(String param, String val) {
        requestMap.put("param" + param, val);
    }

    public void setParameter(String param, Date val) {
        requestMap.put("param" + param, val);
    }

    public void setParameter(String param, List val) {
        requestMap.put("param" + param, val.toArray());
    }

    public void setDataAccessId(String id) {
        requestMap.put("dataAccessId", id);
    }

    public void setDefinitionFile(String file) {
        requestMap.put("path", file);
    }
}
