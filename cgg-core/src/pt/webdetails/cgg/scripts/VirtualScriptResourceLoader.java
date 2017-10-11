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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.InputStream;
import java.util.HashMap;

public class VirtualScriptResourceLoader implements ScriptResourceLoader {
  private HashMap<String, String> scripts;

  public VirtualScriptResourceLoader() {
    scripts = new HashMap<String, String>();
  }

  public void clear() {
    scripts.clear();
  }

  public String remove( final String key ) {
    return scripts.remove( key );
  }

  public String put( final String key, final String value ) {
    return scripts.put( key, value );
  }

  public String get( final String key ) {
    return scripts.get( key );
  }

  public boolean isEmpty() {
    return scripts.isEmpty();
  }

  public Reader getSystemLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException();
  }

  public Reader getContextLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    String s = scripts.get( script );
    if ( s != null ) {
      return new StringReader( s );
    }

    throw new ScriptResourceNotFoundException();
  }

  public String getContextResourceURI( final String script ) throws IOException, ScriptResourceNotFoundException {
    String s = scripts.get( script );
    if ( s != null ) {
      return "virtual://" + script;
    }
    throw new ScriptResourceNotFoundException();
  }

  public InputStream getContextResource( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException();
  }

  @Override
  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }
}
