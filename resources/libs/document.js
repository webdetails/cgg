window = this;


var cgg = cgg || {};
cgg.document = function(_doc){
  var doc = {
    get _node() {return _doc;},
    get implementation() {return 'batik';},
    get lastChild() {return new cgg.element(_doc.getLastChild());},

    appendChild: function(e) {
      /* We need to appendChild() the raw Java element, but
       * also need to return the wrapped element.
       * If the element wasn't wrapped to begin with, we must wrap it.
       */
      if ((_e = typeof e._elem) !== "undefined") {
        _doc.appendChild(_e);
        return e;
      } else {
        _doc.appendChild(e);
        return new cgg.element(e);
      }
    },
    getElementById: function(id) {return new cgg.element(_doc.getElementById(id));},
    createElement: function(tagName) {return cgg.element(_doc.createElement(tagName));},
    createTextNode: function(text) {return cgg.element(_doc.createTextNode(text));},
    createElementNS: function(nameSpace, qualifiedName) {return cgg.element(_doc.createElementNS(nameSpace, qualifiedName));}
  };
  return doc;
};

cgg.element = function(_el) {
  var el = {
    get _node(){return _el;},
    get lastChild(){return _el.getLastChild();},
    get style(){
        return new cgg.style(_el.getStyle());
    },

    addEventListener: function() {},
    appendChild: function(e) {
      /* We need to appendChild() the raw Java element, but
       * also need to return the wrapped element.
       * If the element wasn't wrapped to begin with, we must wrap it.
       */
      if (typeof (_e = e._node) !== "undefined") {
        _el.appendChild(_e);
        return e;
      } else {
        _el.appendChild(e);
        return new cgg.element(e);
      }
    },
    setAttribute: function(attrName, value) {_el.setAttribute(attrName, value);},
    setAttributeNS: function(ns, attrName, value) {_el.setAttributeNS(ns,attrName, value);},
    removeAttribute: function(attrName) {_el.removeAttribute(attrName);}

  };
  return el;
};

cgg.style = function(_style) {
  var style = {
    setProperty: function(name, value,prio) {_style.setProperty(name, value, (typeof prio === "undefined" ? null :prio));},
    removeProperty: function(name) {_style.removeProperty(name);}
  };
  return style;
}
document = new cgg.document(_document);
