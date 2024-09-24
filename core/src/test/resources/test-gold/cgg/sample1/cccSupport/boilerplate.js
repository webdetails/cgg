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

cgg.init();

var ccc = require('ccc!');
var pvc = ccc.pvc;
var pv  = ccc.pv;

function boilerplate(type,settings, data, dataSettings) {

  var elem = document.createElement('g');
  elem.setAttribute('id','canvas');

  document.lastChild.appendChild(elem);

  settings.canvas = 'canvas';
  settings.showTooltips = false;

  var chart = new pvc[type](settings);
  chart.setData(data, dataSettings);
  chart.render();

  var error = chart.getLastRenderError();
  if(error) {
    print("CCC render error: " + error.message + "\nStack: " + error.stack);
    throw error;
  }

  document.lastChild.appendChild(elem.lastChild);
  document.lastChild.setAttribute('width', settings.width);
  document.lastChild.setAttribute('height', settings.height);
  document.lastChild.setAttribute('viewBox', '0 0 ' + settings.width + ' ' + settings.height);
}
