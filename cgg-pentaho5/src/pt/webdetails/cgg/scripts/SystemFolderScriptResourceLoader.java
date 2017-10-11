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

    String fullPath = s;

    Boolean isSystem = s.startsWith( "/system" );
    Boolean isPlugin = s.startsWith( "/plugin" );

    if ( !isSystem && !isPlugin ) {
      // However, if basePath is system, it is prepended to whatever path is given,
      // to make it a system path.
      if ( basePath == null || !basePath.startsWith( "/system" ) ) {
        throw new ScriptResourceNotFoundException( s );
      }

      fullPath = basePath + "/" + s;
      isSystem = true;
    }

    String plugin;

    if ( isSystem ) {
      plugin = "cgg";
    } else {
      // => isPlugin

      // Get the plugin id first
      String[] sections = fullPath.split( "/" );

      plugin = sections[2];

      // Remove the name of the plugin
      sections = (String[]) ArrayUtils.remove( sections, 1 );
      sections = (String[]) ArrayUtils.remove( sections, 1 );

      fullPath = StringUtils.join( sections, "/" );
    }

    SystemPluginResourceAccess resourceAccess = new SystemPluginResourceAccess( plugin, "" );

    return resourceAccess.getFileInputStream( fullPath );
  }

  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }
}
