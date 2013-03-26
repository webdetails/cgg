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
