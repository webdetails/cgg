/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

define([
    './util',
    'cgg'
], function(util, cgg) {
    var _controlClassHandlers = [defaultClassHandler];
    var _controlClassesScope  = {};

    return {
        registerControlClassHandler: registerClassHandler,
        registerControlClass:        registerClass,
        getControlClass:             getControlClass,
        propertiesArrayToObject:     propsArrayToObject,
        objectToPropertiesArray:     objectToPropsArray,

        log: function(m, type) {
            cgg.print((type || 'LOG').toUpperCase() + ": " + m);
        }
    };

    // -------------

    function defaultClassHandler(typeName, scope) {
        var TypeName = typeName.substring(0,1).toUpperCase() +
                       typeName.substring(1);

        // try _TypeComponent_, _type_ and _Type_ as class names
        var typeNames = [TypeName + 'Component', typeName, TypeName];

        for(var i = 0, N = typeNames.length ; i < N ; i++) {
            // If the value of a name is not a function, keep on trying.
            var Class = scope[typeNames[i]];
            if(Class && typeof Class === 'function') return Class;
        }
    }

    function registerClass(type, Class) {
        _controlClassesScope[type] = Class;
    }

    function registerClassHandler(handler) {
        _controlClassHandlers.push(handler);
    }

    function getControlClass(control, scope) {
        // see if there is a class defined for this control
        var typeName = control.type;
        if(typeof typeName === 'function')
            // <=> control.type() ; the _this_ in the call is _control_
            typeName = typeName.call(control);

        if(typeName) {
            if(!scope) scope = _controlClassesScope;

            for(var i = 0, N = _controlClassHandlers.length ; i < N ; i++) {
                var Class = _controlClassHandlers[i](typeName, scope);
                if(Class) return Class;
            }
        }
        // return undefined;
    }

    function propsArrayToObject(pArray) {
        var obj = {};
        for(var p in pArray)
            if(util.hasOwn.call(pArray, p)) {
                var prop = pArray[p];
                obj[prop[0]] = prop[1];
            }

        return obj;
    }

    function objectToPropsArray(obj) {
        var pArray = [];
        for(var key in obj)
            if(util.hasOwn.call(obj, key))
                pArray.push([key, obj[key]]);

        return pArray;
    }
});