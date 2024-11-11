/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cgg;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import pt.webdetails.cgg.output.OutputHandler;
import pt.webdetails.cgg.scripts.DefaultScriptFactory;

public class DefaultCgg extends AbstractCgg
{
  private OutputStream out;

  public DefaultCgg()
  {
  }

  public DefaultCgg(final OutputStream out)
  {
    this.out = out;
  }

  public DefaultCgg(final OutputStream out, final URL scriptContext)
  {
    this.out = out;
    setScriptFactory(new DefaultScriptFactory(scriptContext));
  }

  public OutputStream getOutputStream()
  {
    return out;
  }

  public void setOutputStream(final OutputStream out)
  {
    this.out = out;
  }

  protected void produceOutput(final Chart chart, final String requestedOutputHandler) throws IOException, ScriptExecuteException
  {
    OutputHandler outputHandler = getOutputFactory().create(requestedOutputHandler);
    outputHandler.render(out, chart);
  }
}
