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

import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;
import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class JCRScriptResourceLoader implements ScriptResourceLoader {


  private String basePath;


  public JCRScriptResourceLoader( String basePath ) {
    this.basePath = basePath;
  }

  @Override
  public Reader getSystemLibraryScript( String s ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( s );
  }

  @Override
  public Reader getContextLibraryScript( String s ) throws IOException, ScriptResourceNotFoundException {
    try {
      return new InputStreamReader( getContextResource( s ), CharsetHelper.getEncoding() );
    } catch ( ScriptResourceNotFoundException e ) {
      throw e;
    } 
  }

  @Override
  public String getContextResourceURI( String s ) throws IOException, ScriptResourceNotFoundException {
    return "file://" + s;
  }

  @Override
  public InputStream getContextResource( String s ) throws IOException, ScriptResourceNotFoundException {
    if ( ( basePath != null && basePath.startsWith( "/system" ) )
      || s.startsWith( "/system" )  ) {
      throw new ScriptResourceNotFoundException( s );
    } else if ( ( basePath != null && basePath.startsWith( "/plugin" ) )
        || s.startsWith( "/plugin" )  ) {
      throw new ScriptResourceNotFoundException( s );
    }
    UserContentRepositoryAccess repositoryAccess = new UserContentRepositoryAccess( PentahoSessionHolder.getSession(),
      s.startsWith( "/" ) ? null : basePath );
    return repositoryAccess.getFileInputStream( s );
  }

  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }
}
