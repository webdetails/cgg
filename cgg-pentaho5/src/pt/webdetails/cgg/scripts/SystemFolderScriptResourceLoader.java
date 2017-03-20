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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import pt.webdetails.cpf.repository.pentaho.SystemPluginResourceAccess;
import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class SystemFolderScriptResourceLoader implements ScriptResourceLoader {

  private String basePath;


  public SystemFolderScriptResourceLoader( String basePath ) {
    this.basePath = basePath;
  }


  @Override
  public Reader getSystemLibraryScript( String s ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( s );
  }

  @Override
  public Reader getContextLibraryScript( String s ) throws IOException, ScriptResourceNotFoundException {
    return new InputStreamReader( getContextResource( s ), CharsetHelper.getEncoding() );
  }

  @Override
  public String getContextResourceURI( String s ) throws IOException, ScriptResourceNotFoundException {
    return "file://" + s;
  }

  @Override
  public InputStream getContextResource( String s ) throws IOException, ScriptResourceNotFoundException {
    if ( ( basePath != null && basePath.startsWith( "/system" ) )
      || s.startsWith( "/system" ) ) {
      SystemPluginResourceAccess resourceAccess = new SystemPluginResourceAccess( "cgg", "" );

      String fullPath = s;
      if ( basePath != null && !s.startsWith( "/system" ) ) {
        fullPath = basePath + "/" + s;
      }
      return resourceAccess.getFileInputStream( fullPath );
    } else if ( basePath != null && basePath.startsWith( "/plugin" ) || s.startsWith( "/plugin" ) ) {
      // get the plugin id first
      String[] sections = s.split( "/" );

      SystemPluginResourceAccess resourceAccess = new SystemPluginResourceAccess( sections[2], "" );

      //remove the name of the plugin
      sections = (String[]) ArrayUtils.remove( sections, 1 );
      sections = (String[]) ArrayUtils.remove( sections, 1 );

      return resourceAccess.getFileInputStream( StringUtils.join( sections, "/" ) );
    } else {
      throw new ScriptResourceNotFoundException( s );
    }
  }

  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }
}
