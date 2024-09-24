/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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


import java.util.Date;
import java.util.List;

public class CdaDatasource implements DataSource {

  private pt.webdetails.cpf.datasources.CdaDatasource wrappedSource;

  public CdaDatasource() {
    wrappedSource = new pt.webdetails.cpf.datasources.CdaDatasource();
  }

  private String getQueryData() {
    return wrappedSource.execute();
  }

  public String execute() {
    return getQueryData();
  }

  public void setParameter( String param, Object val ) {
    if ( val instanceof String ) {
      wrappedSource.setParameter( param, (String) val );
    } else if ( val instanceof List ) {
      wrappedSource.setParameter( param, (List<Object>) val );
    } else if ( val instanceof Date ) {
      wrappedSource.setParameter( param, (Date) val );
    } else if ( val instanceof String[] ) {
      wrappedSource.setParameter( param, (String[]) val );
    }

  }

  public void setDataAccessId( String id ) {
    wrappedSource.setDataAccessId( id );
  }

  public void setDefinitionFile( String file ) {
    wrappedSource.setDefinitionFile( file );
  }
}
