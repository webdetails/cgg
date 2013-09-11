
var Dashboards = Dashboards || {};

(function() {

Dashboards.bindControl = function(control, Class) {
  if(!Class) { Class = this._getControlClass(control); }
  if(!Class) {
    this.log("Object type " + control["type"] + " can't be mapped to a valid class", "error");
  } else {
    this._castControlToClass(control, Class);
  }

  return this.bindExistingControl(control, Class);
};

Dashboards._castControlToClass = function(control, Class) {
  if(!(control instanceof Class)) {
    var controlImpl = this._makeInstance(Class);

    // Copy implementation into control
    $.extend(control, controlImpl);
  }
};

Dashboards.bindExistingControl = function(control, Class) {
  if(!control.dashboard) {
    control.dashboard = this;

    // Ensure BaseComponent's methods
    this._castControlToComponent(control, Class);
  }

  return control;
};

var _controlClassHandlers = [];

Dashboards._registerControlClassHandler = function(handler) {
  _controlClassHandlers.push(handler);
};

Dashboards._registerControlClassHandler(function(typeName) {
  var TypeName = typeName.substring(0,1).toUpperCase() + typeName.substring(1);

  // try _TypeComponent_, _type_ and _Type_ as class names
  var typeNames = [TypeName + 'Component', typeName, TypeName];

  for(var i = 0, N = typeNames.length ; i < N ; i++) {
    
    // If the value of a name is not a function, keep on trying.
    var Class = window[typeNames[i]];
    if(Class && typeof Class === 'function') { return Class; }
  }
});

Dashboards._getControlClass = function(control) {
  // see if there is a class defined for this control
  var typeName = control.type;
  if(typeof typeName === 'function') { typeName = typeName.call(control); } // <=> control.type() ; the _this_ in the call is _control_

  if(typeName) {
    for(var i = 0, N = _controlClassHandlers.length ; i < N ; i++) {
      var Class = _controlClassHandlers[i](typeName);
      if(Class) { return Class; }
    }
  }
  // return undefined;
};

Dashboards._makeInstance = function(Class, args) {
  var o = Object.create(Class.prototype);
  if(args) { Class.apply(o, args); } else { Class.apply(o); }
  return o;
};

Dashboards._castControlToComponent = function(control, Class) {
  // Extend control with BaseComponent methods, if it's not an instance of it.
  // Also, avoid extending if _Class_ was already applied
  // and it is a subclass of BaseComponent.
  if(!(control instanceof BaseComponent) &&
     (!Class || !(Class.prototype instanceof BaseComponent))) {

    var baseProto = BaseComponent.prototype;
    for(var p in baseProto) {
      if(baseProto.hasOwnProperty(p) &&
         (control[p] === undefined) &&
         (typeof baseProto[p] === 'function')) {
        switch(p) {
          // Exceptions
          case 'base': break;

          // Copy
          default: control[p] = baseProto[p]; break;
        }
      }
    }
  }
};

Dashboards.propertiesArrayToObject = function(pArray) {
  var obj = {};
  for(var p in pArray) {
    if(pArray.hasOwnProperty(p)) {
      var prop = pArray[p];
      obj[prop[0]] = prop[1];
    }
  }
  return obj;
};

Dashboards.objectToPropertiesArray = function(obj) {
  var pArray = [];
  for(var key in obj) {
    if(obj.hasOwnProperty(key)) {
      pArray.push([key,obj[key]]);
    }
  }
  return pArray;
};

Dashboards.log = function(m, type) {
    print((type || 'LOG').toUpperCase() + ": " + m);
};

}());
