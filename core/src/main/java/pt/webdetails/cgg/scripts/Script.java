/*!
 * Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
