/*!
* Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
