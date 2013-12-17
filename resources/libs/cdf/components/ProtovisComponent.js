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
    'require',
    'cgg',
    'util',
    '../dash',
    './SvgComponent'
], function(require, cgg, util, dash, SvgComponent) {

    var ProtovisComponent = SvgComponent.extend({
        vis: null,
        pv:  null,

        init: function() {
            this.base();

            this.pv = require('ccc!protovis-standalone');
            if(cgg.useGlobal) {
                util.global.pv = this.pv;
            }
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