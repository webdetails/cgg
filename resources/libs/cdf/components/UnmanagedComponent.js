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
    'cgg',
    'cgg/dataSource',
    './BaseComponent'
], function(cgg, dataSource, BaseComponent) {

    var UnmanagedComponent = BaseComponent.extend({
        doPrePostExec: true,

        logLifecycle: function(e, msg) {
            var eventStr;
            var eventName = e.substr(4);
            switch(eventName) {
                case "preExecution":  eventStr = ">Start"; break;
                case "postExecution": eventStr = "<End  "; break;
                case "postFetch":     eventStr = "=Data "; break;
                case "error":         eventStr = "!Error"; break;
                default:              eventStr = "      "; break;
            }

            var entry = " [Lifecycle " + eventStr + "] " + this.name + " [" + this.type + "] [" + eventName + "]";
            if(msg) { entry += ": " + msg; }
            cgg.print(entry);
        },

        synchronous: function(callback, args) {
            if(!this.preExec()) cgg.print("Ignoring 'preExecution' falsy result. Printing anyway.");

            // Execute
            callback.call(this, args || []);

            this.postExec();
        },

        triggerQuery: function(queryDef, callback) {
            this.synchronous(function() { this.fetchData(queryDef, callback); });
        },

        preExec: function() {
            // Pre-execution
            // This may start causing problems to older dashboards
            //  that trusted on preExecution not being called in CGG.
            //  Here trusting on that catching exceptions is good enough (no-side-effects assumption).
            if(this.doPrePostExec) {
                (cgg.debug > 2) && this.logLifecycle('cdf:preExecution');

                if(typeof this.preExecution === 'function') {
                    try {
                        var contin = this.preExecution();
                        if(contin !== undefined && !contin) {
                            return false;
                        }
                    } catch(ex) {
                        (cgg.debug > 0) && this.logLifecycle('cdf:error', "Ignoring error in 'preExecution': " + ex);
                    }
                }
            }

            return true;
        },

        postExec: function() {
            // Post-execution
            // This may start causing problems to older dashboards
            //  that trusted on postExecution not being called in CGG.
            // Here trusting on that catching exceptions is good enough (no-side-effects assumption).
            if(this.doPrePostExec) {
                (cgg.debug > 2) && this.logLifecycle('cdf:postExecution');

                if(typeof this.postExecution === 'function') {
                    try {
                        this.postExecution();
                    } catch(ex) {
                        (cgg.debug > 0) && this.logLifecycle('cdf:error', "Ignoring error in 'postExecution': " + ex);
                    }
                }
            }
        },

        // Data / DataSource methods

        fetchData: function(queryDef, callback) {
            (cgg.debug > 2) && this.logLifecycle('cdf:postFetch');

            var data = this.fetchDataCore(queryDef);

            if(typeof this.postFetch === 'function') {
                try {
                    var newData = this.postFetch(data);
                    if(newData !== undefined) data = newData;
                } catch(ex) {
                    (cgg.debug > 0) && this.logLifecycle('cdf:error', "Ignoring error in 'postFetch': " + ex);
                }
            }

            (cgg.debug > 3) &&
            this.logLifecycle('cdf:postFetch', JSON.stringify(data));

            callback(data);
        },

        fetchDataCore: function(queryDef) {
            var ds = cgg.dataSource(queryDef);
            if(ds) {
                this.setDataSourceParameters(ds);
                return ds.execute();
            }
        },

        setDataSourceParameters: function(ds) {
            // Comparatively to specifying parameters
            //  for the *static* data source,
            //  the following method risks passing parameters that
            //  are not defined in the *current* data source.
            // Can happen if a data source is changed dynamically in preExecution, for example.
            // Generally, there's no harm in passing unapplicable parameters.
            cgg.params
            .names()
            .forEach(function(name) {
                if(this.filterDataSourceParameter(name)) {
                    ds.setParameter(
                        name,
                        cgg.getParameterValue(name));
                }
            }, this);
        },

        filterDataSourceParameter: function(name) {
            switch(name) {
                case 'debug':
                case 'debugLevel': return false;
            }
            return true;
        },

        detectQueryType: function(queryDef) {
            var qd = this.queryDefinition || this.chartDefinition;
            return dataSource.detectQueryType(qd);
        }
    });

    return UnmanagedComponent;
});