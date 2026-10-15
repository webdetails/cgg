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


package pt.webdetails.cgg.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import pt.webdetails.cgg.ScriptExecuteException;

public abstract class AbstractScriptFactory implements ScriptFactory {
  private static final Log logger = LogFactory.getLog( AbstractScriptFactory.class );
  private ContextFactory contextFactory;
  private HashMap<ScriptType, BaseScope> contexts;

  protected AbstractScriptFactory() {
    contextFactory = new ContextFactory();
    contexts = new HashMap<>();
  }

  protected abstract ScriptResourceLoader getResourceLoader();

  public Reader getSystemLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    return getResourceLoader().getSystemLibraryScript( script );
  }

  public Reader getContextLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    return getResourceLoader().getContextLibraryScript( script );
  }

  public String getContextResourceURI( final String script ) throws IOException, ScriptResourceNotFoundException {
    return getResourceLoader().getContextResourceURI( script );
  }

  public InputStream getContextResource( final String script ) throws IOException, ScriptResourceNotFoundException {
    return getResourceLoader().getContextResource( script );
  }

  public InputStream getWebResource( final String script ) throws IOException, ScriptResourceNotFoundException {
    return getResourceLoader().getWebResource( script );
  }

  public Script createScript( final String path, final String scriptType, final boolean isMultiPage )
    throws ScriptExecuteException {
    ScriptType st = ScriptType.valueOf( scriptType.toUpperCase( Locale.ENGLISH ) );
    return createScript( path, st, isMultiPage );
  }

  public Script createScript( final String path, final ScriptType scriptType, final boolean isMultiPage )
    throws ScriptExecuteException {
    final Script script;
    switch ( scriptType ) {
      case SVG:
        script = new SvgScript( path, isMultiPage );
        break;
      case J2D:
        script = new Java2DScript( path, isMultiPage );
        break;
      default:
        throw new IllegalArgumentException();
    }
    script.setScope( getScope( scriptType ) );
    return script;
  }

  public void enterContext() {
    Context cx = contextFactory.enterContext();

    cx.setGeneratingDebug( false );
    cx.setOptimizationLevel( -1 );
    cx.setLanguageVersion( Context.VERSION_ES6 );
  }

  public void exitContext() {
    Context.exit();
  }

  protected BaseScope getScope( final ScriptType type ) throws ScriptExecuteException {
    BaseScope o = contexts.get( type );
    if ( o != null ) {
      return o;
    }

    o = createScope( type );
    contexts.put( type, o );
    return o;
  }

  protected BaseScope createScope( final ScriptType type ) throws ScriptExecuteException {
    final String[] dependencies = computeDependencies( type );

    final Context cx = contextFactory.enterContext();
    cx.setGeneratingDebug( false );
    cx.setOptimizationLevel( -1 );

    final BaseScope scope = new BaseScope();
    scope.setScriptFactory( this );
    scope.init( cx );
    for ( final String file : dependencies ) {
      try {
        scope.loadSystemScript( cx, file );
      } catch ( ScriptResourceNotFoundException ex ) {
        logger.error( "Failed to read " + file + ": " + ex.toString() );
      } catch ( IOException e ) {
        throw new ScriptExecuteException( e );
      }
    }
    Context.exit();
    return scope;
  }

  protected String[] computeDependencies( final ScriptType type ) {
    return new String[ 0 ];
  }

  public void clearCachedScopes() {
  }
}
