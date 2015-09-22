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

package org.pentaho.reporting.libraries.cgg.datasources;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.table.TableModel;

import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

/**
 * @noinspection HardCodedStringLiteral
 */
public class XmlExporter {
  public XmlExporter() {
  }

  public String export( final TableModel tableModel ) throws IOException {
    final StringWriter stringWriter = new StringWriter(); final XmlWriter writer = new XmlWriter( stringWriter );
    writer.writeXmlDeclaration( "UTF-8" ); writer.writeTag( null, "CdaExport", XmlWriter.OPEN );
    writer.writeTag( null, "MetaData", XmlWriter.OPEN );
    // Generate metadata

    final int columnCount = tableModel.getColumnCount(); final int rowCount = tableModel.getRowCount();

    for ( int i = 0; i < columnCount; i++ ) {
      final AttributeList attributeList = new AttributeList();
      attributeList.setAttribute( null, "index", String.valueOf( i ) );
      attributeList.setAttribute( null, "type", getColType( tableModel.getColumnClass( i ) ) );
      attributeList.setAttribute( null, "name", tableModel.getColumnName( i ) );
      writer.writeTag( null, "ColumnMetaData", attributeList, XmlWriter.CLOSE );
    }

    writer.writeCloseTag();

    final SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US );
    writer.writeTag( null, "ResultSet", XmlWriter.OPEN ); for ( int rowIdx = 0; rowIdx < rowCount; rowIdx++ ) {
      writer.writeTag( null, "Row", XmlWriter.OPEN ); for ( int colIdx = 0; colIdx < columnCount; colIdx++ ) {

        final Object value = tableModel.getValueAt( rowIdx, colIdx ); if ( value instanceof Date ) {
          writer.writeTag( null, "Row", XmlWriter.OPEN ); writer.writeTextNormalized( format.format( value ), false );
          writer.writeCloseTag();
        } else if ( value != null ) {
          // numbers can be safely converted via toString, as they use a well-defined format there
          writer.writeTag( null, "Row", XmlWriter.OPEN ); writer.writeTextNormalized( value.toString(), false );
          writer.writeCloseTag();
        } else {
          writer.writeTag( null, "Row", "isNull", "true", XmlWriter.CLOSE );
        }
      } writer.writeCloseTag();

    } writer.writeCloseTag(); writer.writeCloseTag(); writer.flush();

    return stringWriter.toString();
  }

  protected String getColType( final Class<?> columnClass ) {
    if ( columnClass.equals( String.class ) ) {
      return "String";
    } else if ( columnClass.equals( Integer.class ) || columnClass.equals( Short.class ) || columnClass
        .equals( Byte.class ) ) {
      return "Integer";
    } else if ( Number.class.isAssignableFrom( columnClass ) ) {
      return "Numeric";
    } else if ( Date.class.isAssignableFrom( columnClass ) ) {
      return "Date";
    } else if ( columnClass.equals( Object.class ) ) {
      // todo: Quick and dirty hack, as the formula never knows what type is returned.
      return "String";
    } else {
      throw new IllegalArgumentException( "Cannot handle class: " + columnClass.toString(), null );
    }
  }
}
