/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.datasources;

/**
 *
 * @author pdpi
 */
public class DatasourceFactory {

    public DatasourceFactory() {
    }

    public static Datasource createDatasource(String type) {

        if (type.toUpperCase().equals("CDA")) {
            return new CdaDatasource();
        } else {
            return null;
        }

    }
}
