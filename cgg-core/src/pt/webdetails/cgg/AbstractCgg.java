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

package pt.webdetails.cgg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import pt.webdetails.cgg.datasources.DataSourceFactory;
import pt.webdetails.cgg.datasources.DefaultDataSourceFactory;
import pt.webdetails.cgg.output.DefaultOutputFactory;
import pt.webdetails.cgg.output.OutputFactory;
import pt.webdetails.cgg.scripts.DefaultScriptFactory;
import pt.webdetails.cgg.scripts.Script;
import pt.webdetails.cgg.scripts.ScriptFactory;

public abstract class AbstractCgg
{
  private ScriptFactory scriptFactory;
  private OutputFactory outputFactory;
  private DataSourceFactory dataSourceFactory;

  public AbstractCgg()
  {
    this.scriptFactory = new DefaultScriptFactory();
    this.outputFactory = DefaultOutputFactory.getInstance();
    this.dataSourceFactory = DefaultDataSourceFactory.getInstance();
  }

  public synchronized void draw(final String scriptFile,
                                final String scriptType,
                                final String outputType,
                                final int width,
                                final int height,
                                final Map<String, Object> params)
      throws ScriptCreationException, FileNotFoundException, ScriptExecuteException
  {
    try
    {
      final ScriptFactory factory = getScriptFactory();
      factory.enterContext();
      final Script script = factory.createScript(scriptFile, scriptType);
      script.configure(width, height, getDataSourceFactory(), factory);
      final Chart chart = script.execute(params);
      produceOutput(chart, outputType);
      factory.exitContext();
    }
    catch (ScriptCreationException e)
    {
      throw e;
    }
    catch (FileNotFoundException se)
    {
      throw se;
    }
    catch (ScriptExecuteException se)
    {
      throw se;
    }
    catch (Exception e)
    {
      throw new ScriptExecuteException(e);
    }
  }

  public ScriptFactory getScriptFactory()
  {
    return scriptFactory;
  }

  public void setScriptFactory(final ScriptFactory scriptFactory)
  {
    if (scriptFactory == null)
    {
      throw new NullPointerException();
    }
    this.scriptFactory = scriptFactory;
  }

  public OutputFactory getOutputFactory()
  {
    return outputFactory;
  }

  public void setOutputFactory(final OutputFactory outputFactory)
  {
    if (outputFactory == null)
    {
      throw new NullPointerException();
    }
    this.outputFactory = outputFactory;
  }

  public DataSourceFactory getDataSourceFactory()
  {
    return dataSourceFactory;
  }

  public void setDataSourceFactory(final DataSourceFactory dataSourceFactory)
  {
    if (dataSourceFactory == null)
    {
      throw new NullPointerException();
    }
    this.dataSourceFactory = dataSourceFactory;
  }

  protected abstract void produceOutput(final Chart chart,
                                        final String requestedOutputHandler) throws IOException, ScriptExecuteException;

  public synchronized void refresh()
  {
    getScriptFactory().clearCachedScopes();
  }
}
