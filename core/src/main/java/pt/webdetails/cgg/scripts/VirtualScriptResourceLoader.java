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
