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
