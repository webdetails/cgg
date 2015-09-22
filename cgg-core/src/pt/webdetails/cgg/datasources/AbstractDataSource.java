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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.swing.table.TableModel;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractDataSource implements DataSource {
  protected String outputType;
  private ArrayList<String> sortBy;
  private HashMap<String, Object> parameter;
  private String query;
  private String path;

  public AbstractDataSource() {
    outputType = "json"; sortBy = new ArrayList<String>(); parameter = new HashMap<String, Object>();
  }

  protected String export( TableModel model ) throws IOException {
    if ( outputType.equals( "json" ) ) {
      final JsonExporter exporter = new JsonExporter(); return exporter.export( model );
    } else {
      throw new IllegalArgumentException();
    }
  }

  public String execute() throws IOException {
    TableModel model = performQuery(); if ( model == null ) {
      return null;
    }

    if ( sortBy.isEmpty() == false ) {
      model = sort( model );
    }

    return export( model );
  }

  protected HashMap<String, Object> getParameter() {
    return parameter;
  }

  private TableModel sort( final TableModel model ) {
    return model;
  }

  protected String getOutputType() {
    return outputType;
  }

  protected List<String> getSortBy() {
    return sortBy;
  }

  protected abstract TableModel performQuery();

  public void setParameter( final String param, final Object val ) {
    if ( "outputType".equals( param ) ) {
      outputType = (String) val;
    } if ( "sortBy".equals( param ) ) {
      sortBy.clear(); if ( val instanceof Collection ) {
        final Collection valC = (Collection) val; for ( final Object o : valC ) {
          if ( o == null ) {
            continue;
          }

          final String s = String.valueOf( o ); if ( StringUtils.isEmpty( s ) ) {
            continue;
          }

          sortBy.add( s );
        }
      } else {
        if ( val != null ) {
          final String source = String.valueOf( val ); if ( StringUtils.isEmpty( source ) == false ) {
            sortBy.add( source );
          }
        }
      }
    }

    parameter.put( param, val );
  }

  public String getQuery() {
    return query;
  }

  public void setQuery( final String query ) {
    this.query = query;
  }

  public String getDataAccessId() {
    return query;
  }

  public void setDataAccessId( final String dataAccessId ) {
    this.query = dataAccessId;
  }

  public String getPath() {
    return path;
  }

  public void setPath( final String path ) {
    this.path = path;
  }
}
