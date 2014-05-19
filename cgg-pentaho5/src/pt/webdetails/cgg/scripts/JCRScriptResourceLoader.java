package pt.webdetails.cgg.scripts;


import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import pt.webdetails.cpf.repository.pentaho.unified.UserContentRepositoryAccess;

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
    return new InputStreamReader( getContextResource( s ) );
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
    }
    UserContentRepositoryAccess repositoryAccess = new UserContentRepositoryAccess( PentahoSessionHolder.getSession(),
      s.startsWith( "/" ) ? null : basePath );
    return repositoryAccess.getFileInputStream( s );
  }
}
