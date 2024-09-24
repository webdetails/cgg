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
package pt.webdetails.cgg.output;

import java.io.IOException;
import java.io.OutputStream;

import pt.webdetails.cgg.Chart;

public class SVGOutputHandler implements OutputHandler {
  public static final String MIME_SVG = "image/svg+xml";

  public SVGOutputHandler() {
  }

  public void render( final OutputStream out, final Chart chart ) throws IOException {
    chart.renderAsSVG( out );
  }

  public String getMimeType() {
    return MIME_SVG;
  }
}
