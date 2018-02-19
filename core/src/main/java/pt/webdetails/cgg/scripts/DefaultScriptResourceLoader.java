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

package pt.webdetails.cgg.scripts;

import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class DefaultScriptResourceLoader implements ScriptResourceLoader {
  private URL context;

  public DefaultScriptResourceLoader() {
  }

  public DefaultScriptResourceLoader( final URL context ) {
    this.context = context;
  }

  public URL getContext() {
    return context;
  }

  public void setContext( final URL context ) {
    this.context = context;
  }

  public String getContextResourceURI( final String script ) throws IOException, ScriptResourceNotFoundException {
    if ( context == null ) {
      throw new ScriptResourceNotFoundException( script );
    }
    try {
      final URL url = new URL( context, script );
      return url.toURI().toASCIIString();
    } catch ( MalformedURLException e ) {
      throw new IOException( e );
    } catch ( URISyntaxException e ) {
      throw new IOException( e );
    }
  }

  public InputStream getContextResource( final String script ) throws IOException, ScriptResourceNotFoundException {
    if ( context == null ) {
      throw new ScriptResourceNotFoundException( script );
    }
    try {
      final URL url = new URL( context, script );
      return new BufferedInputStream( url.openStream() );
    } catch ( MalformedURLException e ) {
      throw new IOException( e );
    }
  }

  public Reader getContextLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    return new InputStreamReader( getContextResource( script ), CharsetHelper.getEncoding() );
  }

  public Reader getSystemLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }

  @Override
  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }
}
