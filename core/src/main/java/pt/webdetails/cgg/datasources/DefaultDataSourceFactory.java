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

import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DefaultDataSourceFactory implements DataSourceFactory
{
  private static final DefaultDataSourceFactory instance = new DefaultDataSourceFactory();
  private HashMap<String, String> handlerRegistry;
  protected static final Log logger = LogFactory.getLog(DefaultDataSourceFactory.class);
  
  public static DefaultDataSourceFactory getInstance()
  {
    return instance;
  }

  private DefaultDataSourceFactory()
  {
    handlerRegistry = new HashMap<String, String>();
  }

  public void register(final String name, final Class<? extends DataSource> handler)
  {
    handlerRegistry.put(name, handler.getName());
  }

  public DataSource createDatasource(final String type)
  {
    final String className = handlerRegistry.get(type);
    if (className == null)
    {
      throw new IllegalArgumentException();
    }
    final DataSource datasource = getSource(className);
    if (datasource == null)
    {
      throw new IllegalArgumentException();
    }
    return datasource;
  }
  
  
  private DataSource getSource(String type) {

    try {
      return (DataSource)Class.forName(type).newInstance();
    } catch (ClassNotFoundException cnfe) {
      logger.fatal("Class for data source " + type + " not found.", cnfe);
    } catch (InstantiationException ie) {
      logger.fatal("Instantiaton of class failed", ie);
    } catch (IllegalAccessException iae) {
      logger.fatal("Illegal access to datasource class", iae);
    }
    return null;
  }
  
}
