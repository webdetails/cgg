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
define([
  'cgg',
  '../../dash',
  './SvgComponent',
  './BaseCccComponent.ext',
  'underscore'
], function (cgg, dash, SvgComponent, BaseCccComponentExt, _) {

  // ATTENTION: A part of this code is synchronized with:
  // cdf/core-js/src/main/javascript/cdf/components/ccc/BaseCccComponent.js

  var DEFAULT_QUANTITATIVE_PALETTE_ID = "pentaho/visual/color/palettes/quantitativeBlue3";

  var BaseCccComponent = SvgComponent.extend({
    chart: null,
    ccc: null,

    /**
     * The identifier of the matching visualization view type, if any; `null` if none.
     *
     * When uninitialized, the value is `undefined`.
     *
     * @type {?string|undefined}
     * @private
     */
    __cccVizViewId: undefined,

    /**
     * Gets the identifier of the matching visualization view type, if any.
     *
     * @returns {?string} The visualization type identifier, `null` otherwise.
     * @private
     */
    __getMatchingVizViewId: function () {

      if (this.__cccVizViewId === undefined) {

        // NOTE: This part is different from CDF getCccType...
        var cccClassName = def.qualNameOf(this.getCccType(this.type)).name;
        this.__cccVizViewId = BaseCccComponentExt.getMatchingVizViewId(cccClassName, this.chartDefinition);
      }

      return this.__cccVizViewId;
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

      globalThis.pv = ccc.pv;
      globalThis.def = def;
      globalThis.cdo = ccc.cdo;
      globalThis.pvc = pvc;

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

      this.preProcessChartDefinition();

      var extensionsPromise = this.__applyVizApiStyles
          ? BaseCccComponentExt.getExtensionsPromise(this.__getMatchingVizViewId())
          : Promise.resolve(null);

      extensionsPromise
          .then(_.bind(this.base, this, cdaData))
          .then(_.bind(this.endExec, this), _.bind(this.failExec, this));

      cgg.run();
    },

    /**
     * Gets a value that indicates if the Viz. API styles should be applied.
     *
     * @type {boolean}
     * @private
     */
    __applyVizApiStyles: false,

    preProcessChartDefinition: function () {

      var applyVizApiStyles = false;

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
              var p2 = 'secondAxis' + (m[1] || '');
              cd[p2] = cd[p];
              delete cd[p];
            }
          }
        } else if (compatVersion >= 3) {
          applyVizApiStyles = this.__getMatchingVizViewId() !== null;
        }

        cd.extensionPoints = dash.propertiesArrayToObject(cd.extensionPoints);
      }

      this.__applyVizApiStyles = applyVizApiStyles;
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

      // Handle overrides
      if (this.__applyVizApiStyles) {
        // apply colors if that is intended
        if (isArrayEmpty(cd.colors)) {

          if (isArrayEmpty(cd.continuousColorAxisColors))  {
            cd.continuousColorAxisColors = BaseCccComponentExt.getColors(DEFAULT_QUANTITATIVE_PALETTE_ID);
          }

          cd.discreteColorAxisColors = BaseCccComponentExt.getColors();
        }
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

  function isArrayEmpty(values) {
    return values == null || values.length === 0;
  }
});

