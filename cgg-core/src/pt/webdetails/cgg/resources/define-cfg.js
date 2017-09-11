/*!
 * Copyright 2002 - 2017 Webdetails, a Pentaho company.  All rights reserved.
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
(function () {
  "use strict";

  /*global lib:true, load:true, params: true */

  var basePathCommonUI = "/plugin/common-ui/resources/web/";
  var basePathCdf = "/plugin/pentaho-cdf/js/";

  require.config({
    loadSync: loadSync,

    packages: [
      "cgg",
      "cdf",
      "ccc"
    ],

    // module -> path
    paths: {
      "underscore": basePathCommonUI + "underscore/underscore",
      "json": basePathCommonUI + "util/require-json/json",
      "text": basePathCommonUI + "util/require-text/text",
      "pentaho": basePathCommonUI + "pentaho",
      "cdf/_context": basePathCdf + "_context",
      "cdf/PentahoTypeContext": basePathCdf + "PentahoTypeContext",
      "cdf/components/ccc/config/cdf.vizApi.conf": basePathCdf + "components/ccc/config/cdf.vizApi.conf"
    },

    // module -> module
    map: {
      "*": {
        "jquery": "jquery-shim",
        "pentaho/type/theme": "pentaho/type/themes/crystal",
        "pentaho/visual/models/theme": "pentaho/visual/models/themes/crystal"
      },

      // Within `cdf`, `jquery` is provided by the small jquery shim.
      "cdf": {
        "cdf/util": "util" // use shared util module
      },

      "cgg": {
        "cgg/util": "util" // use shared util module
      }
    },

    shim: {
      "Base": {exports: "Base"}
    },

    config: {
      "cgg/main": {
        useGlobal: true
      },

      "pentaho/service": {
        "pentaho/visual/config/vizApi.conf": "pentaho.config.spec.IRuleSet",
        "cdf/components/ccc/config/cdf.vizApi.conf": "pentaho.config.spec.IRuleSet",
        "pentaho/config/impl/instanceOfAmdLoadedService": "pentaho.config.IService"
      },

      "pentaho/environment": {
        server: {
          root: params.get("CONTEXT_PATH")
        }
      },

      // setup requirejs text! plugin to use a mock xhr object that delegates to
      // the global `readResource`.
      // Used at least by json!, and the latter by pentaho/i18n!.
      "text": {
        useXhr: function () {
          return true;
        },
        env: "xhr",
        createXhr: function () {
          // A XHR mock
          return {
            _headers: {},

            open: function (method, url, async) {
              this._url = url;
            },

            setRequestHeader: function (name, value) {
              this._headers[name] = value;
            },

            send: function (body) {
              this.responseText = null;
              this.readyState = 0;
              this.status = 500;

              this.responseText = readResource(this._url);
              this.readyState = 4;
              this.status = 200;

              if (this.onreadystatechange) {
                this.onreadystatechange({});
              }
            }
          };
        }
      }
    }
  });

  // A dummy css plugin.
  // Supports dummy loading of tipsy.css, by jquery.tipsy.js.
  define("css", [], {
    load: function (cssId, req, load) {
      load(null);
    }
  });

  define("cdf/lib/CCC/def",      ["ccc!"], function(ccc) { return ccc.def; });
  define("cdf/lib/CCC/cdo",      ["ccc!"], function(ccc) { return ccc.cdo; });
  define("cdf/lib/CCC/pvc",      ["ccc!"], function(ccc) { return ccc.pvc; });
  define("cdf/lib/CCC/protovis", ["ccc!"], function(ccc) { return ccc.pv;  });

  var _load = load;
  var _lib = lib;

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
    if (m) {
      loader = _load;
      // `load` is relative to the printing CGG script's folder.
      var protocol = m[3];
      if (!protocol) {
        loader = _load;
      } else {
        switch (protocol.toLowerCase()) {
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
