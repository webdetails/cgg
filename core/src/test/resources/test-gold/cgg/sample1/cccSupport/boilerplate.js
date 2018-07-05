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
