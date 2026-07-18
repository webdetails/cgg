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

import java.io.File;
import java.io.FileOutputStream;

public class CggGoldGenerator extends CggGoldTestBase
{
  protected void handleContent(final byte[] reportOutput, final File file, final String outputType) throws Exception
  {
    file.getParentFile().mkdirs();

    final FileOutputStream outputStream = new FileOutputStream(file);
    try
    {
      outputStream.write(reportOutput);
    }
    finally
    {
      outputStream.close();
    }
  }

  protected void initializeTestEnvironment() throws Exception
  {
    super.setUp();

    final File marker = findMarker();
    final File gold = new File(marker, "gold");
    gold.mkdirs();
  }

  public static void main(String[] args) throws Exception
  {
    new CggGoldGenerator().runAllGoldCharts();
  }
}
