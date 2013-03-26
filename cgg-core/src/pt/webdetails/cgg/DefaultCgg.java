package pt.webdetails.cgg;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;


import pt.webdetails.cgg.datasources.DataSourceFactory;
import pt.webdetails.cgg.datasources.DefaultDataSourceFactory;
import pt.webdetails.cgg.output.DefaultOutputFactory;
import pt.webdetails.cgg.output.OutputFactory;
import pt.webdetails.cgg.output.OutputHandler;
import pt.webdetails.cgg.scripts.DefaultScriptFactory;
import pt.webdetails.cgg.scripts.ScriptFactory;


public class DefaultCgg extends AbstractCgg
{
  private OutputStream out;
  private URL scriptContext;

  public DefaultCgg(final OutputStream out, final URL scriptContext)
  {
    this.out = out;
    this.scriptContext = scriptContext;
  }

  protected void produceOutput(final Chart chart, final OutputHandler outputHandler) throws IOException
  {
    outputHandler.render(out, chart);
  }

  protected DataSourceFactory createDataSourceFactory()
  {
    return DefaultDataSourceFactory.getInstance();
  }

  protected OutputFactory createOutputFactory()
  {
    return DefaultOutputFactory.getInstance();
  }

  protected ScriptFactory createScriptFactory()
  {
    final DefaultScriptFactory scriptFactory = new DefaultScriptFactory();
    scriptFactory.setContext(scriptContext);
    return scriptFactory;
  }
}
