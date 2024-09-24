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
    './interop'
], function(interop) {

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

        var params = cgg.params;

        globalThis.params = params;

        params.each(function(v, name) {
            var dv = _parameterDefaults[name];
            if(dv != null) globalThis[parameterVar(name)] = v;
        });

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
            var params = globalThis.params;
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

            if(changed) globalThis.params = params;

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
                    globalThis[parameterVar(name)] = v;

                    params.put(name, v);
                }

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
            // Prefer global value, if there is one.
            var varName = parameterVar(name);
            if(varName in globalThis) return globalThis[varName];

            return cgg.params.get(name);
        }

        function setParameterValue(name, v) {
            // Sync global value, if there is one already
            var varName = parameterVar(name);
            if(varName in globalThis) globalThis[varName] = v;

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
