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

(function() {

    "use strict";

    /* globals lib, load, require, _runTimeouts */

    lib("require.js");

    var reLoad = /(^\.)|(^\/)|(^[a-z]+:)/i;

    var requireReal = globalThis.require;

    requireReal.load = function (context, moduleName, url) {

        loadSync(url);

        // Support anonymous modules.
        context.completeLoad(moduleName);
    };

    // Called by RequireJS for the synchronous syntax, `require(id)`, instead of failing when the module is not yet
    // loaded. Loads the module, and its dependencies, using the asynchronous syntax, before returning.
    // Modules are loaded synchronously by `requireReal.load`. Making RequireJS's deferral, `nextTick`, synchronous as
    // well, the asynchronous syntax completes before returning, without running the event loop, which would
    // otherwise also run unrelated pending timeouts and micro-tasks.
    requireReal.get = function(context, id, relMap, localRequire) {
        var result;
        var error;
        var isDone = false;

        var nextTickPrev = context.nextTick;
        context.nextTick = syncTick;
        try {
            localRequire([id], function(_result) {
                isDone = true;
                result = _result;
            }, function(_error) {
                isDone = true;
                error = _error;
            });
        } finally {
            context.nextTick = nextTickPrev;
        }

        if(!isDone) {
            // Some loader plugin defers completion, e.g. via setTimeout or a promise.
            // Run the event loop, only until done.
            _runTimeouts(function() { return isDone; });
        }

        if(error) {
            throw error;
        }

        return result;
    };

    function syncTick(fn) {
        fn();
    }

    // Synchronous load function for cgg.
    // ./      -> load
    // ../     -> load
    // foo:    -> load
    // res:    -> load(remaining_path)
    // lib:    -> lib(remaining_path)
    // plugin: -> ???(remaining_path) ~ pentaho system...
    // /       -> load
    // //      -> load
    // else    -> lib
    function loadSync(path) {
        var loader;
        var m = reLoad.exec(path);
        if (m) {
            loader = load;
            // `load` is relative to the printing CGG script's folder.
            var protocol = m[3];
            if (!protocol) {
                loader = load;
            } else {
                switch (protocol.toLowerCase()) {
                    case 'res:':
                        loader = load;
                        path = path.substr(protocol.length);
                        break;

                    case 'lib:':
                        loader = lib;
                        path = path.substr(protocol.length);
                        break;

                    default:
                        loader = load;
                }
            }
        } else {
            loader = lib;
        }

        return loader(path);
    }
}());
