/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
* 
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
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
