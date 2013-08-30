// Portions Copyright (c) 2012 jQuery Foundation and other contributors, http://jquery.com/ 


var hasOwn = Object.prototype.hasOwnProperty;
// Shamelessly copied from jquery!
jQuery = $ = {};
jQuery.isPlainObject = function(ele){

  // Must be an Object.
  // Because of IE, we also have to check the presence of the constructor property.
  // Make sure that DOM nodes and window objects don't pass through, as well
  if ( !obj || jQuery.type(obj) !== "object" || obj.nodeType || jQuery.isWindow( obj ) ) {
     return false;
  }
  
  // Not own constructor property must be Object
  if ( obj.constructor &&
     !hasOwn.call(obj, "constructor") &&
     !hasOwn.call(obj.constructor.prototype, "isPrototypeOf") ) {
     return false;
  }
  
  // Own properties are enumerated firstly, so to speed up,
  // if last one is own, then all properties are own.

  var key;
  for ( key in obj ) {}
  
  return key === undefined || hasOwn.call( obj, key );
}
jQuery.isArray = Array.isArray || function( obj ) {
  return jQuery.type(obj) === "array";
}
jQuery.isWindow = function( obj ) {
  return obj && typeof obj === "object" && "setInterval" in obj;
}
jQuery.type = function( obj ) {
  return obj == null ?
     String( obj ) :
     "object";
}

jQuery.isPlainObject = function( obj ) {
  // Must be an Object.
  // Because of IE, we also have to check the presence of the constructor property.
  // Make sure that DOM nodes and window objects don't pass through, as well
  if ( !obj || jQuery.type(obj) !== "object" || obj.nodeType || jQuery.isWindow( obj ) ) {
     return false;
  }
  
  // Not own constructor property must be Object
  if ( obj.constructor &&
     !hasOwn.call(obj, "constructor") &&
     !hasOwn.call(obj.constructor.prototype, "isPrototypeOf") ) {
     return false;
  }
  
  // Own properties are enumerated firstly, so to speed up,
  // if last one is own, then all properties are own.

  var key;
  for ( key in obj ) {}
  
  return key === undefined || hasOwn.call( obj, key );
}
$.support = {};
$.extend = function() {
     var options, name, src, copy, copyIsArray, clone,
        target = arguments[0] || {},
        i = 1,
        length = arguments.length,
        deep = false;

    // Handle a deep copy situation
    if ( typeof target === "boolean" ) {
        deep = target;
        target = arguments[1] || {};
        // skip the boolean and the target
        i = 2;
    }

    // Handle case when target is a string or something (possible in deep copy)
    if ( typeof target !== "object" && !jQuery.isFunction(target) ) {
        target = {};
    }

    // extend jQuery itself if only one argument is passed
    if ( length === i ) {
        target = this;
        --i;
    }

    for ( ; i < length; i++ ) {
        // Only deal with non-null/undefined values
        if ( (options = arguments[ i ]) != null ) {
            // Extend the base object
            for ( name in options ) {
                src = target[ name ];
                copy = options[ name ];

                // Prevent never-ending loop
                if ( target === copy ) {
                    continue;
                }

                // Recurse if we're merging plain objects or arrays
                if ( deep && copy && ( jQuery.isPlainObject(copy) || (copyIsArray = jQuery.isArray(copy)) ) ) {
                    if ( copyIsArray ) {
                        copyIsArray = false;
                        clone = src && jQuery.isArray(src) ? src : [];

                    } else {
                        clone = src && jQuery.isPlainObject(src) ? src : {};
                    }

                    // Never move original objects, clone them
                    target[ name ] = jQuery.extend( deep, clone, copy );

                // Don't bring in undefined values
                } else if ( copy !== undefined ) {
                    target[ name ] = copy;
                }
            }
        }
    }

    // Return the modified object
    return target;
};

$.tipsy = function(){};
pv.Behavior.tipsy = function(){};

function getCccType(type) {
    if(type){
        var className = type.replace(/^ccc(.*)$/, '$1');
        return pvc[className];
    }
}

