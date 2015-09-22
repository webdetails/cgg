/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package pt.webdetails.cgg;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;

import pt.webdetails.cgg.datasources.WebDataSourceFactory;
import pt.webdetails.cgg.output.OutputHandler;
import pt.webdetails.cgg.scripts.DefaultScriptFactory;

public class WebCgg extends AbstractCgg {
  private HttpServletResponse servletResponse;
  private OutputStream out;
  private SetResponseHeaderDelegate delegate;

  public WebCgg( final URL context, final HttpServletResponse servletResponse, final OutputStream out,
      final SetResponseHeaderDelegate delegate ) {
    this.servletResponse = servletResponse; this.out = out; this.delegate = delegate;
    setDataSourceFactory( new WebDataSourceFactory() ); setScriptFactory( new DefaultScriptFactory( context ) );
  }

  protected void produceOutput( final Chart chart, final String requestedOutputHandler )
      throws IOException, ScriptExecuteException {
    OutputHandler cggOutputHandler = getOutputFactory().create( requestedOutputHandler );
    servletResponse.setContentType( cggOutputHandler.getMimeType() );
    servletResponse.setHeader( "Cache-Control", "max-age=0, no-store" );

    delegate.setResponseHeader( cggOutputHandler.getMimeType() );

    cggOutputHandler.render( out, chart );
  }
}
