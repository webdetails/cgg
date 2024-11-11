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

package pt.webdetails.cgg.output;

import java.io.IOException;
import java.io.OutputStream;

import pt.webdetails.cgg.Chart;

public class PngOutputHandler implements OutputHandler {
  public static final String MIME_PNG = "image/png";

  public PngOutputHandler() {
  }

  public void render( final OutputStream out, Chart chart ) throws IOException {
    chart.renderAsPng( out );
  }

  public String getMimeType() {
    return MIME_PNG;
  }
}
