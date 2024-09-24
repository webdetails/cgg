/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package pt.webdetails.cgg.scripts;

import pt.webdetails.cgg.Chart;
import pt.webdetails.cgg.ScriptExecuteException;
import pt.webdetails.cgg.datasources.DataSourceFactory;

import java.util.Locale;
import java.util.Map;

public interface Script {
  void configure( final int width,
                  final int height,
                  final DataSourceFactory dataSourceFactory,
                  final ScriptFactory scriptFactory ) throws ScriptExecuteException;

  default void configureLocale( final Locale locale ) throws ScriptExecuteException {
  }

  void setScope( BaseScope scope ) throws ScriptExecuteException;

  Chart execute( Map<String, Object> params ) throws ScriptExecuteException;
}
