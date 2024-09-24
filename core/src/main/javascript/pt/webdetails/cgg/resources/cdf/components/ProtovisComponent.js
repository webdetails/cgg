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
define([
    'require',
    'cgg',
    '../dash',
    './ccc/SvgComponent'
], function(require, cgg, dash, SvgComponent) {

    var ProtovisComponent = SvgComponent.extend({
        vis: null,
        pv:  null,

        init: function() {
            this.base();

            this.pv = require('ccc!protovis-standalone');
            globalThis.pv = this.pv;
        },

        renderCore: function(cdaData) {
            if(cgg.debug > 2) {
                cgg.print("Protovis - main options\n" +
                      "  Width:     " + this.width + "\n" +
                      "  Height:    " + this.height + "\n" +
                      "  NoChartBg: " + this.noChartBg);
            }

            var vis = new this.pv.Panel()
                .width (this.width)
                .height(this.height);

            this.vis = vis;
            this.customfunction(vis, cdaData);
            vis.root.render();
        },

        processdata: function(cdaData) {
            this.render(cdaData);
        },

        fixCanvasSize: function() {
            if(this.vis) this.base();
        }
    });

    dash.registerControlClass('protovis', ProtovisComponent);

    return ProtovisComponent;
});
