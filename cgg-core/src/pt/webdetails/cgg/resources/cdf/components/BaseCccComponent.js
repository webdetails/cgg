/*!
 * Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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
define([
  'cgg',
  'util',
  '../dash',
  './SvgComponent',
  './BaseCccComponent.ext'
], function (cgg, util, dash, SvgComponent, BaseCccComponentExt) {

  var BaseCccComponent = SvgComponent.extend({
    chart: null,
    ccc: null,

    _cccVizName: null,

    /**
     * Gets and assigns the Ccc Visualization name
     *
     * @returns {String|undefined} The Viz Type name if it is a valid visualization, undefined otherwise
     */
    getCccVisualizationName: function () {
      console.log("getCccVisualizationName -> " + this.type);
      if (!this._cccVizName && this.type) {
        var cccTypeName = def.qualNameOf(this.getCccType(this.type)).name;
        this._cccVizName = BaseCccComponentExt.getVizDigestedName(cccTypeName, this.chartDefinition);
      }
      console.log("getCccVisualizationName = " + this._cccVizName);
      return this._cccVizName;
    },

    init: function () {

      this.base();

      var ccc = this.ccc = require('ccc!');
      var pvc = ccc.pvc;
      var def = ccc.def;

      // Not all versions of CCC have def.describe/pvc.stringify.
      var describe = def.describe || pvc.describe;
      if (def.describe)
        cgg.win.console.stringify = function (s) {
          return describe(s, {ownOnly: false});
        };

      if (cgg.useGlobal) {
        var global = util.global;
        global.pv = ccc.pv;
        global.def = def;
        global.cdo = ccc.cdo;
        global.pvc = pvc;
      }

      // Sync def/pvc log, taking older ccc versions into account.
      if (def.setDebug) def.setDebug(cgg.debug);
      else if (pvc.setDebug) pvc.setDebug(cgg.debug);
      else pvc.debug = cgg.debug;

      var cd = this.chartDefinition;
      var multiChartOverflow = cgg.params.get('multiChartOverflow');
      if (multiChartOverflow) cd.multiChartOverflow = multiChartOverflow;
    },

    filterDataSourceParameter: function (name) {
      if (!this.base(name)) return false;

      switch (name) {
        case 'multiChartOverflow':
          return false;
      }
      return true;
    },

    render: function (cdaData) {
      console.log("BaseCccComponent.render");
      this.preProcessChartDefinition();

      try {
        this.base(cdaData, {
          legendPosition: "top"
        });
        this.endExec();
      } catch(e) {
        this.failExec(e);
      }

      //BaseCccComponentExt.getExtensionsPromise(this.getCccVisualizationName(), this._vizApiStyles)
      //    .then(_.bind(this.base, this, cdaData))
      //    .then(_.bind(this.endExec, this), _.bind(this.failExec, this));
    },

    preProcessChartDefinition: function () {
      var cd = this.chartDefinition;
      if (cd) {
        var pvc = this.ccc.pvc;

        // Obtain effective compatVersion
        var compatVersion = cd.compatVersion;
        if (compatVersion == null)
          compatVersion = typeof pvc.defaultCompatVersion === 'function'
              ? pvc.defaultCompatVersion()
              : 1;

        if (compatVersion <= 1) {
          // Properties that are no longer registered in the component
          // and that had a name mapping.
          // The default mapping, for unknown properties, doesn't work.
          if ('showLegend' in cd) {
            cd.legend = cd.showLegend;
            delete cd.showLegend;
          }

          // Don't presume cd props must be own
          for (var p in cd) {
            var m = /^barLine(.*)$/.exec(p);
            if (m) {
              p2 = 'secondAxis' + (m[1] || '');
              cd[p2] = cd[p];
              delete cd[p];
            }
          }
        } else if (compatVersion >= 3) {
          this._vizApiStyles = BaseCccComponentExt.isValidVisualization(this.getCccVisualizationName());
        }

        cd.extensionPoints = dash.propertiesArrayToObject(cd.extensionPoints);
      }
    },

    renderCore: function (cdaData, cd) {
      cd.interactive = false;

      if (cgg.debug > 2) {
        print("CCC - main options\n" +
            "  Width:     " + this.width + "\n" +
            "  Height:    " + this.height + "\n" +
            "  NoChartBg: " + this.noChartBg + "\n" +
            "  MultiChartOverflow: " + (cd.multiChartOverflow || ''));
      }

      if (this._vizApiStyles && (!cd.colors || (cd.colors && cd.colors.length == 0))) {
//        cd.colors = BaseCccComponentExt.getColors('default');
      }

      var ChartType = this.getCccType(this.type);
      var chart = new ChartType(cd);
      this.chart = chart;
      chart.setData(cdaData);
      chart.render();
    },

    fixCanvasSize: function () {
      if (this.chart) {
        // When no data, no basePanel.
        var basePanel = this.chart.basePanel;
        if (basePanel) {
          this.width = basePanel.width;
          this.height = basePanel.height;
        }

        this.base();
      }
    },

    getCccType: function (type) {
      if (type) {
        var pvc = this.ccc.pvc;
        var className = type.replace(/^ccc(.*)$/, '$1');
        return pvc[className];
      }
    }
  });

  // ------------

  // Make this class hook to "ccc" prefixed component types
  dash.registerControlClassHandler(function (typeName) {
    if ((/^ccc/i).test(typeName)) return BaseCccComponent;
  });

  return BaseCccComponent;
});

