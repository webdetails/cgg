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

// ATTENTION: this file is now **deprecated** and intended to be used only
// by Analyzer <= 4.8.2 print scripts.
//
// Use cdf-env.js instead!

print("cccVersion: " + String(params.get('cccVersion')));

if(params.get('cccVersion') != '2.0-analyzer') {

    print(
    "ATTENTION!\n" +
    "Using 'protovis-bundle.js' CGG print script.\n" +
    "This script is *deprecated* and will be dropped in a future release.\n" +
    "Please regenerate the print script by re-saving the associated CDE dashboard.");

    lib('cdf-env.js');


    // <= ~2013-09-12 Legacy scripts; did not execute pre/postExec and received data directly.
    renderCccFromComponent = function (component, data) {
        cgg.init(component);

        var CggLegacy1CccComponent = require('cdf/components/CggLegacy1CccComponent');

        Dashboards.bindControl(component, CggLegacy1CccComponent);

        component.setPreFetchedData(data);

        component.update();
    };

} else {
    lib('cgg-env.js');
    // To be backward-compatible with Analyzer <= 4.8.2 print scripts,
    // there's some processing of `load` calls like the following
    // that must be done:
    //   load("../../common-ui/resources/web/vizapi/DataTable.js");
    // and
    //   load("../../common-ui/resources/web/vizapi/ccc/ccc_wrapper.js");
    //
    // Also, need to remove ".js" extensions.
    // Then, they'll look like common-ui AMD module ids:
    //  "common-ui/vizapi/DataTable"
    //  "common-ui/vizapi/ccc/ccc_wrapper"

    var __commonUiBasePath = '../../common-ui/resources/web';
    var __load = load;
    load = function(path) {
        if(path && path.indexOf(__commonUiBasePath) === 0) {
            path = 'common-ui/' +
                path
                .substr(__commonUiBasePath.length + 1)
                .replace(/\.js$/i, '');

            // Need to require the files because of:
            // * path mapping,
            // * anonymous modules not loaded through require fail to load,
            // * just defining a module doesn't load it.
            require(path);
        } else {
            __load(path);
        }
    };

    // Additional configurations
    require.config({
        paths: {
            'common-ui': 'res:' + __commonUiBasePath
        },
        shim: {
            // An additional hack is needed here as the VizController
            // does not "define" itself, yet, ccc_wrapper requires it,
            // and the cgg AMD loader doesn't like it that way...
            'common-ui/vizapi/VizController': {
                init: function() { return pentaho.VizController; }
            }
        }
    });


    // ccc_wrapper.js et al use pen.define/pen.require shims.
    // The shims are defined before including this file.
    // The following overrides those methods, directing to the new AMD loader.
    if(typeof pen !== 'undefined') {
        pen.require = require;
        pen.define  = define;
    }

    // Analyzer print scripts already export
    //  the ccc globals (pv, pvc and def) to modules named
    //  as required by ccc_wrapper et al:
    //
    //  "cdf/lib/CCC/protovis",
    //  "cdf/lib/CCC/pvc-d1.0" and
    //  "cdf/lib/CCC/def"
    //
    // Yet, that is done right after this file is loaded, and
    //  the CCC globals are only exported when a CDF/CCC component is actually created
    // (which Analyzer doesn't even create...).
    // Bottom-line, we must explicitly export these.
    require(['ccc!', 'jquery'], function(ccc, _$) {
        pv  = ccc.pv;
        pvc = ccc.pvc;
        def = ccc.def;

        jQuery = $ = _$;
    });
}