// NOTE: this function must be in sync with that of 
// BaseCccComponent#_preProcessChartDefinition
function preProcessChartDefinition(chartDef) {
    if(chartDef){
        // Obtain effective compatVersion
        var compatVersion = chartDef.compatVersion;
        if(compatVersion == null){
            compatVersion = typeof pvc.defaultCompatVersion === 'function' ? 
                            pvc.defaultCompatVersion() :
                            1;
        }
        
        if(compatVersion <= 1){
            // Properties that are no more registered in the component
            // and that had a name mapping.
            // The default mapping, for unknown properties, doesn't work.
            if('showLegend' in chartDef){
                chartDef.legend = chartDef.showLegend;
                delete chartDef.showLegend;
            }
            
            // Don't presume chartDef props must be own
            for(var p in chartDef){
                var m = /^barLine(.*)$/.exec(p);
                if(m){
                    p2 = 'secondAxis' + (m[1] || '');
                    chartDef[p2] = chartDef[p];
                    delete chartDef[p];
                }
            }
        }
        
        chartDef.extensionPoints = Dashboards.propertiesArrayToObject(chartDef.extensionPoints);
    }
}

function renderCccFromComponent(component, data) {
    try {
    
        // Adding ability to process external height and width
        var cd = component.chartDefinition;
        var w = cd.width  = +(parseFloat(params.get('width' )) || cd.width );
        var h = cd.height = +(parseFloat(params.get('height')) || cd.height);
        
        var multiChartOverflow = params.get('multiChartOverflow');
        if(multiChartOverflow) { cd.multiChartOverflow = multiChartOverflow; }
        
        var noChartBg = params.get('noChartBg') === 'true';
        
        var svgElem = document.documentElement;
        
        print("\nCGG PRINT CCC CHART " + component.type + " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" +
              " Debug:     " + pvc.debug + "\n" + 
              " Width:     " + w         + "\n" + 
              " Height:    " + h         + "\n" +
              " NoChartBg: " + noChartBg + "\n" +
              " MultiChartOverflow: " + (cd.multiChartOverflow || 'grow'));
        
        // Chart Background
        var bg;
        if(!noChartBg) {
            bg = document.createElementNS('http://www.w3.org/2000/svg','rect');
            bg.setAttribute('id',     'foo_35434765');
            bg.setAttribute('x',      '0');
            bg.setAttribute('y',      '0');
            bg.setAttribute('width',   w);
            bg.setAttribute('height',  h);
            bg.setAttribute('style',   'fill:white');
            svgElem.appendChild(bg);
        }
        
        // This may start causing problems to older dashboards
        // that trusted on preExecution not being called in CGG.
        if(typeof component.preExecution === 'function') {
            try {
                var contin = component.preExecution();
                if(contin != undefined && !contin) {
                    print("Ignoring 'preExecution' falsy result.");
                }
            } catch(ex) {
                // ignore
                print("Error in 'preExecution': " + ex);
            }
        }
        
        if(data) {
          if(typeof component.postFetch === 'function') {
              try {
                  var newData = data = component.postFetch(data);
                  if(newData !== undefined) { data = newData; }
              } catch(ex) {
                  // ignore
                  print("Error in 'postFetch': " + ex);
              }
          }
        } else {
          // May be set in preExecution
          data = component.valuesArray;
        }
        
        preProcessChartDefinition(component.chartDefinition);
        
        cd = $.extend({}, component.chartDefinition);
        cd.interactive = false;
        
        var ChartType = getCccType(component.type);
        var chart = new ChartType(cd);
        chart.setData(data);
        chart.render();
        
        // This may start causing problems to older dashboards
        // that trusted on postExecution not being called in CGG.
        if(typeof component.postExecution === 'function') {
            try {
                component.postExecution();
            } catch(ex) {
                // ignore
                print("Error in 'postExecution': " + ex);
            }
        }

        // Set the SVG element with the final chart width/height
        //  so that the PNGTranscoder can detect the image size.
        // When no data, no basePanel.
        var basePanel = chart.basePanel;
        if(basePanel) {
            w = basePanel.width ;
            h = basePanel.height;
        }

        svgElem.setAttribute('width',  w);
        svgElem.setAttribute('height', h);
        
        // Also, fix the bg size
        if(bg) {
            bg.setAttribute('width',  w);
            bg.setAttribute('height', h);
        }
    } catch(ex) {
        print("Error while rendering CCC chart: " + ex);
        throw ex;
    }
}

var Dashboards = Dashboards || {};
Dashboards.propertiesArrayToObject = function(pArray) {
  var obj = {};
  for (p in pArray) if (pArray.hasOwnProperty(p)) {
    var prop = pArray[p];
    obj[prop[0]] = prop[1];
  }
  return obj;
};

Dashboards.objectToPropertiesArray = function(obj) {
  var pArray = [];
  for (key in obj) if (obj.hasOwnProperty(key)) {
    pArray.push([key,obj[key]]);
  }
  return pArray;
};

Dashboards.log = function(m, type) {
    print((type || 'LOG').toUpperCase() + ": " + m);
};
