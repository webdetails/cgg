/*!
* Copyright 2002 - 2017 Webdetails, a Pentaho company.  All rights reserved.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebResourceLoader implements ScriptResourceLoader {
  private static final Log logger = LogFactory.getLog( WebResourceLoader.class );

  private static final String URL_PARAM_USER = "_TRUST_USER_";
  private String context;
  private String userName;

  public WebResourceLoader() {
  }

  public WebResourceLoader( final String context, final String userName ) {
    this.context = context;
    this.userName = userName;
  }

  public String getContextResourceURI( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }

  public InputStream getContextResource( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }

  public Reader getContextLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }

  public Reader getSystemLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }

  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException {
    try {
      String url = script;

      // e.g. "http://my-server:8080/pentaho/"
      String serverURL = PentahoSystem.getApplicationContext().getFullyQualifiedServerURL();
      // e.g. "/pentaho/"
      String contextPath = new URL( serverURL ).getPath();
      // e.g. "http://my-server:8080"
      String baseURL = serverURL.substring( 0, serverURL.length() - contextPath.length() );

      String regex = "^(.[^\\?]*)\\?plugin=\\&name=plugin\\/(.[^\\/]*)(.*)$";
      Matcher m = Pattern.compile( regex ).matcher( script );
      if ( m.matches() ) {
        url = m.group( 1 ).replace( "${CONTEXT_PATH}", baseURL + contextPath )
            + "?plugin=" + m.group( 2 )
            + "&name=" + m.group( 3 ).substring( 1, m.group( 3 ).length() );
      } else if ( url.startsWith( "/" ) ) {
        url = baseURL + url;
      }

      if ( this.userName != null ) {
        url = url + ( url.contains( "?" ) ? "&" : "?" )
            + URL_PARAM_USER + "=" + URLEncoder.encode( this.userName, StandardCharsets.UTF_8.name() );
      }

      URLConnection connection = new URL( url ).openConnection();

      return new BufferedInputStream( connection.getInputStream() );
    } catch ( MalformedURLException ex ) {
      throw new ScriptResourceNotFoundException( script );
    }
  }
}
