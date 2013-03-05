/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */
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
