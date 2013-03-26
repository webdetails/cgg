/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.output;

import java.io.IOException;
import java.io.OutputStream;

import pt.webdetails.cgg.Chart;

public class SVGOutputHandler implements OutputHandler
{
  private static final String MIME_SVG = "image/svg+xml";

  public SVGOutputHandler()
  {
  }

  public void render(final OutputStream out, final Chart chart) throws IOException
  {
    chart.renderAsSVG(out);
  }

  public String getMimeType()
  {
    return MIME_SVG;
  }
}
