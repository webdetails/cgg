/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cgg.scripts;

import java.net.URL;

public class DefaultScriptFactory extends AbstractScriptFactory
{
  private ScriptResourceLoader resourceLoader;

  public DefaultScriptFactory()
  {
    resourceLoader = new SystemScriptResourceLoader();
  }

  public void setResourceLoader(final ScriptResourceLoader resourceLoader)
  {
    if (resourceLoader == null)
    {
      throw new NullPointerException();
    }
    this.resourceLoader = resourceLoader;
  }

  public DefaultScriptFactory(URL url)
  {
    resourceLoader = new CompoundScriptResourceLoader
        (new DefaultScriptResourceLoader(url), new SystemScriptResourceLoader());
  }

  public ScriptResourceLoader getResourceLoader()
  {
    return resourceLoader;
  }
}

