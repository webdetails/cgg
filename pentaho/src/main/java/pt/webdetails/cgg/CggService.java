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

package pt.webdetails.cgg;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.util.messages.LocaleHelper;
import pt.webdetails.cgg.scripts.ScriptResourceNotFoundException;
import pt.webdetails.cpf.utils.CharsetHelper;
import pt.webdetails.cpf.utils.MimeTypes;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_XML;

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

      // Normalize the path to have consumable backslashes and to prevent "escape" from the PS context via relative pathing "../"
      String replacedScript = script == null ? null : Paths.get( script ).normalize().toString().replace( "\\", "/" );
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
      if ( servletResponse != null ) {
        processException( ex, servletResponse );
      }
    }
  }

  private void processException( Exception ex, HttpServletResponse servletResponse ){
      Throwable rootCause = ex;
      while ( rootCause.getCause() != null ) {
        rootCause = rootCause.getCause();
      }

      if ( rootCause instanceof ScriptResourceNotFoundException ) {
        servletResponse.setStatus( 404 );
      } else {
        servletResponse.setStatus( 400 );
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
