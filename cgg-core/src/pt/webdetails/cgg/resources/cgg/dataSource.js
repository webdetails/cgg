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
    './util',
    './interop'
], function(util, interop) {

    createDataSource.detectQueryType = detectQueryType;

    return createDataSource;

    function createDataSource(queryDef) {
        var queryType = detectQueryType(queryDef);
        if(!queryType) throw new Error("Unsupported data source type.");

        return new DataSource(queryType).configure(queryDef);
    }

    function DataSource(queryType) {
        var _ds = util.global.datasourceFactory.createDatasource(queryType);

        this.type = queryType;

        this.setParameter = function(name, value) {
            _ds.setParameter(name, interop.jsToJava(value));
            return this;
        };

        this.execute = function() {
            var result = String(_ds.execute());
            return eval('new Object(' + result + ');');
        };

        this.configure = function(config) {
            switch(this.type) {
                case 'cda':
                    _ds.setDefinitionFile(config.path);
                    _ds.setDataAccessId(config.dataAccessId);
                    break;

                //case 'cpk':
                //case 'legacy':
                default:
                    // TODO: Unsupported after all?
                    throw new Error("Unsupported data source of type '" +  this.type + "'.");
            }
            return this;
        };
    }

    // NOTE: The query type detection code should be kept in sync with
    // CDF's Dashboards.js Query constructor code.
    function detectQueryType(qd) {
        if(qd) {
            var qt = qd.queryType                 ? qd.queryType : // cpk goes here
                     qd.query                     ? 'legacy'     :
                     (qd.path && qd.dataAccessId) ? 'cda'        :
                     undefined;

            qd.queryType = qt;

            return qt;
        }
    }
});