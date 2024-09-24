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
    './util'
], function(util) {
    "use strict";

    var EA = [];

    return createEvents;

    function createEvents() {
        var _listenersByEvName = {};

        trigger.on = on;

        return trigger;

        function trigger(evName, args) {
            var liz = util.getOwn.call(_listenersByEvName, evName);
            if(liz) liz.forEach(function(lis) { lis.apply(null, args || EA); });
        }

        function on(evName, lis) {
            var liz = util.getOwn.call(_listenersByEvName, evName) ||
                      (_listenersByEvName[evName] = []);

            liz.push(lis);
        }
    }
});