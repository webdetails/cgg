/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.datasources;

import java.util.Date;
import java.util.List;

/**
 *
 * @author pdpi
 */
public interface Datasource {

    public String execute();

    public void setParameter(String param, String val);

    public void setParameter(String param, Date val);

    public void setParameter(String param, List val);
}
