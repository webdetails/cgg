/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

package pt.webdetails.cgg.scripts;

import java.io.BufferedInputStream;
import java.io.File;
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
        "pt/webdetails/cgg/resources/Base.js");
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
    catch (Exception e)
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
