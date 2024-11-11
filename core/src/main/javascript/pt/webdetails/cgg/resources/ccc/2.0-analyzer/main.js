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
    'cgg',
    './protovis',
    './pvc'
], function(cgg, pv, pvc) {
    // Let cgg init, as well.
    cgg.debug = 4;
    cgg.init();

    // Needed otherwise debugging mode throws...
    cgg.debug = 0;
    JSON.stringify = String;

    return {
        pv:  pv,
        def: pvc._def,
        pvc: pvc
    };
});