// Language
lib("lib/shims.js") &&
lib("Base.js") &&
lib("json.js") &&

// CGG environment
lib("cgg.js") &&
lib("document.js")  &&
lib("svg-utils.js") &&

// CCC library
lib_CCC_2_0() &&

// CDF layer
lib("Dashboards.js") &&
lib("components/core.js") &&
lib("components/ccc.js");

function lib_CCC_2_0() {
    var basePath = "lib/CCC/2.0/";

    return lib(basePath + "jquery-shim.js") && 
        lib(basePath + "protovis.js") && 
        lib(basePath + "protovis-cgg.js") && 
        lib(basePath + "tipsy-shim.js") && 
        lib(basePath + "def.js") &&
        lib(basePath + "pvc-d2.0.js") &&
        lib(basePath + "compatVersion.js");
}
