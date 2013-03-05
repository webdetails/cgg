/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function boilerplate(type,settings, data, dataSettings) {
  var elem = document.createElement('g');
  elem.setAttribute('id','canvas');
  document.lastChild.appendChild(elem);
  settings.canvas = 'canvas';
  settings.showTooltips = false;
  var chart = new pvc[type](settings);
  chart.setData(data, dataSettings);
  chart.render();

  document.lastChild.appendChild(elem.lastChild);
  document.lastChild.setAttribute('width', settings.width);
  document.lastChild.setAttribute('height', settings.height);
  document.lastChild.setAttribute('viewBox', '0 0 ' + settings.width + ' ' + settings.height);
}
