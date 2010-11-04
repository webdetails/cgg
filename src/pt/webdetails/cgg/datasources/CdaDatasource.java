/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.datasources;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IContentGenerator;
import org.pentaho.platform.api.engine.IParameterProvider;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.solution.SimpleParameterProvider;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;

/**
 *
 * @author pdpi
 */
public class CdaDatasource implements Datasource {

    private Map<String, Object> paramsMap = new HashMap<String, Object>();
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
        IParameterProvider params = new SimpleParameterProvider(paramsMap);
        try {
            Method setSession = cda.getClass().getMethod("setSession", IPentahoSession.class);
            setSession.invoke(cda, userSession);
            Method doQuery = cda.getClass().getMethod("doQuery", IParameterProvider.class, OutputStream.class);
            doQuery.invoke(cda, params, outputStream);
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
        paramsMap.put("param" + param, val);
    }

    public void setParameter(String param, Date val) {
        paramsMap.put("param" + param, val);
    }

    public void setParameter(String param, List val) {
        paramsMap.put("param" + param, val.toArray());
    }

    public void setDataAccessId(String id) {
        paramsMap.put("dataAccessId", id);
    }

    public void setDefinitionFile(String file) {
        paramsMap.put("path", file);
    }
}
