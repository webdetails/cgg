/*!
 * Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

  var basePathCommonUI = "/plugin/common-ui/resources/web/compressed/";
  var basePathCdf = "/plugin/pentaho-cdf/js/";
  var basePathKarafConfig = "/system/karaf/config/web-client/";

  require.config({

    baseUrl: "",

    packages: [
      "cgg",
      "cdf",
      "ccc",
      {"name": "pentaho/module", "main": "metaOf"},
      {"name": "pentaho/environment"},
      {"name": "pentaho/i18n", "main": "serverService"},
      {"name": "pentaho/debug", "main": "manager"}
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

      "pentaho/modules": {
        "cdf/components/ccc/config/cdf.vizApi.conf": {type: "pentaho/config/spec/IRuleSet"},
        "pentaho/config/deploy/config": {type: "pentaho/config/spec/IRuleSet"},

        "pentaho/config/spec/IRuleSet": {
          "base": null,
          "isAbstract": true
        },
        "pentaho/type/Instance": {
          "alias": "instance",
          "base": null
        },
        "pentaho/type/Value": {
          "alias": "value",
          "base": "instance"
        },
        "pentaho/type/Property": {
          "alias": "property",
          "base": "instance"
        },
        "pentaho/type/List": {
          "alias": "list",
          "base": "value"
        },
        "pentaho/type/Element": {
          "alias": "element",
          "base": "value"
        },
        "pentaho/type/Complex": {
          "alias": "complex",
          "base": "element"
        },
        "pentaho/type/Simple": {
          "alias": "simple",
          "base": "element"
        },
        "pentaho/type/Number": {
          "alias": "number",
          "base": "simple"
        },
        "pentaho/type/String": {
          "alias": "string",
          "base": "simple"
        },
        "pentaho/type/Boolean": {
          "alias": "boolean",
          "base": "simple"
        },
        "pentaho/type/Date": {
          "alias": "date",
          "base": "simple"
        },
        "pentaho/type/Object": {
          "alias": "object",
          "base": "simple"
        },
        "pentaho/type/Function": {
          "alias": "function",
          "base": "simple"
        },
        "pentaho/type/TypeDescriptor": {
          "alias": "type",
          "base": "simple"
        },
        "pentaho/type/mixins/Enum": {
          "alias": "enum",
          "base": "element"
        },
        "pentaho/type/action/Base": {
          "base": "element"
        },
        "pentaho/data/filter/Abstract": {
          "base": "complex"
        },
        "pentaho/data/filter/True": {
          "alias": "true",
          "base": "pentaho/data/filter/Abstract"
        },
        "pentaho/data/filter/False": {
          "alias": "false",
          "base": "pentaho/data/filter/Abstract"
        },
        "pentaho/data/filter/Tree": {
          "base": "pentaho/data/filter/Abstract"
        },
        "pentaho/data/filter/Or": {
          "alias": "or",
          "base": "pentaho/data/filter/Tree"
        },
        "pentaho/data/filter/And": {
          "alias": "and",
          "base": "pentaho/data/filter/Tree"
        },
        "pentaho/data/filter/Not": {
          "alias": "not",
          "base": "pentaho/data/filter/Abstract"
        },
        "pentaho/data/filter/Property": {
          "base": "pentaho/data/filter/Abstract"
        },
        "pentaho/data/filter/IsEqual": {
          "alias": "=",
          "base": "pentaho/data/filter/Property"
        },
        "pentaho/data/filter/IsIn": {
          "alias": "in",
          "base": "pentaho/data/filter/Property"
        },
        "pentaho/data/filter/IsGreater": {
          "alias": ">",
          "base": "pentaho/data/filter/Property"
        },
        "pentaho/data/filter/IsGreaterOrEqual": {
          "alias": ">=",
          "base": "pentaho/data/filter/Property"
        },
        "pentaho/data/filter/IsLess": {
          "alias": "<",
          "base": "pentaho/data/filter/Property"
        },
        "pentaho/data/filter/IsLessOrEqual": {
          "alias": "<=",
          "base": "pentaho/data/filter/Property"
        },
        "pentaho/data/filter/IsLike": {
          "alias": "like",
          "base": "pentaho/data/filter/Property"
        },
        "pentaho/visual/base/Model": {
          "base": "complex"
        },
        "pentaho/visual/base/View": {
          "base": "complex"
        },
        "pentaho/visual/role/adaptation/Strategy": {
          "base": "complex"
        },
        "pentaho/visual/role/adaptation/EntityWithTimeIntervalKeyStrategy": {
          "base": "pentaho/visual/role/adaptation/Strategy",
          "ranking": -5
        },
        "pentaho/visual/role/adaptation/IdentityStrategy": {
          "base": "pentaho/visual/role/adaptation/Strategy",
          "ranking": -10
        },
        "pentaho/visual/role/adaptation/TupleStrategy": {
          "base": "pentaho/visual/role/adaptation/Strategy",
          "ranking": -20
        },
        "pentaho/visual/models/Abstract": {
          "base": "pentaho/visual/base/Model"
        },
        "pentaho/visual/samples/calc/Model": {
          "base": "pentaho/visual/base/Model"
        },
        "pentaho/visual/models/CartesianAbstract": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/CategoricalContinuousAbstract": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/BarAbstract": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/BarNormalizedAbstract": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/BarHorizontal": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/Bar": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/BarStacked": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/BarStackedHorizontal": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/BarNormalized": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/BarNormalizedHorizontal": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/BarLine": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/Line": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/PointAbstract": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/MetricPointAbstract": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/AreaStacked": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/Pie": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/HeatGrid": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/Sunburst": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/Donut": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/Scatter": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/models/Bubble": {
          "base": "pentaho/visual/models/Abstract"
        },
        "pentaho/visual/action/Base": {
          "base": "pentaho/type/action/Base"
        },
        "pentaho/visual/action/Select": {
          "alias": "select",
          "base": "pentaho/visual/action/Base"
        },
        "pentaho/visual/action/Execute": {
          "alias": "execute",
          "base": "pentaho/visual/action/Base"
        },
        "pentaho/visual/action/Update": {
          "base": "pentaho/visual/action/Base"
        },
        "pentaho/visual/color/Palette": {
          "base": "complex"
        },
        "pentaho/visual/config/vizApi.conf": {
          "type": "pentaho/config/spec/IRuleSet"
        },
        "pentaho/visual/color/palettes/nominalPrimary": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        },
        "pentaho/visual/color/palettes/nominalNeutral": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -110
        },
        "pentaho/visual/color/palettes/nominalLight": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -120
        },
        "pentaho/visual/color/palettes/nominalDark": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -130
        },
        "pentaho/visual/color/palettes/quantitativeBlue3": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        },
        "pentaho/visual/color/palettes/quantitativeBlue5": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        },
        "pentaho/visual/color/palettes/quantitativeGray3": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        },
        "pentaho/visual/color/palettes/quantitativeGray5": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        },
        "pentaho/visual/color/palettes/divergentRyg3": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        },
        "pentaho/visual/color/palettes/divergentRyg5": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        },
        "pentaho/visual/color/palettes/divergentRyb3": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        },
        "pentaho/visual/color/palettes/divergentRyb5": {
          "type": "pentaho/visual/color/Palette",
          "ranking": -10
        }
      },

      "pentaho/environment": {
        server: {
          // requirejs does not like to mixin java objects
          // convert to JS string
          root: ("" + params.get("CONTEXT_PATH"))
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

    bundles: {
      "pentaho/platformBundle": [
        "pentaho/util/has",
        "pentaho/util/object",
        "pentaho/util/fun",
        "pentaho/util/text",
        "pentaho/util/requireJSConfig",
        "pentaho/debug/Levels",
        "pentaho/debug/impl/Manager",
        "pentaho/util/domWindow",
        "pentaho/debug/manager",
        "pentaho/debug",
        "pentaho/lang/Base",
        "pentaho/lang/List",
        "pentaho/lang/SortedList",
        "pentaho/lang/ArgumentError",
        "pentaho/lang/ArgumentRequiredError",
        "pentaho/lang/ArgumentInvalidError",
        "pentaho/_core/module/MetaService",
        "pentaho/util/logger",
        "pentaho/lang/ArgumentInvalidTypeError",
        "pentaho/lang/ArgumentRangeError",
        "pentaho/lang/OperationInvalidError",
        "pentaho/lang/NotImplementedError",
        "pentaho/util/error",
        "pentaho/util/arg",
        "pentaho/shim/_es6-promise/es6-promise",
        "pentaho/shim/es6-promise",
        "pentaho/util/promise",
        "pentaho/_core/module/Meta",
        "pentaho/_core/module/InstanceMeta",
        "pentaho/_core/module/TypeMeta",
        "pentaho/_core/module/Service",
        "pentaho/util/spec",
        "pentaho/_core/config/Service",
        "pentaho/_core/Core",
        "pentaho/util/url",
        "pentaho/environment/impl/Environment",
        "pentaho/environment/main",
        "pentaho/environment",
        "pentaho/_core/main",
        "pentaho/config/service",
        "pentaho/module/service",
        "pentaho/module/metaService",
        "pentaho/module/util",
        "pentaho/module/metaOf",
        "pentaho/module",
        "pentaho/module/impl/ServicePlugin",
        "pentaho/module/subtypeOf",
        "pentaho/module/subtypesOf",
        "pentaho/module/instanceOf",
        "pentaho/module/instancesOf",
        "pentaho/type/SpecificationContext",
        "pentaho/type/SpecificationScope",
        "pentaho/type/impl/SpecificationProcessor",
        "pentaho/type/impl/Loader",
        "pentaho/type/_baseLoader",
        "pentaho/type/InstanceType",
        "pentaho/type/Instance",
        "pentaho/type/changes/_transactionControl",
        "pentaho/type/ReferenceList",
        "pentaho/type/changes/ChangeRef",
        "pentaho/type/changes/AbstractTransactionScope",
        "pentaho/type/changes/TransactionScope",
        "pentaho/type/changes/CommittedScope",
        "pentaho/lang/UserError",
        "pentaho/type/changes/TransactionRejectedError",
        "pentaho/lang/ActionResult",
        "pentaho/type/changes/Transaction",
        "pentaho/type/util",
        "pentaho/type/ValidationError",
        "pentaho/type/Value",
        "pentaho/type/Element",
        "pentaho/lang/Event",
        "pentaho/lang/EventSource",
        "pentaho/type/mixins/changeset",
        "pentaho/type/events/WillChange",
        "pentaho/type/mixins/error",
        "pentaho/type/events/RejectedChange",
        "pentaho/type/events/DidChange",
        "pentaho/type/mixins/Container",
        "pentaho/type/changes/Change",
        "pentaho/type/changes/Changeset",
        "pentaho/type/changes/PrimitiveChange",
        "pentaho/type/changes/Add",
        "pentaho/type/changes/Remove",
        "pentaho/type/changes/Move",
        "pentaho/type/changes/Sort",
        "pentaho/type/changes/Clear",
        "pentaho/type/changes/ListChangeset",
        "pentaho/type/List",
        "pentaho/type/mixins/DiscreteDomain",
        "pentaho/type/Property",
        "pentaho/lang/Collection",
        "pentaho/type/PropertyTypeCollection",
        "pentaho/type/changes/Replace",
        "pentaho/type/changes/ComplexChangeset",
        "pentaho/type/Simple",
        "pentaho/type/String",
        "pentaho/type/Number",
        "pentaho/type/Boolean",
        "pentaho/util/date",
        "pentaho/type/Date",
        "pentaho/type/Object",
        "pentaho/type/Function",
        "pentaho/type/TypeDescriptor",
        "pentaho/type/standardSimple",
        "pentaho/type/Complex",
        "pentaho/type/mixins/Enum",
        "pentaho/type/standard",
        "pentaho/type/loader",
        "pentaho/data/_ElementMock",
        "pentaho/data/AtomicTypeName",
        "pentaho/data/_AbstractTable",
        "pentaho/data/_OfAttribute",
        "pentaho/lang/_Annotatable",
        "pentaho/data/Member",
        "pentaho/data/Cell",
        "pentaho/data/StructurePosition",
        "pentaho/data/MemberCollection",
        "pentaho/data/Attribute",
        "pentaho/data/AttributeCollection",
        "pentaho/data/Model",
        "pentaho/data/Structure",
        "pentaho/data/_plain/Row",
        "pentaho/data/_WithStructure",
        "pentaho/data/CellTuple",
        "pentaho/data/_WithCellTupleBase",
        "pentaho/data/_plain/RowList",
        "pentaho/data/_plain/Table",
        "pentaho/data/_cross/AxisPosition",
        "pentaho/data/_cross/Axis",
        "pentaho/data/_cross/MeasureCellSet",
        "pentaho/data/_cross/Table",
        "pentaho/data/_Table",
        "pentaho/data/_TableView",
        "pentaho/data/AbstractTable",
        "pentaho/data/Table",
        "pentaho/data/TableView",
        "pentaho/data/filter/_core/Tree",
        "pentaho/data/filter/KnownFilterKind",
        "pentaho/data/filter/_core/And",
        "pentaho/data/filter/_core/Or",
        "pentaho/data/filter/_core/Not",
        "pentaho/data/filter/_core/True",
        "pentaho/data/filter/_core/False",
        "pentaho/data/filter/_core/Property",
        "pentaho/data/filter/_core/IsEqual",
        "pentaho/data/filter/_core/IsIn",
        "pentaho/data/filter/_core/IsGreater",
        "pentaho/data/filter/_core/IsLess",
        "pentaho/data/filter/_core/IsGreaterOrEqual",
        "pentaho/data/filter/_core/IsLessOrEqual",
        "pentaho/data/filter/_core/IsLike",
        "pentaho/data/filter/Abstract",
        "pentaho/data/filter/Tree",
        "pentaho/data/filter/Property",
        "pentaho/data/filter/And",
        "pentaho/data/filter/Or",
        "pentaho/data/filter/Not",
        "pentaho/data/filter/IsEqual",
        "pentaho/data/filter/IsIn",
        "pentaho/data/filter/IsGreater",
        "pentaho/data/filter/IsLess",
        "pentaho/data/filter/IsGreaterOrEqual",
        "pentaho/data/filter/IsLessOrEqual",
        "pentaho/data/filter/IsLike",
        "pentaho/data/filter/True",
        "pentaho/data/filter/False",
        "pentaho/data/filter/standard",
        "pentaho/visual/role/MappingField",
        "pentaho/data/util",
        "pentaho/visual/role/AbstractMapping",
        "pentaho/visual/role/AbstractProperty",
        "pentaho/visual/color/Level",
        "pentaho/visual/color/Palette",
        "pentaho/visual/color/PaletteProperty",
        "pentaho/visual/base/Application",
        "pentaho/visual/base/AbstractModel",
        "pentaho/visual/role/Mode",
        "pentaho/visual/role/Mapping",
        "pentaho/visual/role/Property",
        "pentaho/visual/base/Model",
        "pentaho/type/action/Base",
        "pentaho/visual/action/Base",
        "pentaho/visual/action/Update",
        "pentaho/visual/action/mixins/Data",
        "pentaho/visual/action/mixins/Positioned",
        "pentaho/visual/action/SelectionModes",
        "pentaho/visual/action/Select",
        "pentaho/visual/action/Execute",
        "pentaho/type/action/States",
        "pentaho/lang/RuntimeError",
        "pentaho/type/action/Execution",
        "pentaho/type/action/impl/Target",
        "pentaho/util/BitSet",
        "pentaho/visual/base/View",
        "pentaho/visual/role/ExternalMapping",
        "pentaho/visual/role/ExternalProperty",
        "pentaho/visual/base/ModelAdapter",
        "pentaho/visual/color/utils",
        "pentaho/visual/color/palettes/divergentRyb3",
        "pentaho/visual/color/palettes/divergentRyb5",
        "pentaho/visual/color/palettes/divergentRyg3",
        "pentaho/visual/color/palettes/divergentRyg5",
        "pentaho/visual/color/palettes/nominalDark",
        "pentaho/visual/color/palettes/nominalLight",
        "pentaho/visual/color/palettes/nominalNeutral",
        "pentaho/visual/color/palettes/nominalPrimary",
        "pentaho/visual/color/palettes/quantitativeBlue3",
        "pentaho/visual/color/palettes/quantitativeBlue5",
        "pentaho/visual/color/palettes/quantitativeGray3",
        "pentaho/visual/color/palettes/quantitativeGray5",
        "pentaho/visual/color/palettes/all",
        "pentaho/visual/models/types/Color",
        "pentaho/visual/models/types/BackgroundFill",
        "pentaho/visual/models/types/FontStyle",
        "pentaho/visual/models/types/Sides",
        "pentaho/visual/models/types/LabelsOption",
        "pentaho/visual/models/Abstract",
        "pentaho/visual/models/types/DisplayUnits",
        "pentaho/visual/models/CartesianAbstract",
        "pentaho/visual/models/mixins/ScaleColorDiscrete",
        "pentaho/visual/models/CategoricalContinuousAbstract",
        "pentaho/visual/models/types/MaxChartsPerRow",
        "pentaho/visual/models/types/MultiChartRangeScope",
        "pentaho/visual/models/types/MultiChartOverflow",
        "pentaho/visual/models/mixins/MultiCharted",
        "pentaho/visual/models/types/EmptyCellMode",
        "pentaho/visual/models/mixins/Interpolated",
        "pentaho/visual/models/PointAbstract",
        "pentaho/visual/models/AreaStacked",
        "pentaho/visual/models/BarAbstract",
        "pentaho/visual/models/types/TrendType",
        "pentaho/visual/models/types/LineWidth",
        "pentaho/visual/models/mixins/Trended",
        "pentaho/visual/models/Bar",
        "pentaho/visual/models/BarHorizontal",
        "pentaho/visual/models/types/Shape",
        "pentaho/visual/models/BarLine",
        "pentaho/visual/models/BarNormalizedAbstract",
        "pentaho/visual/models/BarNormalized",
        "pentaho/visual/models/BarNormalizedHorizontal",
        "pentaho/visual/models/BarStacked",
        "pentaho/visual/models/BarStackedHorizontal",
        "pentaho/visual/models/types/ColorSet",
        "pentaho/visual/models/types/Pattern",
        "pentaho/visual/models/mixins/ScaleColorContinuous",
        "pentaho/visual/models/MetricPointAbstract",
        "pentaho/visual/models/types/SizeByNegativesMode",
        "pentaho/visual/models/mixins/ScaleSizeContinuous",
        "pentaho/visual/models/Bubble",
        "pentaho/visual/models/Pie",
        "pentaho/visual/models/Donut",
        "pentaho/visual/models/HeatGrid",
        "pentaho/visual/models/Line",
        "pentaho/visual/models/Scatter",
        "pentaho/visual/models/types/SliceOrder",
        "pentaho/visual/models/Sunburst",
        "pentaho/visual/models/all",
        "pentaho/visual/role/adaptation/Strategy",
        "pentaho/visual/role/adaptation/IdentityStrategy",
        "pentaho/visual/role/adaptation/TupleStrategy",
        "pentaho/visual/role/adaptation/TimeIntervalDuration",
        "pentaho/visual/role/adaptation/EntityWithTimeIntervalKeyStrategy",
        "pentaho/visual/role/adaptation/allStrategies",
        "pentaho/visual/scene/util",
        "pentaho/visual/scene/impl/Variable",
        "pentaho/visual/scene/Base",
        "pentaho/ccc/visual/_util",
        "pentaho/ccc/visual/Abstract",
        "pentaho/ccc/visual/CartesianAbstract",
        "pentaho/ccc/visual/CategoricalContinuousAbstract",
        "pentaho/ccc/visual/PointAbstract",
        "pentaho/ccc/visual/AreaAbstract",
        "pentaho/ccc/visual/Area",
        "pentaho/ccc/visual/AreaStacked",
        "pentaho/ccc/visual/BarAbstract",
        "pentaho/data/_trends",
        "pentaho/data/_trend-linear",
        "pentaho/data/trends",
        "pentaho/ccc/visual/_trends",
        "pentaho/ccc/visual/Bar",
        "pentaho/ccc/visual/BarHorizontal",
        "pentaho/ccc/visual/BarLine",
        "pentaho/ccc/visual/BarNormalizedAbstract",
        "pentaho/ccc/visual/BarNormalized",
        "pentaho/ccc/visual/BarNormalizedHorizontal",
        "pentaho/ccc/visual/BarStacked",
        "pentaho/ccc/visual/BarStackedHorizontal",
        "pentaho/ccc/visual/Boxplot",
        "pentaho/ccc/visual/MetricPointAbstract",
        "pentaho/ccc/visual/Bubble",
        "pentaho/ccc/visual/Pie",
        "pentaho/ccc/visual/Donut",
        "pentaho/ccc/visual/HeatGrid",
        "pentaho/ccc/visual/Line",
        "pentaho/ccc/visual/Scatter",
        "pentaho/ccc/visual/Sunburst",
        "pentaho/ccc/visual/Treemap",
        "pentaho/ccc/visual/Waterfall",
        "pentaho/ccc/visual/all"
      ]
    }
  });

  // A dummy css plugin.
  // Supports dummy loading of tipsy.css, by jquery.tipsy.js.
  define("css", [], {
    load: function(cssId, req, onload) {
      onload(null);
    }
  });

  define("cdf/lib/CCC/def", ["ccc!"], function(ccc) { return ccc.def; });
  define("cdf/lib/CCC/cdo", ["ccc!"], function(ccc) { return ccc.cdo; });
  define("cdf/lib/CCC/pvc", ["ccc!"], function(ccc) { return ccc.pvc; });
  define("cdf/lib/CCC/protovis", ["ccc!"], function(ccc) { return ccc.pv; });
}());
