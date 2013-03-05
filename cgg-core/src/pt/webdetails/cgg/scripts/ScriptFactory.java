/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.scripts;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import pt.webdetails.cgg.ScriptCreationException;

/**
 * @author pdpi
 */
public interface ScriptFactory
{
  public Reader getSystemLibraryScript(String script) throws IOException;

  public Reader getContextLibraryScript(String script) throws IOException;
  
  public String getContextResourceURI(String script) throws IOException;
  public InputStream getContextResource(String script) throws IOException;

  public Script createScript(String path, String scriptType)
      throws FileNotFoundException, IOException, ScriptCreationException;

  public void clearCachedScopes();
}
