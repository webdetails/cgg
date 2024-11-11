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

import java.io.IOException;
import java.io.OutputStream;

public interface Chart
{
  public void renderAsPng(OutputStream out) throws IOException;
  public void renderAsSVG(OutputStream out) throws IOException;
  public Object getRawObject();
}
