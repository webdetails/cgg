
var BaseComponent = Base.extend({
  cgg: true
});

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
    print(entry);
  },

  synchronous: function(callback, args) {
    if(!this.preExec()) {
      print("Ignoring 'preExecution' falsy result. Printing anyway.");
    }

    // Execute
    callback.call(this, args || []);

    this.postExec();
  },

  triggerQuery: function(queryDef, callback) {
    this.synchronous(function() {  this.fetchData(queryDef, callback); });
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
        if(newData !== undefined) { data = newData; }
      } catch(ex) {
        (cgg.debug > 0) && this.logLifecycle('cdf:error', "Ignoring error in 'postFetch': " + ex);
      }
    }

    (cgg.debug > 3) && this.logLifecycle('cdf:postFetch', cgg.logStringify(data));

    callback(data);
  },

  fetchDataCore: function(queryDef) {
    var ds = this.createDataSource(queryDef);
    if(ds) {
      this.configureDataSource(ds, queryDef);

      this.setDataSourceParameters(ds);
        
      return this.executeDatasource(ds);
    }
  },

  createDataSource: function(queryDef) {
    var queryType = this.detectQueryType(queryDef);
    if(queryType) {
      // CGG injected variable
      /*global datasourceFactory:true */
      return datasourceFactory.createDatasource(queryType);
    }
  },

  // Data source type specific configuration.
  configureDataSource: function(ds, queryDef) {
    // Data source specific arguments
    switch(queryDef.queryType) {
      case 'cda':
        ds.setDefinitionFile(queryDef.path);
        ds.setDataAccessId(queryDef.dataAccessId);
        break;

      //case 'cpk':
      //case 'legacy':
      default:
        // TODO:
        throw new Error("Unsupported data source of type '" +  queryType + "'.");
    }
  },

  setDataSourceParameters: function(ds) {
    // Comparatively to specifying parameters
    //  for the *static* data source, 
    //  the following method risks passing parameters that 
    //  are not defined in the *current* data source.
    // Can happen if a data source is changed dynamically in preExecution, for example.
    // Generally, there's no harm in passing unapplicable parameters.
    params
    .names()
    .forEach(function(pname) {
      if(this.filterDataSourceParameter(pname)) {
        ds.setParameter(pname, cgg.jsToJava(cgg.getParameterValue(pname)));
      }
    }, this);
  },

  filterDataSourceParameter: function(pname) {
    switch(pname) {
      case 'debug':
      case 'debugLevel': return false;
    }
    return true;
  },

  executeDatasource: function(ds) {
    var queryResult = String(ds.execute());
      
    return eval('new Object(' + queryResult + ');');
  },

  // NOTE: The query type detection code should be kept in sync with 
  // CDF's Dashboards.js Query constructor code.
  detectQueryType: function(qd) {
    if(!qd) { qd = this.chartDefinition; }
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
