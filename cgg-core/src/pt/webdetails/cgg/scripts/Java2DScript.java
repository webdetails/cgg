/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.scripts;

import java.awt.image.BufferedImage;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import pt.webdetails.cgg.Chart;
import pt.webdetails.cgg.Java2DChart;
import pt.webdetails.cgg.ScriptExecuteException;
import pt.webdetails.cgg.datasources.DataSourceFactory;

/**
 * @author pdpi
 */
public class Java2DScript extends BaseScript
{
  private static final Log logger = LogFactory.getLog(Java2DScript.class);
  private BufferedImage imageBuffer;
  private long width;
  private long height;

  public Java2DScript(final String source)
  {
    super(source);
  }

  public void configure(final int width,
                        final int height,
                        final DataSourceFactory dataSourceFactory,
                        final ScriptFactory scriptFactory)
  {
    super.configure(width, height, dataSourceFactory, scriptFactory);
    this.width = width;
    this.height = height;
  }

  @Override
  public Chart execute(final Map<String, Object> params) throws ScriptExecuteException
  {
    ContextFactory.getGlobal().enter();
    try
    {
      addGraphicsToScope();
      executeScript(params);
      return new Java2DChart(imageBuffer);
    }
    catch (Exception e)
    {
      throw new ScriptExecuteException(e);
    }
    finally
    {
      if (Context.getCurrentContext() != null)
      {
        Context.exit();
      }
    }
  }

  private void addGraphicsToScope()
  {
    final Scriptable scope = getScope();
    imageBuffer = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_4BYTE_ABGR);
    final Object wrappedGraphics = Context.javaToJS(imageBuffer.createGraphics(), scope);
    ScriptableObject.putProperty(scope, "graphics", wrappedGraphics);
  }
}
