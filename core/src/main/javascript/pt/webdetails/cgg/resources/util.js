/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


define(function() {

    var O_hasOwn = Object.prototype.hasOwnProperty;

    return {
        hasOwn: O_hasOwn,
        getOwn: O_getOwn,
        slice:  Array.prototype.slice,
        compare: compare,
        makeInstance: makeInstance
    };

    // LIB
    function compare(a, b) {
        return a === b ? 0 : a > b ? 1 : -1;
    }
    
    function O_getOwn(p, dv) {
        return O_hasOwn.call(this, p) ? this[p] : dv;
    }

    function makeInstance(Class, args) {
        var o = Object.create(Class.prototype);
        if(args) { Class.apply(o, args); } else { Class.apply(o); }
        return o;
    }
});
