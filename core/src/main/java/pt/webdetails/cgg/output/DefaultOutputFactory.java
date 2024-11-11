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


package pt.webdetails.cgg.output;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class DefaultOutputFactory implements OutputFactory
{
  private static final DefaultOutputFactory instance = new DefaultOutputFactory();
  private HashMap<String, String> handlerRegistry;
  protected static final Log logger = LogFactory.getLog(DefaultOutputFactory.class);  

  public static DefaultOutputFactory getInstance()
  {
    return instance;
  }

  private DefaultOutputFactory()
  {
    handlerRegistry = new HashMap<String, String>();
  }

  public void register(final String name, final Class<? extends OutputHandler> handler)
  {
    handlerRegistry.put(name, handler.getName());
  }

  public OutputHandler create(final String type)
  {
    final String className = handlerRegistry.get(type);
    if (className == null)
    {
      throw new IllegalArgumentException("No Registered handler for type " + type);
    }
    final OutputHandler outputHandler = getHandler(className);
    if (outputHandler == null)
    {
      throw new IllegalArgumentException();
    }
    return outputHandler;
  }
  
  private OutputHandler getHandler(String type) {

    try {
      return (OutputHandler)Class.forName(type).newInstance();    
    } catch (ClassNotFoundException cnfe) {
      logger.fatal("Class for output handler " + type + " not found.", cnfe);
    } catch (InstantiationException ie) {
      logger.fatal("Instantiaton of class failed", ie);
    } catch (IllegalAccessException iae) {
      logger.fatal("Illegal access to outputhandler class", iae);
    }
    return null;
  }
  
  
}
