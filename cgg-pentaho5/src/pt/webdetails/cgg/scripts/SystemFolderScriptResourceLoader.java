package pt.webdetails.cgg.scripts;


import pt.webdetails.cpf.repository.pentaho.SystemPluginResourceAccess;

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
    return new InputStreamReader( getContextResource( s ) );
  }

  @Override
  public String getContextResourceURI( String s ) throws IOException, ScriptResourceNotFoundException {
    return "file://" + s;
  }

  @Override
  public InputStream getContextResource( String s ) throws IOException, ScriptResourceNotFoundException {
    if ( ( basePath != null && basePath.startsWith( "/system" ) )
            || s.startsWith( "/system" ) )  {
      SystemPluginResourceAccess resourceAccess = new SystemPluginResourceAccess( "cgg", "" );

      String fullPath = s;
      if ( basePath != null && !s.startsWith( "/system" ) ) {
        fullPath = basePath + "/" + s;
      }
      return resourceAccess.getFileInputStream( fullPath );
    } else {
      throw new ScriptResourceNotFoundException( s );
    }
  }
}
