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

package pt.webdetails.cgg.scripts;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import pt.webdetails.cgg.datasources.DataSourceFactory;


/**
 * @author pdpi
 */
public abstract class BaseScript implements Script
{
  private static final Log logger = LogFactory.getLog(BaseScript.class);
  private String source;
  private Scriptable scope;
  private DataSourceFactory dataSourceFactory;
  private ScriptFactory scriptFactory;

  protected BaseScript(final String source)
  {
    this.source = source;
  }

  public void configure(final int width,
                        final int height,
                        final DataSourceFactory dataSourceFactory,
                        final ScriptFactory scriptFactory)
  {
    this.dataSourceFactory = dataSourceFactory;
    this.scriptFactory = scriptFactory;
    initializeObjects(dataSourceFactory);
  }

  public Scriptable getScope()
  {
    return scope;
  }

  public void initializeObjects(final DataSourceFactory dataSourceFactory)
  {
    ContextFactory.getGlobal().enter();
    final Object wrappedFactory = Context.javaToJS(dataSourceFactory, scope);
    ScriptableObject.putProperty(scope, "datasourceFactory", wrappedFactory);
  }

  public void setScope(final Scriptable scope)
  {
    this.scope = scope;
    initializeObjects(dataSourceFactory);
  }

  protected void executeScript(final Map<String, Object> params)
  {
    final Context cx = Context.getCurrentContext();
    // env.js has methods that pass the 64k Java limit, so we can't compile
    // to bytecode. Interpreter mode to the rescue!
    cx.setOptimizationLevel(-1);

    final Object wrappedParams;
    if (params != null)
    {
      wrappedParams = Context.javaToJS(params, scope);
    }
    else
    {
      wrappedParams = Context.javaToJS(new HashMap<String, Object>(), scope);
    }
    ScriptableObject.defineProperty(scope, "params", wrappedParams, 0);

    try
    {
      final Reader contextLibraryScript = scriptFactory.getContextLibraryScript(source);
      try
      {
        cx.evaluateReader(scope, contextLibraryScript, this.source, 1, null);
      }
      finally
      {
        contextLibraryScript.close();
      }
    }
    catch (IOException ex)
    {
      logger.error("Failed to read " + source + ": " + ex.toString());
    }
  }
}
