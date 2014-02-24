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
    './util',
    './interop'
], function(util, interop) {

    var CggParameters = function() { this._params = {}; };

    CggParameters.prototype = {
        names: function()         { return Object.keys(this._params); },
        get:   function(pname)    { return this._params[pname]; },
        put:   function(pname, v) { this._params[pname] = v; return this; },
        each:  function(f, x) {
            this
            .names()
            .forEach(function(pname) { f.call(x, this[pname], pname); }, this._params);
        }
    };

    return createParameters;

    // LIB

    function createParameters(cgg) {
        var _parameterDefaults = {};
        var _lastJavaParams;

        importGlobal();

        if(cgg.useGlobal) {
            var params = cgg.params;

            util.global.params = params;

            params.each(function(v, name) {
                var dv = _parameterDefaults[name];
                if(dv != null) util.global[parameterVar(name)] = v;
            });
        }

        return {
            init:     initParameter,
            doImport: importGlobal,
            doInit:   initParameters,
            get:      getParameterValue,
            set:      setParameterValue
        };

        // ------------

        // Store default for later use in initParameters
        function initParameter(name, dv) {
            _parameterDefaults[name] = dv;
            return initParameter;
        }

        function importGlobal() {
            var changed;
            var params = util.global.params;
            if(!params) {
                // Nothing to import.
                // Create cggParams if there ain't one.
                params = cgg.params;
                if(!params) {
                    params = cgg.params = new CggParameters();
                    changed = true;
                }
            } else if(!(params instanceof CggParameters)) {
                // Only import if:
                // there are no cgg.params, or
                // the global params are != from
                // the last converted ones, if any.
                if(!cgg.params || !_lastJavaParams || (_lastJavaParams !== params)) {
                    changed = true;
                    _lastJavaParams = params;
                    params = cgg.params = paramsToJs(params);
                }
            }

            if(changed && cgg.useGlobal) util.global.params = params;

            return params;
        }

        // Imports (global) parameters, if not already.
        // Applies default values.
        // Logs parameters (cgg.debug > 2).
        function initParameters() {
            var out;
            if(cgg.debug > 2) out = ["CGG - PARAMETERS"];

            var params = importGlobal();

            params.each(function(v, name) {
                var defaulted;
                var dv = _parameterDefaults[name];
                if(dv != null) {
                    if(v == null || v === '') {
                        v = dv;
                        defaulted = true;
                    }

                    // Also, define a global variable for this parameter
                    // (only for DS parameters)
                    if(cgg.useGlobal) util.global[parameterVar(name)] = v;

                    params.put(name, v);
                }

                // TODO: could apply JSON.stringify here...
                out && out.push(
                    "  " + name + " = " +
                    cgg.win.console.stringify(v) +
                    (defaulted ? ' (default)' : ''));
            });

            out && cgg.print(out.join("\n"));

            return params;
        }

        // Component data source parameters namespace
        function getParameterValue(name) {
            if(cgg.useGlobal) {
                // Prefer global value, if there is one.
                var varName = parameterVar(name);
                if(varName in cgg.global) return cgg.global[varName];
            }
            return cgg.params.get(name);
        }

        function setParameterValue(name, v) {
            if(cgg.useGlobal) {
                // Sync global value, if there is one already
                var varName = parameterVar(name);
                if(varName in cgg.global) cgg.global[varName] = v;
            }
            cgg.params.put(name, v);
        }
    }

    function paramsToJs(params) {

        if(!(params instanceof CggParameters)) {
              var jsParams = new CggParameters();

              params.keySet().toArray()
              .forEach(function(name) {
                    jsParams.put(name, interop.javaToJS(params.get(name)));
              });

              params = jsParams;
        }

        return params;
    }

    function parameterVar(name) { return 'param' + name; }
});