/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cgg.scripts;

public class JCRScriptFactory extends AbstractScriptFactory {

  private ScriptResourceLoader resourceLoader;

  public JCRScriptFactory( String context ) {
    this( context, null );
  }

  public JCRScriptFactory( String context, final String userName ) {
    resourceLoader = new CompoundScriptResourceLoader(
      new JCRScriptResourceLoader( context ),
      new SystemFolderScriptResourceLoader( context ),
      new SystemScriptResourceLoader(),
      new WebResourceLoader( context, userName ) );
  }

  public void setResourceLoader( final ScriptResourceLoader resourceLoader ) {
    if ( resourceLoader == null ) {
      throw new NullPointerException();
    }
    this.resourceLoader = resourceLoader;
  }

  @Override
  protected ScriptResourceLoader getResourceLoader() {
    return resourceLoader;
  }
}
