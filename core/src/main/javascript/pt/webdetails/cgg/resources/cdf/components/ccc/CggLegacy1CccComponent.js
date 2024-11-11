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

define(['./BaseCccComponent'], function(BaseCccComponent) {

    // The version used by renderCccFromComponent
    var CggLegacy1CccComponent = BaseCccComponent.extend({
        _preCdaData:   null,
        doPrePostExec: false,

        setPreFetchedData: function(cdaData) { this._preCdaData = cdaData; },
        fetchDataCore:     function()        { return this._preCdaData;    }
    });

    return CggLegacy1CccComponent;
});
