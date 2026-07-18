/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package pt.webdetails.cgg.scripts;

import java.io.IOException;

import pt.webdetails.cgg.ScriptCreationException;
import pt.webdetails.cgg.ScriptExecuteException;

/**
 * @author pdpi
 */
public interface ScriptFactory extends ScriptResourceLoader {
  void enterContext();

  void exitContext();

  Script createScript( String path, String scriptType, boolean isMultiPage )
      throws ScriptResourceNotFoundException, IOException, ScriptCreationException, ScriptExecuteException;

  void clearCachedScopes();
}
