    lib("compat.js") &&
    lib("document.js") &&
    lib("svg-utils.js") &&
    lib("protovis.js") &&
    lib("protovis-cgg.js") &&
    lib("json.js") &&
    lib("ccc-util.js") &&
    lib("def.js") &&
    lib("pvc-d2.0.js") &&
    lib("compatVersion.js");

// --------------

// Not all versions of CCC have pvc.stringify.
if(pvc.stringify) {
  cgg.logStringify = function(s) {
    return pvc.stringify(s, {ownOnly: false});
  };
}

// --------------
// idem
if(pvc.setDebug) {
  pvc.setDebug(cgg.debug);
}