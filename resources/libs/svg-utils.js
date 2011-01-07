var cgg = cgg || {};
cgg.utils = cgg.utils || {};

cgg.utils.loadSvg = function(path) {
  return new cgg.document(_loadSvg(path));
};

cgg.utils.initDocument = function(path) {
  _document = _loadSvg(path);
  document = new cgg.document(_document);
}

cgg.utils.printNode = function(node) {
    print(_xmlToString(node._node ? node._node : node));
}
