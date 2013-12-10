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
define(['cgg'], function(cgg) {

    // Defines an AMD loader plugin.
    return {load: load};

    function load(id, require, callback) {
        var cccVersion = getCccVersion();

        // When no id, => all 3 ccc exports in one (as exported by main).

        var mid = './' + cccVersion + '/' + (id || 'main');

        // Will write multiple times, for each id of ccc...
        (cgg.debug > 2) && cgg.print("Using CCC v" + cccVersion);

        require([mid], callback);
    }

    function getCccVersion() {
        var version = cgg.params.get('cccVersion');
        version = version == null ? '' : String(version);
        return version || '2.0';
    }
});