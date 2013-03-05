/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.scripts;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultScriptFactory extends AbstractScriptFactory
{
  private static final Log logger = LogFactory.getLog(DefaultScriptFactory.class);
  private URL context;

  public DefaultScriptFactory()
  {
    context = this.getClass().getClassLoader().getResource(
        "org/pentaho/reporting/libraries/cgg/resources/Base.js");
  }

  public URL getContext()
  {
    return context;
  }

  public void setContext(final URL context)
  {
    this.context = context;
  }

  public String getContextResourceURI(final String script) throws IOException
  {
    try
    {
      final URL url = new URL(context, script);
      return url.toURI().toASCIIString();
    }
    catch (MalformedURLException e)
    {
      throw new IOException(e);
    }
    catch (URISyntaxException e)
    {
      throw new IOException(e);
    }
  }

  public InputStream getContextResource(final String script) throws IOException
  {
    try
    {
      final URL url = new URL(context, script);
      return new BufferedInputStream(url.openStream());
    }
    catch (MalformedURLException e)
    {
      throw new IOException(e);
    }
  }

}
