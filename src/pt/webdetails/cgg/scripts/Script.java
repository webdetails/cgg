/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg.scripts;

import java.util.Map;
import org.mozilla.javascript.Scriptable;
import pt.webdetails.cgg.charts.Chart;

/**
 *
 * @author pdpi
 */
public interface Script {


    public void setScope(Scriptable scope);
    
    public Chart execute();
    public Chart execute(Map<String,Object> params);
}
