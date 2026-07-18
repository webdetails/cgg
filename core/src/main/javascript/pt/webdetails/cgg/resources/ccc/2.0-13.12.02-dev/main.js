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


define([
    './protovis',
    './def',
    './pvc',
    '../protovis-cgg'
], function(pv, def, pvc, pvCgg) {

    // Set the default compatibility version to CCC v1.
    pvc.defaultCompatVersion(1);

    // Install protovis CGG
    pvCgg(pv);

    return {
        pv:  pv,
        def: def,
        pvc: pvc
    };
});