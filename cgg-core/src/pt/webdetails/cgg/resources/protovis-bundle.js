// Language
lib("lib/shims.js") &&
lib("Base.js") &&
lib("json.js") &&

// CGG environment
lib("cgg.js") &&
lib("document.js")  &&
lib("svg-utils.js") &&

// CCC library
lib("lib/CCC/jquery-shim.js") && 
lib("lib/CCC/protovis.js") && 
lib("lib/CCC/protovis-cgg.js") && 
lib("lib/CCC/tipsy-shim.js") && 
lib("lib/CCC/def.js") &&
lib("lib/CCC/pvc-d2.0.js") &&
lib("lib/CCC/compatVersion.js") && 

// CDF layer
lib("Dashboards.js") &&
lib("components/core.js") &&
lib("components/ccc.js");
