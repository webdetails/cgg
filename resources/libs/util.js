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
define(function() {

    var global   = (function() { return this; }());
    var O_hasOwn = Object.prototype.hasOwnProperty;

    return {
        global: global,
        hasOwn: O_hasOwn,
        getOwn: O_getOwn,
        makeInstance: makeInstance
    };

    // LIB

    function O_getOwn(p, dv) {
        return O_hasOwn.call(this, p) ? this[p] : dv;
    }

    function makeInstance(Class, args) {
        var o = Object.create(Class.prototype);
        if(args) { Class.apply(o, args); } else { Class.apply(o); }
        return o;
    }
});