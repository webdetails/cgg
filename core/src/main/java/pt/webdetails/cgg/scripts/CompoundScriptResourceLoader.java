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
