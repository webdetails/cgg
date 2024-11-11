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

define(['Base'], function(Base) {

    var BaseComponent = Base.extend({
        // Allows quick `this.cgg` testing,
        // from within components.
        cgg: true
    });

    return BaseComponent;
});