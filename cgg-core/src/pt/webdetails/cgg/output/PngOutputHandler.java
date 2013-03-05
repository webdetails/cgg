/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.output;

import java.io.IOException;
import java.io.OutputStream;

import pt.webdetails.cgg.Chart;

public class PngOutputHandler implements OutputHandler
{
  private static final String MIME_PNG = "image/png";

  public PngOutputHandler()
  {
  }

  public void render(final OutputStream out, Chart chart) throws IOException
  {
    chart.renderAsPng(out);
  }

  public String getMimeType()
  {
    return MIME_PNG;
  }
}
