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
(function() {
    "use strict";

    /*global lib:true, load:true */

    require.config({
        loadSync: loadSync,

        packages: ['cgg', 'cdf', 'ccc'],

        config: {
            'cgg/main': {
                useGlobal: true
            }
        },

        // module -> path
        //paths: {
            //'ccc': 'cdf/lib/CCC/loader'
        //},

        // module -> module
        map: {
            '*': {
                'cdf/lib/CCC': 'ccc',
                'jquery': 'jquery-shim'
            },

            // Within `cdf`, `jquery` is provided by the small jquery shim.
            'cdf': {
                'cdf/util': 'util' // use shared util module
            },

            'cgg': {
                'cgg/util': 'util' // use shared util module
            }
        },

        shim: {
            'Base': {exports: 'Base'}
        }
    });

    var _load = load;
    var _lib  = lib;

    var _reLoad = /(^\.)|(^\/)|(^[a-z]+:)/i;

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
        var m = _reLoad.exec(path);
        if(m) {
            loader = _load;
            // `load` is relative to the printing CGG script's folder.
            var protocol = m[3];
            if(!protocol) {
                loader = _load;
            } else {
                switch(protocol.toLowerCase()) {
                    case 'res:':
                        loader = _load;
                        path = path.substr(protocol.length);
                        break;

                    case 'lib:':
                        loader = _lib;
                        path = path.substr(protocol.length);
                        break;

                    default:
                        loader = _load;
                }
            }
        } else {
            loader = _lib;
        }

        return loader(path);
    }
}());