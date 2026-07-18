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
import java.io.InputStream;
import java.io.Reader;

public interface ScriptResourceLoader {
  public Reader getSystemLibraryScript( String script ) throws IOException, ScriptResourceNotFoundException;

  public Reader getContextLibraryScript( String script ) throws IOException, ScriptResourceNotFoundException;

  public String getContextResourceURI( String script ) throws IOException, ScriptResourceNotFoundException;

  public InputStream getContextResource( String script ) throws IOException, ScriptResourceNotFoundException;

  public InputStream getWebResource( String script ) throws IOException, ScriptResourceNotFoundException;
}
