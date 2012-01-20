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
    return {
        "cccBarChart": pvc.BarChart,
        "cccDotChart": pvc.DotChart,
        "cccLineChart": pvc.LineChart,
        "cccStackedLineChart": pvc.StackedLineChart,
        "cccStackedAreaChart": pvc.StackedAreaChart,
        "cccPieChart": pvc.PieChart,
        "cccHeatGridChart": pvc.HeatGridChart,
        "cccBulletChart": pvc.BulletChart,
        "cccMetricDotChart": pvc.MetricDotChart,
        "cccMetricLineChart": pvc.MetricLineChart,
        "cccStackedLineChart": pvc.mStackedLineChart,
        "cccStackedAreaChart": pvc.mStackedAreaChart,
        "cccWaterfallChart": pvc.WaterfallChart,
        "cccBoxplotChart": pvc.BoxplotChart        
    }[type];
};

function convertExtensionPoints(extPoints) {
    var ep = {};
    extPoints.forEach(
        function(a){
	    ep[a[0]]=a[1];
	});
   return ep;
}

function renderCccFromComponent(component, data) {

	if(typeof component.postFetch === 'function'){
		try{
			data = component.postFetch(data);
		}
		catch(e){print("Error in postfetch: " + e)} // ignore
	}
    component.chartDefinition.extensionPoints = convertExtensionPoints(component.chartDefinition.extensionPoints);
    var o = $.extend({},component.chartDefinition);
    o.showTooltips = false;
    o.tooltipFormat = function(){};
    var chartType = getCccType(component.type);
    var chart = new chartType(o);

    chart.setData(data,{
        crosstabMode: component.crosstabMode,
        seriesInRows: component.seriesInRows
    });
    chart.render();
}


var Dashboards = Dashboards || {};
Dashboards.log = function(m,type){ 
	if (type)
		print(type + ": " + m);
	else
		print("LOG: " + m);
}