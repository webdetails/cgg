// Portions Copyright (c) 2012 jQuery Foundation and other contributors, http://jquery.com/

// Shamelessly copied from jquery!

define(function() {

  var jQuery = {};

  var hasOwn = Object.prototype.hasOwnProperty;

  jQuery.isPlainObject = function(ele) {
      // Must be an Object.
      // Because of IE, we also have to check the presence of the constructor property.
      // Make sure that DOM nodes and window objects don't pass through, as well
      if(!obj || jQuery.type(obj) !== "object" || obj.nodeType || jQuery.isWindow( obj )) {
          return false;
      }

      // Not own constructor property must be Object
      if(obj.constructor &&
         !hasOwn.call(obj, "constructor") &&
         !hasOwn.call(obj.constructor.prototype, "isPrototypeOf") ) {
          return false;
      }

      // Own properties are enumerated firstly, so to speed up,
      // if last one is own, then all properties are own.

      var key;
      for(key in obj) {}

      return key === undefined || hasOwn.call(obj, key);
  };

  jQuery.isArray = Array.isArray || function(obj) {
      return jQuery.type(obj) === "array";
  };

  jQuery.isWindow = function(obj) {
      return obj && typeof obj === "object" && "setInterval" in obj;
  };

  jQuery.type = function(obj) {
      return obj == null ? String(obj) : "object";
  };

  jQuery.isPlainObject = function( obj ) {
    // Must be an Object.
    // Because of IE, we also have to check the presence of the constructor property.
    // Make sure that DOM nodes and window objects don't pass through, as well.
    if(!obj || jQuery.type(obj) !== "object" || obj.nodeType || jQuery.isWindow(obj)) {
       return false;
    }

    // Not own constructor property must be Object
    if(obj.constructor &&
       !hasOwn.call(obj, "constructor") &&
       !hasOwn.call(obj.constructor.prototype, "isPrototypeOf")) {
       return false;
    }

    // Own properties are enumerated firstly, so to speed up,
    // if last one is own, then all properties are own.

    var key;
    for(key in obj) {}
    return key === undefined || hasOwn.call(obj, key);
  };

  jQuery.support = {};

  jQuery.extend = function() {
    var options, name, src, copy, copyIsArray, clone,
        target = arguments[0] || {},
        i = 1,
        length = arguments.length,
        deep = false;

      // Handle a deep copy situation
      if(typeof target === "boolean") {
          deep = target;
          target = arguments[1] || {};
          // skip the boolean and the target
          i = 2;
      }

      // Handle case when target is a string or something (possible in deep copy)
      if(typeof target !== "object" && !jQuery.isFunction(target)) {
          target = {};
      }

      // extend jQuery itself if only one argument is passed
      if(length === i) {
          target = this;
          --i;
      }

      for( ; i < length; i++) {
          // Only deal with non-null/undefined values
          if((options = arguments[i]) != null) {
              // Extend the base object
              for(name in options) {
                  src  = target[name];
                  copy = options[name];

                  // Prevent never-ending loop
                  if(target === copy) { continue; }

                  // Recurse if we're merging plain objects or arrays
                  if(deep && copy && (jQuery.isPlainObject(copy) || (copyIsArray = jQuery.isArray(copy)))) {
                      if(copyIsArray) {
                          copyIsArray = false;
                          clone = src && jQuery.isArray(src) ? src : [];
                      } else {
                          clone = src && jQuery.isPlainObject(src) ? src : {};
                      }

                      // Never move original objects, clone them
                      target[name] = jQuery.extend(deep, clone, copy);

                  // Don't bring in undefined values
                  } else if(copy !== undefined) {
                      target[name] = copy;
                  }
              }
          }
      }

      // Return the modified object
      return target;
  };

  return jQuery;
});