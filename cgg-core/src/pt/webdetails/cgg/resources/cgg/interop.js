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
    './util'
], function(util) {

    var javaLang = util.global.java.lang;

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