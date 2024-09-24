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
