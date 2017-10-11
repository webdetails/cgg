/*!
 * Copyright 2002 - 2017 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

  /* globals params, readResource */

  var basePathCommonUI = "/plugin/common-ui/resources/web/";
  var basePathCdf = "/plugin/pentaho-cdf/js/";
  var basePathKarafConfig = "/system/karaf/config/web-client/";

  require.config({

    baseUrl: "",

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
      "cdf/components/ccc/config/cdf.vizApi.conf": basePathCdf + "components/ccc/config/cdf.vizApi.conf",
      "pentaho/config/deploy": basePathKarafConfig
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

      // ATTENTION: this AMD information is duplicated from parts of common-ui and cdf.
      // Keep all in sync.

      "pentaho/instanceInfo": {
        "pentaho/visual/config/vizApi.conf": {type: "pentaho.config.spec.IRuleSet"},
        "cdf/components/ccc/config/cdf.vizApi.conf": {type: "pentaho.config.spec.IRuleSet"},
        "pentaho/config/deploy/config": {type: "pentaho.config.spec.IRuleSet"},
        "pentaho/config/impl/instanceOfAmdLoadedService": {type: "pentaho.config.IService"},

        "pentaho/visual/color/palettes/nominalPrimary": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/nominalNeutral": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/nominalLight": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/nominalDark": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/quantitativeBlue3": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/quantitativeBlue5": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/quantitativeGray3": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/quantitativeGray5": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/divergentRyg3": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/divergentRyg5": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/divergentRyb3": {type: "pentaho/visual/color/palette"},
        "pentaho/visual/color/palettes/divergentRyb5": {type: "pentaho/visual/color/palette"}
      },

      "pentaho/typeInfo": {
        "pentaho/type/instance": {alias: "instance"},
        "pentaho/type/value": {alias: "value", base: "instance"},
        "pentaho/type/property": {alias: "property", base: "instance"},
        "pentaho/type/list": {alias: "list", base: "value"},
        "pentaho/type/element": {alias: "element", base: "value"},
        "pentaho/type/complex": {alias: "complex", base: "element"},
        "pentaho/type/application": {alias: "application", base: "complex"},
        "pentaho/type/model": {alias: "model", base: "complex"},
        "pentaho/type/simple": {alias: "simple", base: "element"},
        "pentaho/type/number": {alias: "number", base: "simple"},
        "pentaho/type/string": {alias: "string", base: "simple"},
        "pentaho/type/boolean": {alias: "boolean", base: "simple"},
        "pentaho/type/date": {alias: "date", base: "simple"},
        "pentaho/type/object": {alias: "object", base: "simple"},
        "pentaho/type/function": {alias: "function", base: "simple"},
        "pentaho/type/mixins/enum": {alias: "enum", base: "element"},
        "pentaho/type/action/base": {base: "element"},

        "pentaho/data/filter/abstract": {base: "complex"},
        "pentaho/data/filter/true": {alias: "true", base: "pentaho/data/filter/abstract"},
        "pentaho/data/filter/false": {alias: "false", base: "pentaho/data/filter/abstract"},
        "pentaho/data/filter/tree": {base: "pentaho/data/filter/abstract"},
        "pentaho/data/filter/or": {alias: "or", base: "pentaho/data/filter/tree"},
        "pentaho/data/filter/and": {alias: "and", base: "pentaho/data/filter/tree"},
        "pentaho/data/filter/not": {alias: "not", base: "pentaho/data/filter/abstract"},
        "pentaho/data/filter/property": {base: "pentaho/data/filter/abstract"},
        "pentaho/data/filter/isEqual": {alias: "=", base: "pentaho/data/filter/property"},
        "pentaho/data/filter/isIn": {alias: "in", base: "pentaho/data/filter/property"},
        "pentaho/data/filter/isGreater": {alias: ">", base: "pentaho/data/filter/property"},
        "pentaho/data/filter/isGreaterOrEqual": {alias: ">=", base: "pentaho/data/filter/property"},
        "pentaho/data/filter/isLess": {alias: "<", base: "pentaho/data/filter/property"},
        "pentaho/data/filter/isLessOrEqual": {alias: "<=", base: "pentaho/data/filter/property"},
        "pentaho/data/filter/isLike": {alias: "like", base: "pentaho/data/filter/property"},

        "pentaho/visual/base/model": {base: "model"},
        "pentaho/visual/base/view": { base: "complex"},

        "pentaho/visual/models/abstract": {base: "pentaho/visual/base/model"},
        "pentaho/visual/samples/calc/model": {base: "pentaho/visual/base/model"},
        "pentaho/visual/models/cartesianAbstract": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/categoricalContinuousAbstract": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/barAbstract": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/barNormalizedAbstract": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/barHorizontal": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/bar": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/barStacked": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/barStackedHorizontal": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/barNormalized": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/barNormalizedHorizontal": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/barLine": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/line": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/pointAbstract": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/metricPointAbstract": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/areaStacked": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/pie": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/heatGrid": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/sunburst": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/donut": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/scatter": {base: "pentaho/visual/models/abstract"},
        "pentaho/visual/models/bubble": {base: "pentaho/visual/models/abstract"},

        "pentaho/visual/action/base": {base: "pentaho/type/action/base"},
        "pentaho/visual/action/data": {base: "pentaho/visual/action/base"},
        "pentaho/visual/action/select": {alias: "select", base: "pentaho/visual/action/data"},
        "pentaho/visual/action/execute": {alias: "execute", base: "pentaho/visual/action/data"},

        "pentaho/visual/color/palette": {base: "complex"}
      },

      "pentaho/environment": {
        server: {
          // requirejs does not like to mixin java objects
          // convert to JS string
          root:  ("" + params.get("CONTEXT_PATH"))
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
    load: function (cssId, req, onload) {
        onload(null);
    }
  });

  define("cdf/lib/CCC/def",      ["ccc!"], function(ccc) { return ccc.def; });
  define("cdf/lib/CCC/cdo",      ["ccc!"], function(ccc) { return ccc.cdo; });
  define("cdf/lib/CCC/pvc",      ["ccc!"], function(ccc) { return ccc.pvc; });
  define("cdf/lib/CCC/protovis", ["ccc!"], function(ccc) { return ccc.pv;  });
}());
