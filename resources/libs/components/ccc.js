
var SvgComponent = UnmanagedComponent.extend({
  init: function() {
    var cd = this.chartDefinition;
    this.width  = cd.width  = parseFloat(params.get('width' )) || +cd.width  || +this.width ;
    this.height = cd.height = parseFloat(params.get('height')) || +cd.height || +this.height;
  },

  // The SVG Element
  getRootElement: function() {
    return document.documentElement;
  },

  filterDataSourceParameter: function(pname) {
    if(!this.base(pname)) { return false; }

    switch(pname) {
      case 'width':
      case 'height':
      case 'noChartBg':
        return false;
    }

    return true;
  },

  update: function() {
    this.init();
    this.renderChart();
    this.fixCanvasSize();
  },

  renderChart: function() {
    var me = this;
    var cd = me.chartDefinition;
    if(this.detectQueryType()) {
      me.triggerQuery(me.chartDefinition, function(cdaData) { me.render(cdaData); });
    } else if(me.valuesArray != undefined) {
      me.synchronous(function() { me.render(me.valuesArray); });
    } else {
      // initialize the component only
      me.synchronous(function() { me.render(); });
    }
  },

  render: function(cdaData) {
    var cd = this.chartDefinition;
    this.width  = +cd.width  || +this.width;
    this.height = +cd.height || +this.height;
    this.noChartBg = params.get('noChartBg') === 'true';

    // Chart Background
    var bg;
    if(!this.noChartBg) {
        bg = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
        bg.setAttribute('x',      '0');
        bg.setAttribute('y',      '0');
        bg.setAttribute('width',  this.width );
        bg.setAttribute('height', this.height);
        bg.setAttribute('style',  'fill:white');
        this.getRootElement().appendChild(bg);
    }

    this._bg = bg;

    this.renderCore(cdaData);
  },

  // Do something useful
  renderCore: function(cdaData) {},

  fixCanvasSize: function() {
    // Set the SVG element with the final chart width/height
    //  so that the PNGTranscoder can detect the image size.
    var svgElem = this.getRootElement();
    svgElem.setAttribute('width',  this.width);
    svgElem.setAttribute('height', this.height);
    
    // Also, fix the bg size
    var bg = this._bg;
    if(bg) {
        bg.setAttribute('width',  this.width);
        bg.setAttribute('height', this.height);
    }
  }
});

// --------------

var ProtovisComponent = SvgComponent.extend({
  vis: null,

  renderCore: function(cdaData) {
    if(cgg.debug > 2) {
      print("Protovis - main options\n" +
            "  Width:     " + this.width + "\n" + 
            "  Height:    " + this.height + "\n" +
            "  NoChartBg: " + this.noChartBg);
    }

    var vis = new pv.Panel()
      .width (this.width)
      .height(this.height);

    this.vis = vis;
    this.customfunction(vis, cdaData);
    vis.root.render();
  },

  processdata: function(cdaData) {
    this.render(cdaData);
  },

  fixCanvasSize: function() {
    if(this.vis) {
      this.base();
    }
  }
});

var BaseCccComponent = SvgComponent.extend({
  chart: null,

  init: function() {
    this.base();

    var cd = this.chartDefinition;
    var multiChartOverflow = params.get('multiChartOverflow');
    if(multiChartOverflow) { cd.multiChartOverflow = multiChartOverflow; }
  },

  filterDataSourceParameter: function(pname) {
    if(!this.base(pname)) { return false; }

    switch(pname) {
      case 'multiChartOverflow':
        return false;
    }
    return true;
  },

  render: function(cdaData) {
    this.preProcessChartDefinition();
    this.base(cdaData);
  },

  preProcessChartDefinition: function() {
    var cd = this.chartDefinition;
    if(cd) {
      // Obtain effective compatVersion
      var compatVersion = cd.compatVersion;
      if(compatVersion == null) {
        compatVersion = typeof pvc.defaultCompatVersion === 'function' ? 
                        pvc.defaultCompatVersion() :
                        1;
      }
      
      if(compatVersion <= 1) {
        // Properties that are no longer registered in the component
        // and that had a name mapping.
        // The default mapping, for unknown properties, doesn't work.
        if('showLegend' in cd) {
          cd.legend = cd.showLegend;
          delete cd.showLegend;
        }
        
        // Don't presume cd props must be own
        for(var p in cd) {
          var m = /^barLine(.*)$/.exec(p);
          if(m) {
            p2 = 'secondAxis' + (m[1] || '');
            cd[p2] = cd[p];
            delete cd[p];
          }
        }
      }
      
      cd.extensionPoints = Dashboards.propertiesArrayToObject(cd.extensionPoints);
    }
  },

  renderCore: function(cdaData) {
    var cd = this.chartDefinition;
    cd.interactive = false;

    if(cgg.debug > 2) {
      print("CCC - main options\n" +
            "  Width:     " + this.width     + "\n" + 
            "  Height:    " + this.height    + "\n" +
            "  NoChartBg: " + this.noChartBg + "\n" +
            "  MultiChartOverflow: " + (cd.multiChartOverflow || ''));
    }

    var ChartType = this.getCccType(this.type);
    var chart = new ChartType(cd);
    this.chart = chart;
    chart.setData(cdaData);
    chart.render();
  },

  fixCanvasSize: function() {
    if(this.chart) {
      // When no data, no basePanel.
      var basePanel = this.chart.basePanel;
      if(basePanel) {
          this.width  = basePanel.width ;
          this.height = basePanel.height;
      }

      this.base();
    }
  },

  getCccType: function(type) {
      if(type) {
          var className = type.replace(/^ccc(.*)$/, '$1');
          return pvc[className];
      }
  }
});

// --------------

// The version used by renderCccFromComponent
var CggLegacy1CccComponent = BaseCccComponent.extend({
  _preCdaData:   null,
  doPrePostExec: false,
  
  setPreFetchedData: function(cdaData) { this._preCdaData = cdaData; },
  fetchDataCore:     function()        { return this._preCdaData;    }
});

// --------------

Dashboards._registerControlClassHandler(function(typeName) {
  if(typeName.substr(0, 3).toLowerCase() === 'ccc') {
    return BaseCccComponent;
  }
});

// --------------

// Not all versions of CCC have pvc.stringify.
if(pvc.stringify) {
  cgg.logStringify = function(s) {
    return pvc.stringify(s, {ownOnly: false}); 
  };
}

// --------------
// idem
if(pvc.setDebug) {
  pvc.setDebug(cgg.debug);
}
