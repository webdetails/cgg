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

/**
 * Allows to potentially post-process the raw SVG and PNG output. With the help of the reporting engine,
 * you could implement a PDF output handler that handles all font-encodings properly.
 */
public interface OutputHandler
{
  public void render (OutputStream out, Chart chart) throws IOException;
  public String getMimeType();
}
