package pt.webdetails.cgg.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public interface ScriptResourceLoader
{
  public Reader getSystemLibraryScript(String script) throws IOException, ScriptResourceNotFoundException;

  public Reader getContextLibraryScript(String script) throws IOException, ScriptResourceNotFoundException;

  public String getContextResourceURI(String script) throws IOException, ScriptResourceNotFoundException;

  public InputStream getContextResource(String script) throws IOException, ScriptResourceNotFoundException;

}
