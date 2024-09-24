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

import pt.webdetails.cgg.CggBoot;
import junit.framework.TestCase;

public class DefaultScriptFactoryTest extends TestCase
{
  public DefaultScriptFactoryTest()
  {
  }

  public DefaultScriptFactoryTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    CggBoot.init();
  }

  public void testBasic()
  {
    
  }
}
