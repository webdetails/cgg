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

  // A dummy css plugin.
  // Supports dummy loading of tipsy.css, by jquery.tipsy.js.
  define('css', [], {
    load: function (cssId, req, load) {
      load(null);
    }
  });

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

(function () {
  // Additional AMD configuration

  var basePathCommonUI = "/plugin/common-ui/resources/web/";
  var basePathCdf = "/plugin/pentaho-cdf/js/";

  var requireCfg = {
    paths: {
      "underscore": basePathCommonUI + "underscore/underscore",
      "json": basePathCommonUI + "util/require-json/json",
      "text": basePathCommonUI + "util/require-text/text",
      "pentaho": basePathCommonUI + "pentaho",
      "pentaho/data": basePathCommonUI + "pentaho/data",
      "pentaho/visual": basePathCommonUI + "pentaho/visual",
      "pentaho/config": basePathCommonUI + "pentaho/config",
      "pentaho/context": basePathCommonUI + "pentaho/context",
      "pentaho/debug": basePathCommonUI + "pentaho/debug",
      "pentaho/service": basePathCommonUI + "pentaho/service",
      "pentaho/i18n": basePathCommonUI + "pentaho/i18n",
      "pentaho/lang": basePathCommonUI + "pentaho/lang",
      "pentaho/util": basePathCommonUI + "pentaho/util",
      "pentaho/shim": basePathCommonUI + "pentaho/shim",
      "pentaho/type": basePathCommonUI + "pentaho/type",
      "pentaho/ccc": basePathCommonUI + "pentaho/ccc",
      "cdf/PentahoTypeContext": basePathCdf + "PentahoTypeContext"
    },
    packages: [],
    config: {
      "pentaho/service": {
        "pentaho/visual/config/vizApi.conf": "pentaho.config.spec.IRuleSet",
        "pentaho/config/impl/instanceOfAmdLoadedService": "pentaho.config.IService"
      },

      "pentaho/context": {
        server: {
          url: "${CONTEXT_PATH}"
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
    },
    map: {
      "*": {
        "pentaho/type/theme": "pentaho/type/themes/crystal",
        "pentaho/visual/models/theme": "pentaho/visual/models/themes/crystal"
      }
    }
  };

  requireCfg.packages.push({"name": "pentaho/visual/base", "main": "model"});

  require.config(requireCfg);
}());
