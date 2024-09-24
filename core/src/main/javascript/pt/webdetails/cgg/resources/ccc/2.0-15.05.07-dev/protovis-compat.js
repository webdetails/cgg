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
define([ "require" ], function(contextualRequire) {
    var protovisId = "./protovis", protovisMsieId = "./protovis-msie", compat = {
        load: function(name, require, onLoad, config) {
            if (config.isBuild) require([ protovisId ], onLoad); else {
                var have_SVG = !(!document.createElementNS || !document.createElementNS("http://www.w3.org/2000/svg", "svg").createSVGRect), have_VML = function(d, a, b) {
                    a = d.createElement("div");
                    a.innerHTML = '<pvml:shape adj="1" />';
                    b = a.firstChild;
                    if (b) {
                        b.style.behavior = "url(#default#VML)";
                        return "object" == typeof b.adj;
                    }
                    return !1;
                }(document);
                !have_SVG && have_VML ? contextualRequire([ protovisMsieId ], onLoad) : contextualRequire([ protovisId ], onLoad);
            }
        }
    };
    return compat;
});