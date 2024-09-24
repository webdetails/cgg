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

lib('cgg-env.js');
load('cccSupport/q01-01.js');
load('cccSupport/boilerplate.js');


/*
 * Get the settings. Ideally, we'd have a JSON
 * file here, but we don't have a way to load JSON files quite yet,
 * so we're using an actual .js that declares a settings variable.
 * Plus, I'm not sure how we'd handle the cases where CCC fields
 * are /supposed/ to be functions rather than data.
 */
var definitionsScript = params.get("chart").toString();
load(definitionsScript);
// var settings = loadJSON(definitionsScript); // How it should work.

/*
 * Hardcoded data just as a demo. How would you like to handle this bit?
 */
var dataSettings = {crosstabMode: false, seriesInRows: false};
var data = relational_01;

/*
 * And here's the boilerplate all abstracted. ChartType should also come from
 * the settings, but that's not how CCC works as-is, so we'll have to agree on
 * some way to pass this. Possibly settings could be of the form
 * {type: "LineChart", config: {}};
 */
boilerplate(params.get("chartType").toString(),settings,data,dataSettings);
