/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

define(function() {

    var javaLang = java.lang;

    return {
        javaToJS: javaToJS,
        jsToJava: jsToJava
    };

    // LIB

    function javaToJS(v) {
        if(v == null) return v;
        if(typeof v === 'object') {
            if(v instanceof javaLang.String ) return '' + v;
            if(v instanceof javaLang.Number ) return +v;
            if(v instanceof javaLang.Boolean) return !!v;
            if(v.getClass().isArray()) { // !(v instanceof Array) &&
                var a = [];
                var L = v.length;
                for(var i = 0 ; i < L ; i++) a.push(javaToJS(v[i]));
                return a;
            }
        }
        return v;
    }

    function jsToJava(v) {
        if(v == null) return v;
        if(typeof v === 'object') {
            if(typeof v === 'string')  return new javaLang.String(v);
            if(typeof v === 'number')  return new javaLang.Double(v);
            if(typeof v === 'boolean') return new javaLang.Boolean(v);
            if(v instanceof Array) { // !(v instanceof Array) &&
                var L = v.length;
                var a = javaLang.reflect.Array.newInstance(javaLang.String, L);
                for(var i = 0 ; i < L ; i++) a[i] = jsToJava(v[i]);
                return a;
            }
        }
        return v;
    }
});
