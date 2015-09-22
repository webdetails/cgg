package pt.webdetails.cgg.scripts;

import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class DefaultScriptResourceLoader implements ScriptResourceLoader {
  private URL context;

  public DefaultScriptResourceLoader() {
  }

  public DefaultScriptResourceLoader( final URL context ) {
    this.context = context;
  }

  public URL getContext() {
    return context;
  }

  public void setContext( final URL context ) {
    this.context = context;
  }

  public String getContextResourceURI( final String script ) throws IOException, ScriptResourceNotFoundException {
    if ( context == null ) {
      throw new ScriptResourceNotFoundException( script );
    } try {
      final URL url = new URL( context, script ); return url.toURI().toASCIIString();
    } catch ( MalformedURLException e ) {
      throw new IOException( e );
    } catch ( URISyntaxException e ) {
      throw new IOException( e );
    }
  }

  public InputStream getContextResource( final String script ) throws IOException, ScriptResourceNotFoundException {
    if ( context == null ) {
      throw new ScriptResourceNotFoundException( script );
    } try {
      final URL url = new URL( context, script ); return new BufferedInputStream( url.openStream() );
    } catch ( MalformedURLException e ) {
      throw new IOException( e );
    }
  }

  public Reader getContextLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    return new InputStreamReader( getContextResource( script ), CharsetHelper.getEncoding() );
  }

  public Reader getSystemLibraryScript( final String script ) throws IOException, ScriptResourceNotFoundException {
    throw new ScriptResourceNotFoundException( script );
  }
}
