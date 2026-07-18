/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package pt.webdetails.cgg.datasources;


public class WebDataSourceFactory implements DataSourceFactory
{
  public WebDataSourceFactory()
  {
  }

  public DataSource createDatasource( final String type )
  {
    if ( "CDA".equalsIgnoreCase(type) )
    {
      return new CdaDatasource();
    }
    return null;
  }
}
