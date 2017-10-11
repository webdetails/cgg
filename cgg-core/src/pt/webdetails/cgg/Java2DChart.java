/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;

/**
 * @author pdpi
 */
public class Java2DChart implements Chart
{
  private RenderedImage buffer;

  public Java2DChart(final RenderedImage buffer)
  {
    if (buffer == null)
    {
      throw new NullPointerException();
    }
    this.buffer = buffer;
  }

  public RenderedImage getRawObject()
  {
    return buffer;
  }

  public void renderAsPng(final OutputStream out) throws IOException
  {
    ImageIO.write(buffer, "png", out);
  }

  public void renderAsSVG(final OutputStream out) throws IOException
  {
    throw new IOException("Unsupported output type: SVG");
  }
}
