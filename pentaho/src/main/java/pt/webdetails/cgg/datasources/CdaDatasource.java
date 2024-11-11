/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
