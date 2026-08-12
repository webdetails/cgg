/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



lib('protovis-bundle.js');

cgg.init();

var pvc = require('cdf/lib/CCC/pvc');
var chartType = String(params.get('chartType') || 'Line');

var resultset = [];
var value = 1;

for (var year = 2003; year <= 2005; year++) {
  var lastMonth = year === 2005 ? 5 : 12;
  for (var month = 1; month <= lastMonth; month++) {
    var monthText = month < 10 ? '0' + month : String(month);
    resultset.push(['North', year + '-' + monthText + '-01', value++]);
    resultset.push(['South', year + '-' + monthText + '-01', value++]);
  }
}

var data = {
  resultset: resultset,
  metadata: [{
    colIndex: 0,
    colType: 'String',
    colName: 'Series'
  }, {
    colIndex: 1,
    colType: 'String',
    colName: 'Categories'
  }, {
    colIndex: 2,
    colType: 'Numeric',
    colName: 'Value'
  }]
};

var root = document.lastChild;
var canvas = document.createElement('g');
canvas.setAttribute('id', 'canvas');
root.appendChild(canvas);

var chartClasses = {
  Line: pvc.LineChart,
  StackedLine: pvc.StackedLineChart,
  StackedArea: pvc.StackedAreaChart,
  Dot: pvc.DotChart
};

var ChartClass = chartClasses[chartType];
if (!ChartClass) {
  throw new Error('Unsupported chartType: ' + chartType);
}

var chart = new ChartClass({
  width: 800,
  height: 600,
  canvas: 'canvas',
  orientation: 'horizontal',
  timeSeries: true,
  timeSeriesFormat: '%Y-%m-%d',
  showDots: true,
  showTooltips: false,
  legend: false,
  xAxisSize: 30,
  yAxisSize: 30,
  xAxisFullGrid: true,
  yAxisFullGrid: true
});

chart.setData(data, {
  crosstabMode: false,
  seriesInRows: false
});

chart.render();

var error = typeof chart.getLastRenderError === 'function'
  ? chart.getLastRenderError()
  : null;
if (error) {
  throw error;
}

if (canvas.lastChild) {
  root.appendChild(canvas.lastChild);
}
root.setAttribute('width', '800');
root.setAttribute('height', '600');
root.setAttribute('viewBox', '0 0 800 600');