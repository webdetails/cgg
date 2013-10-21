/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg.datasources;

import java.util.HashMap;
import java.util.Map;

import pt.webdetails.cpf.InterPluginCall;

/**
 *
 * @author pdpi
 */
public class CdaDatasource implements Datasource {

    private Map<String, Object> requestMap = new HashMap<String, Object>();

    public CdaDatasource() {
    }
    
    private String getQueryData() {
      InterPluginCall cdaCall = new InterPluginCall(InterPluginCall.CDA, "doQueryGet");
      cdaCall.setRequestParameters(requestMap);
      return cdaCall.callInPluginClassLoader();
    }

    public String execute() {
        return getQueryData();
    }
    
    public void setParameter(String param, Object val){
      requestMap.put("param" + param, val);
    }

//    public void setParameter(String param, String val) {
//        requestMap.put("param" + param, val);
//    }
//    public void setParameter(String param, String[] val) {
//        requestMap.put("param" + param, val);
//    }
//    public void setParameter(String param, Date val) {
//        requestMap.put("param" + param, val);
//    }

//    public void setParameter(String param, List val) {
//        requestMap.put("param" + param, val.toArray());
//    }

    public void setDataAccessId(String id) {
        requestMap.put("dataAccessId", id);
    }

    public void setDefinitionFile(String file) {
        requestMap.put("path", file);
    }
}
