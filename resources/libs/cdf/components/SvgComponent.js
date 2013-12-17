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
    './UnmanagedComponent'
], function(cgg, UnmanagedComponent) {

    var SvgComponent = UnmanagedComponent.extend({
        init: function() {
            var cd = this.chartDefinition;

            this.width  = cd.width  =
                parseFloat(cgg.params.get('width' )) || +cd.width  || +this.width;

            this.height = cd.height =
                parseFloat(cgg.params.get('height')) || +cd.height || +this.height;
        },

        // The SVG Element
        getRootElement: function() {
            return cgg.win.document.documentElement;
        },

        filterDataSourceParameter: function(pname) {
            if(!this.base(pname)) { return false; }

            switch(pname) {
                case 'width':
                case 'height':
                case 'noChartBg':
                    return false;
            }

            return true;
        },

        update: function() {
            this.init();
            this.renderChart();
            this.fixCanvasSize();
        },

        renderChart: function() {
            var me = this;
            if(this.detectQueryType()) {
                me.triggerQuery(
                    me.queryDefinition || me.chartDefinition,
                    function(cdaData) { me.render(cdaData); });
            } else if(me.valuesArray != null) {
                me.synchronous(function() { me.render(me.valuesArray); });
            } else {
                // initialize the component only
                me.synchronous(function() { me.render(); });
            }
        },

        render: function(cdaData) {
            var cd = this.chartDefinition;
            this.width  = +cd.width  || +this.width;
            this.height = +cd.height || +this.height;
            this.noChartBg = cgg.params.get('noChartBg') === 'true';

            // Chart Background
            var bg;
            if(!this.noChartBg) {
                bg = cgg.win.document.createElementNS('http://www.w3.org/2000/svg', 'rect');
                bg.setAttribute('x',      '0');
                bg.setAttribute('y',      '0');
                bg.setAttribute('width',  this.width );
                bg.setAttribute('height', this.height);
                bg.setAttribute('style',  'fill:white');
                this.getRootElement().appendChild(bg);
            }

            this._bg = bg;

            (cgg.debug > 2) && cgg.print("BEG Render");
            try {
                this.renderCore(cdaData);
            } finally {
                (cgg.debug > 2) && cgg.print("END Render");
            }
        },

        // Do something useful
        renderCore: function(cdaData) {},

        fixCanvasSize: function() {
            // Set the SVG element with the final chart width/height
            //  so that the PNGTranscoder can detect the image size.
            var svgElem = this.getRootElement();
            svgElem.setAttribute('width',  this.width);
            svgElem.setAttribute('height', this.height);

            // Also, fix the bg size
            var bg = this._bg;
            if(bg) {
                bg.setAttribute('width',  this.width);
                bg.setAttribute('height', this.height);
            }
        }
    });

    return SvgComponent;
});