/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg.datasources;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cpf.InterPluginCall;

/**
 *
 * @author pdpi
 */
public class CdaDatasource implements Datasource {

    private Map<String, Object> requestMap = new HashMap<String, Object>();
    private static final Log logger = LogFactory.getLog(CdaDatasource.class);

    public CdaDatasource() {
    }

//    private String getQueryData() {
//
//        IPentahoSession userSession = PentahoSessionHolder.getSession();
//        IPluginManager pluginManager = PentahoSystem.get(IPluginManager.class, userSession);
//        IContentGenerator cda;
//        try {
//            cda = pluginManager.getContentGenerator("cda", userSession);
//        } catch (Exception e) {
//            logger.error("Failed to acquire CDA plugin to query");
//            return null;
//        }
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        IOutputHandler outputHandler = new SimpleOutputHandler(outputStream, false);
//        IParameterProvider requestParams = new SimpleParameterProvider(requestMap);
//        Map<String,Object> pathMap = new HashMap<String,Object>();
//        pathMap.put("path", "/doQuery");
//        IParameterProvider pathParams = new SimpleParameterProvider(pathMap);
//        Map<String, IParameterProvider> paramProvider = new HashMap<String, IParameterProvider>();
//        paramProvider.put(IParameterProvider.SCOPE_REQUEST,requestParams);
//        paramProvider.put("path",pathParams);
//
//        try {
//            cda.setSession(userSession);
//            cda.setOutputHandler(outputHandler);
//            cda.setParameterProviders(paramProvider);
//            cda.createContent();
//            return outputStream.toString();
//        } catch (Exception e) {
//            logger.error("Failed to execute query: " + e.toString());
//            return null;
//        }
//    }
    
    private String getQueryData() {
      InterPluginCall cdaCall = new InterPluginCall(InterPluginCall.CDA, "doQuery");
      cdaCall.setRequestParameters(requestMap);
      return cdaCall.callInPluginClassLoader();
    }

    public String execute() {
        return getQueryData();
    }

    public void setParameter(String param, String val) {
        requestMap.put("param" + param, val);
    }
    public void setParameter(String param, String[] val) {
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
