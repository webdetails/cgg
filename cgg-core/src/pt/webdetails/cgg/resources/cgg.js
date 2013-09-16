
var cgg = cgg || {};

(function() {
cgg.javaToJS = function(v) {
  if(v == null) { return v; }
  if(typeof v === 'object') {
      if(v instanceof java.lang.String ) { return '' + v; }
      if(v instanceof java.lang.Number ) { return +v;     }
      if(v instanceof java.lang.Boolean) { return !!v;    }
      if(v.getClass().isArray()) { // !(v instanceof Array) && 
        var a = [];
        var L = v.length;
        for(var i = 0 ; i < L ; i++) {
          a.push(cgg.javaToJS(v[i]));
        }
        return a;
      }
  }
  return v;
};

cgg.jsToJava = function(v) {
  if(v == null) { return v; }
  if(typeof v === 'object') {
      if(typeof v === 'string')  { return new java.lang.String(v);  }
      if(typeof v === 'number')  { return new java.lang.Double(v);  }
      if(typeof v === 'boolean') { return new java.lang.Boolean(v); }
      if(v instanceof Array) { // !(v instanceof Array) && 
        var L = v.length;
        var a = java.lang.reflect.Array.newInstance(java.lang.String, L);
        for(var i = 0 ; i < L ; i++) {
          a[i] = cgg.jsToJava(v[i]);
        }
        return a;
      }
  }
  return v;
};

var _parameterDefaults = {};

cgg.initParameter = function(pname, dv) {
  _parameterDefaults[pname] = dv;
  return cgg.initParameter;
};

// Component data source parameters namespace
cgg.getParameterValue = function(pname) {
  var pVarName = 'param' + pname;
  return (pVarName in window) ? window[pVarName] : params.get(pname);
};

cgg.setParameterValue = function(pname, v) {
  var pVarName = 'param' + pname;
  if(pVarName in window) { window[pVarName] = v; }

  params.put(pname, v);
};

cgg.init = function(component) {
  var out;
  if(cgg.debug > 2) { out = ["CGG - RENDER " + (component ? component.type : "")]; }

  var lparams = new CggParameters();
  
  params.keySet().toArray()
  .forEach(function(pname) {
    var v  = cgg.javaToJS(params.get(pname));
    var dv = _parameterDefaults[pname];
    
    if(dv != null) {
        if(v == null || v === '') { v = dv; }

        // Also, define a global variable for this parameter (only for DS parameters)
        window['param' + pname] = v;
    }
    
    lparams.put(pname, v);
    
    out && out.push("  " + pname + " = " + cgg.logStringify(v));
  });
    
  // Replace params with lparams
  window.params = lparams;
  
  out && print(out.join("\n"));
};

cgg.render = function(component) {
  cgg.init(component);

  Dashboards.bindControl(component);

  component.update();
};

// -----------

var CggParameters = Base.extend({
  constructor: function() { this._params = {}; },
  names: function()        { return Object.keys(this._params); },
  get:  function(pname)    { return this._params[pname]; },
  put:  function(pname, v) { this._params[pname] = v; return this; },
  each: function(f, x) {
    this
    .names()
    .forEach(function(pname) { f.call(x, this[pname], pname); }, this._params);
  }
});


}());

// <= ~2013-09-12 Legacy scripts; did not execute pre/postExec and received data directly.
function renderCccFromComponent(component, data) {
  cgg.init(component);

  Dashboards.bindControl(component, CggLegacy1CccComponent);

  component.setPreFetchedData(data);

  component.update();
}
