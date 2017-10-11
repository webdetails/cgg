/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
