    lib("env.js") &&
    lib("protovis.js") && 
    lib("Base.js") &&
    lib("jquery.js") &&
    lib("tipsy.js") &&
    lib("pvc-d1.0.js");

(function() {
  nativeCreateElementNS = document.createElementNS;

  document.createElementNS = function() {
        var node = nativeCreateElementNS.apply(document,arguments);
        if (typeof node.style == 'undefined') {
            node.style = new CSS2Properties(node);
        }
        return node;
  }
})();

