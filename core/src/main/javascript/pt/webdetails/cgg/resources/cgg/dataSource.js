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

define([
    './interop'
], function(interop) {

    createDataSource.detectQueryType = detectQueryType;

    return createDataSource;

    function createDataSource(queryDef) {
        var queryType = detectQueryType(queryDef);
        if(!queryType) throw new Error("Unsupported data source type.");

        return new DataSource(queryType).configure(queryDef);
    }

    function DataSource(queryType) {
        var _ds = globalThis.datasourceFactory.createDatasource(queryType);

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
