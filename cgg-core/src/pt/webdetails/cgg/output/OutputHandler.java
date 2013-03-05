/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

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
