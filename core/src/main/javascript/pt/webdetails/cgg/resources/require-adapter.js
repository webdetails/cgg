/*!
 * Copyright 2017-2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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
(function() {

    "use strict";

    /* globals lib, load, require */

    lib("timer.js");

    lib("require.js");

    var reLoad = /(^\.)|(^\/)|(^[a-z]+:)/i;

    var requireReal = globalThis.require;

    requireReal.load = function (context, moduleName, url) {

        loadSync(url);

        // Support anonymous modules.
        context.completeLoad(moduleName);
    };

    var requireFake = globalThis.requirejs = globalThis.require = function(id) {

        var result;
        var error;

        if(typeof id === "string") {
            // Synchronous syntax.
            requireReal.call(this, [id], function(_result) {
                result = _result;
            }, function(_error) {
                error = _error;
            });

            globalThis.__timer__run__();

            if(error) { // only in sync case...
                throw error;
            }
        } else {
            // Asynchronous syntax.
            result = requireReal.apply(this, arguments);
            if(result === requireReal) {
                result = requireFake;
            }
        }

        return result;
    };

    Object.keys(requireReal).forEach(function(p) {
        requireFake[p] = requireReal[p];
    });

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
