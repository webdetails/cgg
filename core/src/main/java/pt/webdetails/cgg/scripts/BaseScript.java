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

package pt.webdetails.cgg.scripts;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import pt.webdetails.cgg.ScriptExecuteException;
import pt.webdetails.cgg.datasources.DataSourceFactory;

/**
 * @author pdpi
 */
public abstract class BaseScript implements Script {
  private static final Log logger = LogFactory.getLog( BaseScript.class );
  private String source;
  private boolean isMultiPage;
  private Locale locale;
  private BaseScope scope;
  private DataSourceFactory dataSourceFactory;
  private ScriptFactory scriptFactory;

  protected BaseScript( final String source, final boolean isMultiPage ) {
    this.source = source;
    this.isMultiPage = isMultiPage;
    this.locale = Locale.getDefault();
  }

  @Override
  public void configure( final int width,
                         final int height,
                         final DataSourceFactory dataSourceFactory,
                         final ScriptFactory scriptFactory ) throws ScriptExecuteException {
    this.dataSourceFactory = dataSourceFactory;
    this.scriptFactory = scriptFactory;
    initializeObjects();
  }

  @Override
  public void configureLocale( Locale locale ) throws ScriptExecuteException {
    Objects.requireNonNull( locale );
    this.locale = locale;
    initializeObjects();
  }

  protected DataSourceFactory getDataSourceFactory() {
    return dataSourceFactory;
  }

  protected ScriptFactory getScriptFactory() {
    return scriptFactory;
  }

  protected Locale getLocale() {
    return locale;
  }

  public BaseScope getScope() {
    return scope;
  }

  public boolean getIsMultiPage() {
    return isMultiPage;
  }

  private void initializeObjects() throws ScriptExecuteException {
    if ( Context.getCurrentContext() == null ) {
      throw new ScriptExecuteException();
    }

    final Object wrappedFactory = Context.javaToJS( dataSourceFactory, scope );
    ScriptableObject.putProperty( scope, "datasourceFactory", wrappedFactory );

    // Locales

    final Object wrappedLocale = Context.javaToJS( locale.toLanguageTag(), scope );
    ScriptableObject.putProperty( scope, "__locale", wrappedLocale );

    ResourceBundle.Control resourceBundleControl =
      ResourceBundle.Control.getControl( ResourceBundle.Control.FORMAT_DEFAULT );

    String[] candidateLocaleTags = resourceBundleControl.getCandidateLocales( "BaseNameIsIgnored", locale )
      .stream()
      // The locale representing the default message bundle is "empty"
      .filter( l -> StringUtils.isNotEmpty( l.getLanguage() ) )
      .map( Locale::toLanguageTag )
      .toArray( String[]::new );

    final Object wrappedCandidateLocales = Context.javaToJS( candidateLocaleTags, scope );
    ScriptableObject.putProperty( scope, "__candidate_locales", wrappedCandidateLocales );
  }

  public void setScope( final BaseScope scope ) {
    this.scope = scope;
  }

  protected void executeScript( final Map<String, Object> params ) throws ScriptExecuteException {
    if ( params == null ) {
      throw new NullPointerException();
    }
    if ( Context.getCurrentContext() == null ) {
      throw new ScriptExecuteException();
    }

    final Context cx = Context.getCurrentContext();
    // env.js has methods that pass the 64k Java limit, so we can't compile
    // to bytecode. Interpreter mode to the rescue!
    cx.setOptimizationLevel( -1 );

    final Object wrappedParams = Context.javaToJS( params, scope );

    ScriptableObject.defineProperty( scope, "params", wrappedParams, 0 );

    try {
      scope.loadScript( cx, source );
    } catch ( ScriptResourceNotFoundException | IOException e ) {
      logger.error( "Failed to read " + source + ": " + e.toString(), e );
      throw new ScriptExecuteException( e );
    }
  }
}
