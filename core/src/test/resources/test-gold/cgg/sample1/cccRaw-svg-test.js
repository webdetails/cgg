/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


/* These are the necessary includes. In this case I created a couple of
 * ad hoc includes for the data and boilerplate code,
 */

lib('cgg-env.js');
load('cccSupport/q01-01.js');
load('cccSupport/boilerplate.js');

var settings = {
  width: 300,
  height: 200,
  animate: true,
  title: "line chart - standard with dots",
  titlePosition: "top",
  titleSize: 40,
  legend: true,
  legendPosition: "top",
  legendAlign: "right",

  orientation: 'horizontal',
  timeSeries: true,
  timeSeriesFormat: "%Y-%m-%d",

  showValues: false,
  showTooltips: false,
  showDots: true,
  yAxisSize: 30,
  xAxisSize: 30,
  xAxisFullGrid: true,
  yAxisFullGrid: true

};

var dataSettings = {crosstabMode: false, seriesInRows: false};
var data = relational_01;

boilerplate("LineChart",settings,data,dataSettings);
