/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/
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