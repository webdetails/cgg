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
