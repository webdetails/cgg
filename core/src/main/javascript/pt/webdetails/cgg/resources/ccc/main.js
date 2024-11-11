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

define(['require', 'cgg'], function(contextualRequire, cgg) {

    // Defines an AMD loader plugin.
    return {load: load};

    function load(id, require, callback) {
        var cccVersion = getCccVersion();

        // When no id, => all 3 ccc exports in one (as exported by main).

        var mid = './' + cccVersion + '/' + (id || 'main');

        // Will write multiple times, for each id of ccc...
        (cgg.debug > 2) && cgg.print("Using CCC v" + cccVersion);

        contextualRequire([mid], callback);
    }

    function getCccVersion() {
        var version = cgg.params.get('cccVersion');
        version = version == null ? '' : String(version);
        return version || '2.0';
    }
});
