/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.webdetails.cgg.script;

import java.util.Map;

/**
 *
 * @author pdpi
 */
public interface Script {


    public void setDependencies(String[] files);
    
    public String execute();
    public String execute(Map<String,String> params);
}
