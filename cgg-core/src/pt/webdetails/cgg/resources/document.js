window = this;

var cgg = cgg || {};

cgg.document = function(_doc) {
  var doc = {
    get _node() { return _doc; },

    get implementation()    { return _doc.getImplementation(); },
    get svgImplementation() { return 'batik'; }, // this feeds pv.renderer
    get documentElement()   { return new cgg.element(_doc.getDocumentElement()); },
    get lastChild()         { return new cgg.element(_doc.getLastChild());  },
    get firstChild()        { return new cgg.element(_doc.getFirstChild()); },

    appendChild: function(e) {
      /* We need to appendChild() the raw Java element, 
       * but also need to return the wrapped element.
       * If the element wasn't wrapped to begin with, we must wrap it.
       */
      var _e = e._node;
      if(_e) {
        _doc.appendChild(_e);
        return e;
      }

      _doc.appendChild(e);
      return new cgg.element(e);
    },
    getElementById:  function(id)      { return new cgg.element(_doc.getElementById(id));     },
    createElement:   function(tagName) { return new cgg.element(_doc.createElement(tagName)); },
    createTextNode:  function(text)    { return new cgg.element(_doc.createTextNode(text));   },
    createElementNS: function(nameSpace, qualifiedName) { return cgg.element(_doc.createElementNS(nameSpace, qualifiedName)); },
    removeChild:     function(node) { 
      _doc.removeChild(node._node || node);
      return node.node ? node : new cgg.element(node);
    }
  };
  return doc;
};

cgg.element = function(_el) {
  var el = {
    get _node() { return _el; },

    get lastChild() { return _el.getLastChild(); },
    get style()     { return new cgg.style(_el.getStyle()); },

    get parentNode() {
      if(_el.getParentNode() == null) { return null };
      return new cgg.element(_el.getParentNode());
    },

    addEventListener: function() {},

    appendChild: function(e) {
      /* We need to appendChild() the raw Java element, 
       * but also need to return the wrapped element.
       * If the element wasn't wrapped to begin with, we must wrap it.
       */
      var _e = e._node;
      if(_e) {
        _el.appendChild(_e);
        return e;
      }

      _el.appendChild(e);
      return new cgg.element(e);
    },
    removeChild: function(node) {
      _el.removeChild(node._node || node);
      return node.node ? node : new cgg.element(node);
    },

    getBBox:              function() { return _el.getBBox(); },
    getElementsByTagName: function(tagName)             { return _el.getElementsByTagName(tagName); },
    setAttribute:         function(attrName, value)     { _el.setAttribute(attrName, value); },
    setAttributeNS:       function(ns, attrName, value) { _el.setAttributeNS(ns,attrName, value); },
    removeAttribute:      function(attrName)            { _el.removeAttribute(attrName); }
  };
  return el;
};

cgg.style = function(_style) {
  var style = {
    setProperty: function(name, value, prio) {
      try {
        _style.setProperty(name, value, (typeof prio === "undefined" ? null : prio));
      } catch(e) {
        if(!(e.javaException.getClass().getName() == "org.w3c.dom.DOMException")) { throw e; }
      }
    },
    removeProperty: function(name) { _style.removeProperty(name); },
    getProperty:    function(name) { return _style.getPropertyValue(name); }
  };
  return style;
};

document = new cgg.document(_document);

window.console = {
  log: function() {
    print("LOG: " + Array.prototype.join.call(arguments, ','));
  }
};
