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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cgg.datasources.DataSourceFactory;
import pt.webdetails.cgg.output.OutputFactory;
import pt.webdetails.cgg.output.OutputHandler;
import pt.webdetails.cgg.scripts.Script;
import pt.webdetails.cgg.scripts.ScriptFactory;

public abstract class AbstractCgg
{
  private static final Log logger = LogFactory.getLog(AbstractCgg.class);

  private ScriptFactory scriptFactory;
  private OutputFactory outputFactory;
  private DataSourceFactory dataSourceFactory;

  public AbstractCgg()
  {
  }

  protected DataSourceFactory getDataSourceFactory()
  {
    if (dataSourceFactory == null)
    {
      dataSourceFactory = createDataSourceFactory();
    }
    return dataSourceFactory;
  }

  protected abstract DataSourceFactory createDataSourceFactory();

  protected OutputFactory getOutputFactory()
  {
    if (outputFactory == null)
    {
      outputFactory = createOutputFactory();
    }
    return outputFactory;
  }

  protected abstract OutputFactory createOutputFactory();

  protected ScriptFactory getScriptFactory()
  {
    if (scriptFactory == null)
    {
      scriptFactory = createScriptFactory();
    }
    return scriptFactory;
  }

  protected abstract ScriptFactory createScriptFactory();

  public synchronized void draw(final String scriptFile,
                                final String scriptType,
                                final String outputType,
                                final int width,
                                final int height,
                                final Map<String, Object> params)
      throws ScriptCreationException, FileNotFoundException, ScriptExecuteException
  {
    long start = System.currentTimeMillis();
    try
    {
      logger.info("Rendering Script \"" + scriptFile + "\" of type \"" + scriptType + "\" into \"" + outputType + "\"");

      final ScriptFactory factory = getScriptFactory();
      final Script script = factory.createScript(scriptFile, scriptType);
      script.configure(width, height, getDataSourceFactory(), factory);
      long end = System.currentTimeMillis();
      logger.debug("Time to create Script: " + (end - start));
      
      start = System.currentTimeMillis();
      final Chart chart = script.execute(params);
      end = System.currentTimeMillis();
      logger.debug("Time to execute Script: " + (end - start));

      start = System.currentTimeMillis();      
      final OutputHandler outputHandler = getOutputFactory().create(outputType);
      produceOutput(chart, outputHandler);
      end = System.currentTimeMillis();
      logger.debug("Time to export Image: " + (end - start));
    }
    catch (ScriptCreationException se)
    {
      throw se;
    }
    catch (FileNotFoundException fe)
    {
      throw fe;
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

  protected abstract void produceOutput(final Chart chart, final OutputHandler outputHandler) throws IOException;

  public synchronized void refresh()
  {
    getScriptFactory().clearCachedScopes();
  }
}
