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

package pt.webdetails.cgg;

import pt.webdetails.cgg.datasources.DataSourceFactory;
import pt.webdetails.cgg.datasources.DefaultDataSourceFactory;
import pt.webdetails.cgg.output.DefaultOutputFactory;
import pt.webdetails.cgg.output.OutputFactory;
import pt.webdetails.cgg.scripts.DefaultScriptFactory;
import pt.webdetails.cgg.scripts.Script;
import pt.webdetails.cgg.scripts.ScriptFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractCgg {
  private ScriptFactory scriptFactory;
  private OutputFactory outputFactory;
  private DataSourceFactory dataSourceFactory;

  public AbstractCgg() {
    this.scriptFactory = new DefaultScriptFactory();
    this.outputFactory = DefaultOutputFactory.getInstance();
    this.dataSourceFactory = DefaultDataSourceFactory.getInstance();
  }

  public synchronized void draw( final String scriptFile,
                                 final String scriptType,
                                 final String outputType,
                                 final int width,
                                 final int height,
                                 final Map<String, Object> genericParamMap )
    throws ScriptCreationException, FileNotFoundException, ScriptExecuteException {
    draw( new DrawParameters( scriptFile, scriptType, outputType, width, height, genericParamMap ) );
  }

  public synchronized void draw( final String scriptFile,
                                 final String scriptType,
                                 final String outputType,
                                 final int width,
                                 final int height,
                                 final boolean isMultiPage,
                                 final Map<String, Object> genericParamMap )
    throws ScriptCreationException, FileNotFoundException, ScriptExecuteException {
    draw( new DrawParameters( scriptFile, scriptType, outputType, width, height, isMultiPage, genericParamMap ) );
  }

  public synchronized void draw( final DrawParameters parameters )
    throws ScriptCreationException, FileNotFoundException, ScriptExecuteException {

    Objects.requireNonNull( parameters );

    try {
      final ScriptFactory factory = getScriptFactory();
      factory.enterContext();
      final Script script =
        factory.createScript( parameters.getScriptFile(), parameters.getScriptType(), parameters.isMultiPage() );

      script.configure( parameters.getWidth(), parameters.getHeight(), getDataSourceFactory(), factory );
      script.configureLocale( parameters.getLocale() );

      final Chart chart = script.execute( parameters.getGenericParameters() );
      produceOutput( chart, parameters.getOutputType() );
      factory.exitContext();
    } catch ( ScriptCreationException e ) {
      throw e;
    } catch ( FileNotFoundException se ) {
      throw se;
    } catch ( ScriptExecuteException se ) {
      throw se;
    } catch ( Exception e ) {
      throw new ScriptExecuteException( e );
    }
  }

  public ScriptFactory getScriptFactory() {
    return scriptFactory;
  }

  public void setScriptFactory( final ScriptFactory scriptFactory ) {
    if ( scriptFactory == null ) {
      throw new NullPointerException();
    }
    this.scriptFactory = scriptFactory;
  }

  public OutputFactory getOutputFactory() {
    return outputFactory;
  }

  public void setOutputFactory( final OutputFactory outputFactory ) {
    if ( outputFactory == null ) {
      throw new NullPointerException();
    }
    this.outputFactory = outputFactory;
  }

  public DataSourceFactory getDataSourceFactory() {
    return dataSourceFactory;
  }

  public void setDataSourceFactory( final DataSourceFactory dataSourceFactory ) {
    if ( dataSourceFactory == null ) {
      throw new NullPointerException();
    }
    this.dataSourceFactory = dataSourceFactory;
  }

  protected abstract void produceOutput( final Chart chart,
                                         final String requestedOutputHandler )
    throws IOException, ScriptExecuteException;

  public synchronized void refresh() {
    getScriptFactory().clearCachedScopes();
  }
}
