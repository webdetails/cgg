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
    './events',
    './dom',
    './parameters',
    './dataSource'
], function(events, dom, parameters, dataSource) {
    "use strict";

    var _trigger = events();

    // Fields backing get/set properties
    var _debugLevel = readDebugLevel(globalThis.params, 1);
    var _win, _renderer;

    var cgg = {
        print:    globalThis.print,
        trigger:  _trigger,
        on:       _trigger.on,
        dataSource: dataSource,
        init:     init,
        run:      dom.run,
        render:   render,
        document: dom.document,
        element:  dom.element,
        style:    dom.style,
        registerRenderer: function(v) { _renderer = v; },
        get win()       { return globalThis; },
        get debug()     { return _debugLevel; }
    };

    // Create the non-global window.
    _win = dom.window(
        cgg,
        globalThis._document,
        dom.console(/*printer*/cgg)
    );

    // Create parameters
    var _params = parameters(cgg);

    cgg.initParameter     = _params.init;
    cgg.getParameterValue = _params.get;
    cgg.setParameterValue = _params.set;

    cgg.utils = {
        loadSvg:      function(path) { return dom.loadSvg(path); },
        initDocument: function(path) { _win.navigate(path); },
        printNode:    function(node) { _win.console.printNode(node); }
    };

    globalThis.cgg = cgg;

    return cgg;

    // LIB

    function init() {
        _params.doInit();
        _trigger('cgg:init');
    }

    function render(component) {
        if(cgg.debug > 2) cgg.print("CGG - RENDER " + component.type);

        cgg.init();

        if(_renderer) _renderer.render(component);
    }

    function readDebugLevel(params, dv) {
        // ATTENTION: has to be == and !=
        // because params are not yet converted.
        var debug = params.get('debug');
        if(debug == null || debug == '') return dv;
        if(debug != 'true') return 0;

        var level = parseDebugLevel(params.get('debugLevel'));
        return isNaN(level) ? dv : level;
    }

    function parseDebugLevel(level) {
        // NOTE: +null === 0
        if(level == null) return NaN;

        level = +level; // to number
         // can be NaN
         // can +/-Infinite
         // (NaN < 0) === false
        return level < 0 ? 0 : level;
    }
});
