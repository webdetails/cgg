/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.webdetails.cgg.script;

import java.util.Map;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author pdpi
 */
public interface Script {


    public void setScope(Scriptable scope);
    
    public String execute();
    public String execute(Map<String,String> params);
}
