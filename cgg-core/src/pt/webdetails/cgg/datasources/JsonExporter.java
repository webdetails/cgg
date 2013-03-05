/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.datasources;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import javax.swing.table.TableModel;

import com.google.gson.stream.JsonWriter;

/** @noinspection HardCodedStringLiteral*/
public class JsonExporter implements Exporter
{
  public JsonExporter()
  {
  }

  public String export(final TableModel tableModel) throws IOException
  {
    final StringWriter stringWriter = new StringWriter();
    final JsonWriter writer = new JsonWriter(stringWriter);
    writer.beginObject();
    writer.name("metadata");
    writer.beginArray();

    final int columnCount = tableModel.getColumnCount();
    final int rowCount = tableModel.getRowCount();

    for (int i = 0; i < columnCount; i++)
    {
      writer.beginObject();
      writer.name("colIndex").value(i);
      writer.name("colName").value(tableModel.getColumnName(i));
      writer.name("colType").value(getColType(tableModel.getColumnClass(i)));
      writer.endObject();
    }
    writer.endArray();

    writer.name("resultset");
    writer.beginArray();
    for (int rowIdx = 0; rowIdx < rowCount; rowIdx++)
    {
      writer.beginArray();
      for (int colIdx = 0; colIdx < columnCount; colIdx++)
      {
        final Object valueAt = tableModel.getValueAt(rowIdx, colIdx);
        if (valueAt == null)
        {
          writer.nullValue();
        }
        else
        {
          writer.value(String.valueOf(valueAt));
        }
      }
      writer.endArray();
    }
    writer.endArray();
    writer.endObject();
    writer.flush();
    return stringWriter.toString();
  }

  protected String getColType(final Class<?> columnClass)
  {
    if (columnClass.equals(String.class))
    {
      return "String";
    }
    else if (columnClass.equals(Integer.class) || columnClass.equals(Short.class) || columnClass.equals(Byte.class))
    {
      return "Integer";
    }
    else if (Number.class.isAssignableFrom(columnClass))
    {
      return "Numeric";
    }
    else if (Date.class.isAssignableFrom(columnClass))
    {
      return "Date";
    }
    else if (columnClass.equals(Object.class))
    {
      // todo: Quick and dirty hack, as the formula never knows what type is returned.
      return "String";
    }
    else
    {
      throw new IllegalArgumentException("Cannot handle class: " + columnClass.toString(), null);
    }
  }
}
