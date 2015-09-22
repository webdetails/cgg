/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

package pt.webdetails.cgg.charts;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import pt.webdetails.cpf.utils.CharsetHelper;

/**
 * @author pdpi
 */
public class SVGChart implements Chart {

  private static final Log logger = LogFactory.getLog( SVGChart.class );
  private Document svg;
  //    private String svgSource;
  //
  //    public SVGChart(String svgSource) {
  //        this.svgSource = svgSource;
  //    }

  public SVGChart( Document doc ) {
    this.svg = doc;
  }

  public void toPNG( OutputStream out ) {
    PNGTranscoder t = new PNGTranscoder(); TranscoderInput input = new TranscoderInput( svg );
    TranscoderOutput output = new TranscoderOutput( out ); try {
      t.transcode( input, output ); out.flush();
    } catch ( IOException ex ) {
      logger.error( ex );
    } catch ( TranscoderException ex ) {
      logger.error( ex );
    }
  }

  public void toSVG( OutputStream out ) {
    SVGTranscoder t = new SVGTranscoder(); TranscoderInput input = new TranscoderInput( svg ); TranscoderOutput
        output =
        new TranscoderOutput( new OutputStreamWriter( out, Charset.forName( CharsetHelper.getEncoding() ) ) );

    try {
      t.transcode( input, output ); out.flush();
    } catch ( IOException ex ) {
      logger.error( ex );
    } catch ( TranscoderException ex ) {
      logger.error( ex );
    }
  }
}
