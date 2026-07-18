/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package pt.webdetails.cgg;

import pt.webdetails.cgg.output.DefaultOutputFactory;
import pt.webdetails.cgg.output.PngOutputHandler;
import pt.webdetails.cgg.output.SVGOutputHandler;

public class CggBoot {

  
  public static void init() {
    DefaultOutputFactory.getInstance().register("svg", SVGOutputHandler.class);
    DefaultOutputFactory.getInstance().register("png", PngOutputHandler.class);

  }
}
