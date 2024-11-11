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
