/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

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
