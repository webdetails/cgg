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