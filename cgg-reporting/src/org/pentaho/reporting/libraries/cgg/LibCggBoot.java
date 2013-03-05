package org.pentaho.reporting.libraries.cgg;

import pt.webdetails.cgg.CggBoot;
import org.pentaho.reporting.libraries.base.boot.AbstractBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;
import org.pentaho.reporting.libraries.cgg.output.DefaultOutputFactory;
import org.pentaho.reporting.libraries.cgg.output.PngOutputHandler;
import org.pentaho.reporting.libraries.cgg.output.SVGOutputHandler;

public class LibCggBoot extends AbstractBoot
{
  /** A singleton variable for the booter. */
  private static LibCggBoot instance;

  /**
   * Returns the singleton instance of LibBaseBoot.
   *
   * @return the boot class for Libbase.
   */
  public static synchronized LibCggBoot getInstance()
  {
    if (instance == null)
    {
      instance = new LibCggBoot();
    }
    return instance;
  }

  private LibCggBoot()
  {
  }

  protected Configuration loadConfiguration()
  {
    return createDefaultHierarchicalConfiguration
        ("/org/pentaho/reporting/libraries/cgg/libcgg.properties",
            "/libcgg.properties", true, LibCggBoot.class);
  }

  protected void performBoot()
  {
    CggBoot.init();
  }

  protected ProjectInformation getProjectInfo()
  {
    return LibCggInfo.getInstance();
  }
}
