/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

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
