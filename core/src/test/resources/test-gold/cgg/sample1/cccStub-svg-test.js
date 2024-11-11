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
