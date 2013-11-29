/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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
