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
    './SvgComponent'
], function(cgg, util, dash, SvgComponent) {

    var BaseCccComponent = SvgComponent.extend({
        chart: null,
        ccc:   null,

        init: function() {

            this.base();

            var ccc = this.ccc = require('ccc!');
            var pvc = ccc.pvc;

            if(cgg.useGlobal) {
                var global = util.global;
                global.pv  = ccc.pv;
                global.def = ccc.def;
                global.pvc = pvc;
            }

            if(pvc.setDebug) pvc.setDebug(cgg.debug);
            else pvc.debug = cgg.debug;

            var cd = this.chartDefinition;
            var multiChartOverflow = cgg.params.get('multiChartOverflow');
            if(multiChartOverflow) cd.multiChartOverflow = multiChartOverflow;
        },

        filterDataSourceParameter: function(name) {
            if(!this.base(name)) return false;

            switch(name) {
                case 'multiChartOverflow': return false;
            }
            return true;
        },

        render: function(cdaData) {
            this.preProcessChartDefinition();
            this.base(cdaData);
        },

        preProcessChartDefinition: function() {
            var cd = this.chartDefinition;
            if(cd) {
                var pvc = this.ccc.pvc;

                // Obtain effective compatVersion
                var compatVersion = cd.compatVersion;
                if(compatVersion == null)
                    compatVersion = typeof pvc.defaultCompatVersion === 'function'
                        ? pvc.defaultCompatVersion()
                        : 1;

                if(compatVersion <= 1) {
                    // Properties that are no longer registered in the component
                    // and that had a name mapping.
                    // The default mapping, for unknown properties, doesn't work.
                    if('showLegend' in cd) {
                        cd.legend = cd.showLegend;
                        delete cd.showLegend;
                    }

                    // Don't presume cd props must be own
                    for(var p in cd) {
                        var m = /^barLine(.*)$/.exec(p);
                        if(m) {
                            p2 = 'secondAxis' + (m[1] || '');
                            cd[p2] = cd[p];
                            delete cd[p];
                        }
                    }
                }

                cd.extensionPoints = dash.propertiesArrayToObject(cd.extensionPoints);
            }
        },

        renderCore: function(cdaData) {
            var cd = this.chartDefinition;
            cd.interactive = false;

            if(cgg.debug > 2) {
                print("CCC - main options\n" +
                      "  Width:     " + this.width     + "\n" +
                      "  Height:    " + this.height    + "\n" +
                      "  NoChartBg: " + this.noChartBg + "\n" +
                      "  MultiChartOverflow: " + (cd.multiChartOverflow || ''));
            }

            var ChartType = this.getCccType(this.type);
            var chart = new ChartType(cd);
            this.chart = chart;
            chart.setData(cdaData);
            chart.render();
        },

        fixCanvasSize: function() {
            if(this.chart) {
                // When no data, no basePanel.
                var basePanel = this.chart.basePanel;
                if(basePanel) {
                    this.width  = basePanel.width ;
                    this.height = basePanel.height;
                }

                this.base();
            }
        },

        getCccType: function(type) {
            if(type) {
                var pvc = this.ccc.pvc;
                var className = type.replace(/^ccc(.*)$/, '$1');
                return pvc[className];
            }
        }
    });

    // ------------

    // Make this class hook to "ccc" prefixed component types
    dash.registerControlClassHandler(function(typeName) {
        if((/^ccc/i).test(typeName)) return BaseCccComponent;
    });

    // Not all versions of CCC have pvc.stringify.
    // if(pvc.stringify)
    //     cgg.win.console.stringify = function(s) {
    //         return pvc.stringify(s, {ownOnly: false});
    //     };

    return BaseCccComponent;
});