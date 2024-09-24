/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
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
