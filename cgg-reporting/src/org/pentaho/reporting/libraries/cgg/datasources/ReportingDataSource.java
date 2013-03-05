/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package org.pentaho.reporting.libraries.cgg.datasources;

import java.io.IOException;

import javax.swing.table.TableModel;
import pt.webdetails.cgg.datasources.AbstractDataSource;




public abstract class ReportingDataSource extends AbstractDataSource
{

  @Override
  protected String export(TableModel model) throws IOException
  {
    if (outputType.equals("xml")) {
      final XmlExporter exporter = new XmlExporter();
      return exporter.export(model);
    } else 
        return super.export(model);
  }

}
