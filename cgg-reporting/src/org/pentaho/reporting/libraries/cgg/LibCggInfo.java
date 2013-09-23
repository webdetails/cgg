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

import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class LibCggInfo extends ProjectInformation
{
  /** A singleton variable for the info-class. */
  private static LibCggInfo info;

  /**
   * Returns a singleton instance of the LibBase project information structure.
   *
   * @return the LibBase project information.
   */
  public static synchronized ProjectInformation getInstance()
  {
    if (info == null)
    {
      info = new LibCggInfo();
      info.initialize();
    }
    return info;
  }

  /**
   * Private constructor to prevent object creation.
   */
  private LibCggInfo()
  {
    super("libcgg", "LibCgg");
  }

  /**
   * Initializes the project info.
   */
  private void initialize()
  {
    setBootClass(LibCggBoot.class.getName());
    setLicenseName("LGPL");
    setInfo("http://reporting.pentaho.org/libcgg/");
    setCopyright("(C)opyright 2012, by Pentaho Corporation and Contributors");
  }

  /**
   * The main method can be used to check the version of the code.
   *
   * @param args not used.
   */
  public static void main(final String[] args)
  {
    System.out.println(getInstance().getVersion());
  }
}
