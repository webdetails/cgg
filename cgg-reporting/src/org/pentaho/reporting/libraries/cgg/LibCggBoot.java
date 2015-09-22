/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

package org.pentaho.reporting.libraries.cgg;

import pt.webdetails.cgg.CggBoot;
import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
//import org.pentaho.reporting.libraries.cgg.output.DefaultOutputFactory;
//import org.pentaho.reporting.libraries.cgg.output.PngOutputHandler;
//import org.pentaho.reporting.libraries.cgg.output.SVGOutputHandler;

public class LibCggBoot extends AbstractBoot {
  /**
   * A singleton variable for the booter.
   */
  private static LibCggBoot instance;

  /**
   * Returns the singleton instance of LibBaseBoot.
   *
   * @return the boot class for Libbase.
   */
  public static synchronized LibCggBoot getInstance() {
    if ( instance == null ) {
      instance = new LibCggBoot();
    } return instance;
  }

  private LibCggBoot() {
  }

  protected Configuration loadConfiguration() {
    return createDefaultHierarchicalConfiguration( "/org/pentaho/reporting/libraries/cgg/libcgg.properties",
        "/libcgg.properties", true, LibCggBoot.class );
  }

  protected void performBoot() {
    CggBoot.init();
  }

  protected ProjectInformation getProjectInfo() {
    return LibCggInfo.getInstance();
  }
}
