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
