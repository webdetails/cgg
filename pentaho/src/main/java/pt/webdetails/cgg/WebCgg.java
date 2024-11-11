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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;

import pt.webdetails.cgg.datasources.WebDataSourceFactory;
import pt.webdetails.cgg.output.OutputHandler;
import pt.webdetails.cgg.scripts.JCRScriptFactory;

import org.pentaho.platform.api.engine.IPentahoSession;


public class WebCgg extends AbstractCgg {

  private HttpServletResponse servletResponse;
  private OutputStream out;
  private SetResponseHeaderDelegate delegate;

  public WebCgg( final URL context,
                 final HttpServletResponse servletResponse,
                 final IPentahoSession userSession,
                 final OutputStream out,
                 final SetResponseHeaderDelegate delegate ) {
    this.servletResponse = servletResponse;
    this.out = out;
    this.delegate = delegate;
    setDataSourceFactory( new WebDataSourceFactory() );
    setScriptFactory( new JCRScriptFactory( context.getPath(), userSession.getName() ) );
  }

  public WebCgg( final URL context,
                 final HttpServletResponse servletResponse,
                 final OutputStream out,
                 final SetResponseHeaderDelegate delegate ) {
    this( context, servletResponse, null, out, delegate );
  }

  protected void produceOutput( final Chart chart,
                                final String requestedOutputHandler ) throws IOException, ScriptExecuteException {
    OutputHandler cggOutputHandler = getOutputFactory().create( requestedOutputHandler );
    if ( servletResponse != null ) {
      servletResponse.setContentType( cggOutputHandler.getMimeType() );
      servletResponse.setHeader( "Cache-Control", "max-age=0, no-store" );
    }

    delegate.setResponseHeader( cggOutputHandler.getMimeType() );

    cggOutputHandler.render( out, chart );
  }


  public void refresh() {
    getScriptFactory().clearCachedScopes();
  }
}
