/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
