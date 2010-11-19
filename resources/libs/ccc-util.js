function getCccType(type) {
    return {
        "cccDotChart": pvc.DotChart,
        "cccLineChart": pvc.LineChart,
        "cccStackedLineChart": pvc.StackedLineChart,
        "cccStackedAreaChart": pvc.StackedAreaChart,
        "cccPieChart": pvc.PieChart
    }[type];
};

function convertExtensionPoints(extPoints) {
    var ep = {};
    extPoints.forEach(
        function(a){
	    ep[a[0]]=a[1];
	});
   return ep;
}

function renderCcccFromComponent(component, data) {
    component.chartDefinition.extensionPoints = convertExtensionPoints(component.chartDefinition.extensionPoints);
    var o = $.extend({},component.chartDefinition);
    var chartType = getCccType(component.type);
    var chart = new chartType(o);

    chart.setData(data,{
        crosstabMode: component.crosstabMode,
        seriesInRows: component.seriesInRows
    });
    print(chart.options.tooltipFormat);
    chart.render();
}

