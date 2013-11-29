/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package pt.webdetails.cgg.datasources;

import java.util.HashMap;
import java.util.Map;

import pt.webdetails.cpf.InterPluginCall;

/**
 * @author pdpi
 */
public class CdaDatasource implements DataSource {

  private Map<String, Object> requestMap = new HashMap<String, Object>();

  public CdaDatasource() {
  }

  private String getQueryData() {
    InterPluginCall cdaCall = new InterPluginCall( InterPluginCall.CDA, "doQuery" );
    cdaCall.setRequestParameters( requestMap );
    return cdaCall.callInPluginClassLoader();
  }

  public String execute() {
    return getQueryData();
  }

  public void setParameter( String param, Object val ) {
    requestMap.put( "param" + param, val );
  }

  public void setDataAccessId( String id ) {
    requestMap.put( "dataAccessId", id );
  }

  public void setDefinitionFile( String file ) {
    requestMap.put( "path", file );
  }
}
