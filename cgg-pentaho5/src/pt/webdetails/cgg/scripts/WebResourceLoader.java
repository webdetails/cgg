package pt.webdetails.cgg.scripts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

public class WebResourceLoader implements ScriptResourceLoader
{
  private static final Log logger = LogFactory.getLog(WebResourceLoader.class);

  private static final String URL_PARAM_USER = "_TRUST_USER_";
  private String context;
  private String contextPath;
  private String userName;

  public WebResourceLoader()
  {
  }

  public WebResourceLoader(final String context, final String userName )
  {
    this.context = context;
    this.userName = userName;
  }

  public String getContextResourceURI( final String script ) throws IOException, ScriptResourceNotFoundException
  {
    throw new ScriptResourceNotFoundException( script );
  }

  public InputStream getContextResource( final String script ) throws IOException, ScriptResourceNotFoundException
  {
    throw new ScriptResourceNotFoundException( script );
  }

  public Reader getContextLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException
  {
    throw new ScriptResourceNotFoundException( script );
  }

  public Reader getSystemLibraryScript(final String script) throws IOException, ScriptResourceNotFoundException
  {
    throw new ScriptResourceNotFoundException( script );
  }

  public InputStream getResource( String script ) throws IOException, ScriptResourceNotFoundException
  {
    try
    {
      String url = script;

      if ( this.userName != null ) {
        url = url + ( url.contains("?") ? "&" : "?" ) +
                URL_PARAM_USER + "=" + URLEncoder.encode( this.userName, StandardCharsets.UTF_8.name() );
      }

      // Paths already contain contextPath.
      // e.g. "/pentaho/foo/bar"
      if ( url.startsWith("/") ) {
        // e.g. "http://my-server:8080/pentaho/"
        String serverURL = PentahoSystem.getApplicationContext().getFullyQualifiedServerURL();

        // e.g. "/pentaho/"
        String contextPath = new URL( serverURL ).getPath();

        // e.g. "http://my-server:8080"
        String baseURL = serverURL.substring( 0, serverURL.length() - contextPath.length() );

        url = baseURL + url;
      }

      URLConnection connection = new URL( url ).openConnection();

      return new BufferedInputStream( connection.getInputStream() );
    }
    catch ( MalformedURLException ex )
    {
      throw new ScriptResourceNotFoundException( script );
    }
  }
}
