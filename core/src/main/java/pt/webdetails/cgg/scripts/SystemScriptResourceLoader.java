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
package pt.webdetails.cgg.scripts;

import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class SystemScriptResourceLoader implements ScriptResourceLoader {
  public SystemScriptResourceLoader() {
  }

  public String getContextResourceURI( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException();
  }

  public InputStream getContextResource( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException();
  }

  public Reader getSystemLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    final String resource = "/pt/webdetails/cgg/resources/" + script;
    final InputStream resourceAsStream = getClass().getResourceAsStream( resource );
    if ( resourceAsStream == null ) {
      throw new ScriptResourceNotFoundException( "Resource not found: " + resource );
    }
    return new BufferedReader(
      new InputStreamReader( new BufferedInputStream( resourceAsStream ), CharsetHelper.getEncoding() ) );
  }

  public Reader getContextLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException();
  }

  @Override
  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }
}
