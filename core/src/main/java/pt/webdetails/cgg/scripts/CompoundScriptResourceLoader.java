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
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;

public class CompoundScriptResourceLoader implements ScriptResourceLoader {
  private ArrayList<ScriptResourceLoader> loaders;

  public CompoundScriptResourceLoader() {
    loaders = new ArrayList<ScriptResourceLoader>();
  }

  public CompoundScriptResourceLoader( final ScriptResourceLoader... loaders ) {
    this();
    for ( ScriptResourceLoader loader : loaders ) {
      add( loader );
    }
  }

  public void add( ScriptResourceLoader loader ) {
    if ( loader == null ) {
      throw new NullPointerException();
    }
    loaders.add( loader );
  }

  public void remove( ScriptResourceLoader loader ) {
    if ( loader == null ) {
      throw new NullPointerException();
    }
    loaders.remove( loader );
  }

  public int size() {
    return loaders.size();
  }

  public ScriptResourceLoader get( int i ) {
    return loaders.get( i );
  }

  public Reader getSystemLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    for ( ScriptResourceLoader loader : loaders ) {
      try {
        return loader.getSystemLibraryScript( script );
      } catch ( ScriptResourceNotFoundException srnfe ) {
        // ignored
      }
    }
    throw new ScriptResourceNotFoundException( script );
  }

  public Reader getContextLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    for ( ScriptResourceLoader loader : loaders ) {
      try {
        return loader.getContextLibraryScript( script );
      } catch ( ScriptResourceNotFoundException srnfe ) {
        // ignored
      }
    }
    throw new ScriptResourceNotFoundException( script );
  }

  public String getContextResourceURI( final String script ) throws IOException, ScriptResourceNotFoundException {
    for ( ScriptResourceLoader loader : loaders ) {
      try {
        return loader.getContextResourceURI( script );
      } catch ( ScriptResourceNotFoundException srnfe ) {
        // ignored
      }
    }
    throw new ScriptResourceNotFoundException( script );
  }

  public InputStream getContextResource( final String script ) throws IOException, ScriptResourceNotFoundException {
    for ( ScriptResourceLoader loader : loaders ) {
      try {
        return loader.getContextResource( script );
      } catch ( ScriptResourceNotFoundException srnfe ) {
        // ignored
      }
    }
    throw new ScriptResourceNotFoundException( script );
  }

  @Override
  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException {
    for ( ScriptResourceLoader loader : loaders ) {
      try {
        return loader.getWebResource( script );
      } catch ( ScriptResourceNotFoundException srnfe ) {
        // ignored
      }
    }
    throw new ScriptResourceNotFoundException( script );
  }
}
