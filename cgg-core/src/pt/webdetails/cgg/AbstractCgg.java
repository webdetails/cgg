/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
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
      
      final ScriptFactory factory = getScriptFactory();
      final Script script = factory.createScript(scriptFile, scriptType);
      script.configure(width, height, getDataSourceFactory(), factory);
      long end = System.currentTimeMillis();
      logger.debug("Time after Script created:" + (start - end));
      
      start = System.currentTimeMillis();
      final Chart chart = script.execute(params);
      end = System.currentTimeMillis();
      logger.debug("Time after Script executed:" + (start-end));

      start = System.currentTimeMillis();      
      final OutputHandler outputHandler = getOutputFactory().create(outputType);
      produceOutput(chart, outputHandler);
      end = System.currentTimeMillis();
      logger.debug("Time after Image exported:" + (start-end));
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
