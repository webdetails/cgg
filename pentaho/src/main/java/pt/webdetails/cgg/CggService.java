/*!
 * Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.messages.LocaleHelper;
import pt.webdetails.cpf.utils.CharsetHelper;
import pt.webdetails.cpf.utils.MimeTypes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path( "/cgg/api/services" )
public class CggService {

  private static final String CCC_VERSION_PARAM = "cccVersion";
  private static final String CCC_MULTICHART_OVERFLOW_PARAM = "multiChartOverflow";
  private static final String CCC_MULTICHART_OVERFLOW_PAGE = "page";
  private static final String CONTEXT_PATH_PARAM = "CONTEXT_PATH";

  private static final Log logger = LogFactory.getLog( CggService.class );
  private OutputStream outputStream;


  public void setOutputStream( final OutputStream stream ) {
    outputStream = stream;
  }

  @GET
  @Path( "/refresh" )
  @Produces( "text/plain" )
  @Consumes( { APPLICATION_XML, APPLICATION_JSON } )
  public Response refresh() {
    WebCgg cgg = new WebCgg( null, null, null, null );
    cgg.refresh();
    return Response.ok().build();
  }

  @GET
  @Path( "/draw" )
  @Consumes( { APPLICATION_XML, APPLICATION_JSON } )
  public Response draw(
    @QueryParam( "script" ) String script,
    @DefaultValue( "svg" ) @QueryParam( "type" ) String type,
    @DefaultValue( "png" ) @QueryParam( "outputType" ) String outputType,
    @DefaultValue( "" ) @QueryParam( "attachmentName" ) String attachmentName,
    @DefaultValue( "grow" ) @QueryParam( "multiChartOverflow" ) String multiChartOverflow,
    @DefaultValue( "0" ) @QueryParam( "width" ) Long width,
    @DefaultValue( "0" ) @QueryParam( "height" ) Long height,
    @Context HttpServletResponse servletResponse, @Context HttpServletRequest servletRequest ) {

    this.draw( script, type, outputType, attachmentName, multiChartOverflow, null,
      width, height, servletResponse, servletRequest, null );

    Response.Status responseStatus = Response.Status.fromStatusCode( servletResponse.getStatus() );

    if ( responseStatus != null ) {
      return Response.status( responseStatus ).build();
    } else {
      return Response.serverError().build();
    }
  }

  public void draw( String script,
                    String type,
                    String outputType,
                    String attachmentName,
                    Long width,
                    Long height,
                    HttpServletResponse servletResponse,
                    HttpServletRequest servletRequest ) {

    this.draw( script, type, outputType, attachmentName, null, null,
      width, height, servletResponse, servletRequest, null );
  }

  public void draw( String script,
                    String type,
                    final String outputType,
                    final String attachmentName,
                    String multiChartOverflow,
                    String cccLibVersion,
                    Long width,
                    Long height,
                    final HttpServletResponse servletResponse,
                    HttpServletRequest servletRequest,
                    IPentahoSession userSession ) {
    try {

      HashMap<String, Object> params = new HashMap<String, Object>();
      @SuppressWarnings( "unchecked" )


      Enumeration<String> inputParams;
      if ( servletRequest != null ) {
        inputParams = servletRequest.getParameterNames();
        while ( inputParams.hasMoreElements() ) {
          String paramName = inputParams.nextElement();
          if ( paramName.startsWith( "param" ) ) {
            String pName = paramName.substring( 5 );

            String[] p = servletRequest.getParameterValues( paramName );
            if ( p.length == 1 ) { // not *really* an array, is it?
              params.put( pName, p[ 0 ] );
            } else {
              params.put( pName, p );
            }
          }
        }
      }

      if ( !StringUtils.isEmpty( multiChartOverflow ) ) {
        multiChartOverflow = multiChartOverflow.toLowerCase();
        params.put( CCC_MULTICHART_OVERFLOW_PARAM, multiChartOverflow );
      }

      final boolean isMultiPage = CCC_MULTICHART_OVERFLOW_PAGE.equals( multiChartOverflow );

      if ( !StringUtils.isEmpty( cccLibVersion ) ) {
        params.put( CCC_VERSION_PARAM, cccLibVersion );
      }

      // e.g. "http://my-server:8080/pentaho/"
      String serverURL = PentahoSystem.getApplicationContext().getFullyQualifiedServerURL();
      // e.g. "/pentaho/"
      params.put( CONTEXT_PATH_PARAM, new URL( serverURL ).getPath() );

      // Ensure script begins with /
      if ( !script.startsWith( "/" ) ) {
        script = "/" + script;
      }

      String replacedScript = StringUtils.replace( script, "\\", "/" );
      File f = new File( replacedScript );

      URL context = new URL( "file", "", StringUtils.replace( replacedScript, f.getName(), "" ) );

      if ( servletResponse != null ) {
        servletResponse.setCharacterEncoding( CharsetHelper.getEncoding() );
      }

      if ( userSession == null ) {
        userSession = PentahoSessionHolder.getSession();
      }

      Locale locale = LocaleHelper.getLocale();

      final WebCgg cgg = new WebCgg(
        context,
        servletResponse,
        userSession,
        servletResponse == null ? outputStream : servletResponse.getOutputStream(),
        new SetResponseHeaderDelegate() {
          @Override
          public void setResponseHeader( String mimeType ) {
            if ( !attachmentName.isEmpty() ) {
              String fileName = attachmentName.indexOf( "." ) > 0 ? attachmentName : attachmentName + "." + outputType;
              setResponseHeaders( MimeTypes.getMimeType( fileName ), fileName, servletResponse );
            } else if ( isMultiPage ) {
              // setting the Content-Type with the mime type "multipart/mixed" isn't enough,
              // the client also needs the boundary property to be able to split the parts
              setResponseHeaders( "multipart/mixed", servletResponse );
            } else {
              setResponseHeaders( mimeType, servletResponse );
            }
          }
        }
      );

      cgg.draw( new DrawParameters(
        replacedScript,
        type,
        outputType,
        width.intValue(),
        height.intValue(),
        isMultiPage,
        locale,
        params ) );

    } catch ( Exception ex ) {
      logger.fatal( "Error while rendering script", ex );
    }
  }


  protected void setResponseHeaders( final String mimeType, final HttpServletResponse response ) {
    setResponseHeaders( mimeType, 0, null, response );
  }

  protected void setResponseHeaders( final String mimeType, final String attachmentName,
                                     final HttpServletResponse response ) {
    setResponseHeaders( mimeType, 0, attachmentName, response );
  }

  protected void setResponseHeaders( final String mimeType, final int cacheDuration, final String attachmentName,
                                     final HttpServletResponse response ) {
    // Make sure we have the correct mime type


    if ( response == null ) {
      logger.warn( "Parameter 'httpresponse' not found!" );
      return;
    }

    response.setHeader( "Content-Type", mimeType );

    if ( attachmentName != null ) {
      response.setHeader( "content-disposition", "attachment; filename=" + attachmentName );
    } // Cache?

    if ( cacheDuration > 0 ) {
      response.setHeader( "Cache-Control", "max-age=" + cacheDuration );
    } else {
      response.setHeader( "Cache-Control", "max-age=0, no-store" );
    }
  }
}
