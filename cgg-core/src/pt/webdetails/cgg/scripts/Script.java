/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.scripts;

import java.util.Map;

import org.mozilla.javascript.Scriptable;

import pt.webdetails.cgg.Chart;
import pt.webdetails.cgg.ScriptExecuteException;
import pt.webdetails.cgg.datasources.DataSourceFactory;

/**
 * @author pdpi
 */
public interface Script
{
  public void configure(final int width,
                        final int height,
                        final DataSourceFactory dataSourceFactory,
                        final ScriptFactory scriptFactory);

  public void setScope(Scriptable scope);

  public Chart execute(Map<String, Object> params) throws ScriptExecuteException;
}
