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

define([
  'cgg',
  '../UnmanagedComponent',
  'underscore',
  'jquery'
], function (cgg, UnmanagedComponent, _, $) {

  var SvgComponent = UnmanagedComponent.extend({
    init: function () {
      var cd = this.chartDefinition;

      this.width = cd.width = parseFloat(cgg.params.get('width')) || +cd.width || +this.width;

      this.height = cd.height = parseFloat(cgg.params.get('height')) || +cd.height || +this.height;
    },

    // The SVG Element
    getRootElement: function () {
      return cgg.win.document.documentElement;
    },

    filterDataSourceParameter: function (pname) {
      if (!this.base(pname)) {
        return false;
      }

      switch (pname) {
        case 'width':
        case 'height':
        case 'noChartBg':
          return false;
      }

      return true;
    },

    update: function () {
      this.init();
      this.renderChart();
      this.fixCanvasSize();
    },

    renderChart: function () {
      if (this.detectQueryType()) {
        this.beginQuery(this.queryDefinition || this.chartDefinition, _.bind(this.render, this));
      } else if (this.valuesArray != null) {
        this.execute(_.bind(function () { this.render(this.valuesArray); }, this));
      } else {
        // initialize the component only
        this.execute(_.bind(this.render, this));
      }
    },

    render: function (cdaData, externalChartDefinition) {
      var cd = $.extend({}, this.chartDefinition);

      // Handle cleanups
      if (this.__applyVizApiStyles) {
        // special case for this array which $.extend does not smash
        if (cd.baseAxisLabelDesiredAngles && cd.baseAxisLabelDesiredAngles.length == 0) {
          cd.baseAxisLabelDesiredAngles = undefined;
        }

        // special case for this array which $.extend does not smash
        if (cd.orthoAxisLabelDesiredAngles && cd.orthoAxisLabelDesiredAngles.length == 0) {
          cd.orthoAxisLabelDesiredAngles = undefined;
        }
      }

      if (externalChartDefinition) {
        cd = $.extend(externalChartDefinition, cd);
      }

      this.width = +cd.width || +this.width;
      this.height = +cd.height || +this.height;
      this.noChartBg = cgg.params.get('noChartBg') === 'true';

      // Chart Background
      var bg;
      if (!this.noChartBg) {
        bg = cgg.win.document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        bg.setAttribute('x', '0');
        bg.setAttribute('y', '0');
        bg.setAttribute('width', this.width);
        bg.setAttribute('height', this.height);
        bg.setAttribute('style', 'fill:white');
        this.getRootElement().appendChild(bg);
      }

      this._bg = bg;

      (cgg.debug > 2) && cgg.print("BEG Render");
      try {
        this.renderCore(cdaData, cd);
      } finally {
        (cgg.debug > 2) && cgg.print("END Render");
      }
    },

    // Do something useful
    renderCore: function (cdaData) {
    },

    fixCanvasSize: function () {
      // Set the SVG element with the final chart width/height
      //  so that the PNGTranscoder can detect the image size.
      var svgElem = this.getRootElement();
      svgElem.setAttribute('width', this.width);
      svgElem.setAttribute('height', this.height);

      // Also, fix the bg size
      var bg = this._bg;
      if (bg) {
        bg.setAttribute('width', this.width);
        bg.setAttribute('height', this.height);
      }
    }
  });

  return SvgComponent;
});

