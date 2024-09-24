/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cgg;

import org.junit.Test;

public class CggGoldSampleTest extends CggGoldTestBase {
  public CggGoldSampleTest() {
  }

  @Test
  public void testExecuteReports() throws Exception {
    runAllGoldCharts();
  }
}
