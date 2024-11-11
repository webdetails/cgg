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

import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.pentaho.platform.api.engine.IParameterProvider;

import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;

/**
 * @author pdpi
 */
public class CggContentGenerator extends SimpleContentGenerator {

  private static final long serialVersionUID = 1L;

  private static final Log logger = LogFactory.getLog( CggContentGenerator.class );

  private static final String CCC_VERSION_PARAM = "cccVersion";

  @Override
  public String getPluginName() {
    return "cgg";
  }

  @Exposed( accessLevel = AccessLevel.PUBLIC )
  public void Draw( final OutputStream out ) {
    draw( out );
  }


  @Exposed( accessLevel = AccessLevel.PUBLIC )
  public void draw( final OutputStream out ) {

    IParameterProvider requestParams = parameterProviders.get( IParameterProvider.SCOPE_REQUEST );

    String script = requestParams.getStringParameter( "script", "" );
    String type = requestParams.getStringParameter( "type", "svg" );
    String outputType = requestParams.getStringParameter( "outputType", "png" );
    String attachmentName = requestParams.getStringParameter( "attachmentName", "" );
    String multiChartOverflow = requestParams.getStringParameter( "multiChartOverflow", "" );

    String widthAsStr = requestParams.getStringParameter( "width", "0" );
    Long width = 0L;
    try {
      width = Long.parseLong( widthAsStr );
    } catch ( NumberFormatException nfe ) {
    }

    String heightAsStr = requestParams.getStringParameter( "height", "0" );
    Long height = 0L;
    try {
      height = Long.parseLong( heightAsStr );
    } catch ( NumberFormatException nfe ) {
    }

    String cccLibVersion = requestParams.getStringParameter( CCC_VERSION_PARAM, "" );

    HttpServletRequest request = getRequest();
    HttpServletResponse response = getResponse();

    CggService service = new CggService();

    if ( response == null ) {
      service.setOutputStream( out );
    }
    service.draw( script, type, outputType, attachmentName, multiChartOverflow, cccLibVersion,
      height, width, response, request, this.userSession );
  }

  @Exposed( accessLevel = AccessLevel.PUBLIC )
  public void refresh( OutputStream out ) {
  }
}
