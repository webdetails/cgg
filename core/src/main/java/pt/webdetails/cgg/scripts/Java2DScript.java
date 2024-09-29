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

import java.awt.image.BufferedImage;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import pt.webdetails.cgg.Chart;
import pt.webdetails.cgg.Java2DChart;
import pt.webdetails.cgg.ScriptExecuteException;
import pt.webdetails.cgg.datasources.DataSourceFactory;

/**
 * @author pdpi
 */
public class Java2DScript extends BaseScript {
  private BufferedImage imageBuffer;
  private long width;
  private long height;

  public Java2DScript( final String source, final boolean isMultiPage ) {
    super( source, isMultiPage );
  }

  public void configure( final int width,
                         final int height,
                         final DataSourceFactory dataSourceFactory,
                         final ScriptFactory scriptFactory ) throws ScriptExecuteException {
    super.configure( width, height, dataSourceFactory, scriptFactory );
    this.width = width;
    this.height = height;
  }

  @Override
  public Chart execute( final Map<String, Object> params ) throws ScriptExecuteException {
    if ( Context.getCurrentContext() == null ) {
      throw new ScriptExecuteException();
    }

    Context.getCurrentContext().getFactory().enterContext();
    try {
      addGraphicsToScope();
      executeScript( params );
      return new Java2DChart( imageBuffer );
    } catch ( Exception e ) {
      throw new ScriptExecuteException( e );
    } finally {
      Context.exit();
    }
  }

  private void addGraphicsToScope() {
    final Scriptable scope = getScope();
    imageBuffer = new BufferedImage( (int) width, (int) height, BufferedImage.TYPE_4BYTE_ABGR );
    final Object wrappedGraphics = Context.javaToJS( imageBuffer.createGraphics(), scope );
    ScriptableObject.putProperty( scope, "graphics", wrappedGraphics );
  }
}
