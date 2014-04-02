
lib("cgg-env.js");

cgg.init();

var d3 = require("d3.v3");

function removeAnimations(){
    d3.selection.prototype.duration   = function(){ return this }
    d3.selection.prototype.transition = function(){ return this }
    d3.selection.prototype.delay = function(){ return this }
    d3.selection.prototype.ease = function(){ return this }
}

removeAnimations();
var data = [
    {
        "5 to 13 Years": "4499890",
        "14 to 17 Years": "2159981",
        "18 to 24 Years": "3853788",
        "25 to 44 Years": "10604510",
        "45 to 64 Years": "8819342",
        "65 Years and Over": "4114496",
        "State": "CA",
        "Under 5 Years": "2704659"
    }, {
        "5 to 13 Years": "3277946",
        "14 to 17 Years": "1420518",
        "18 to 24 Years": "2454721",
        "25 to 44 Years": "7017731",
        "45 to 64 Years": "5656528",
        "65 Years and Over": "2472223",
        "State": "TX",
        "Under 5 Years": "2027307"
    },
    {
        "5 to 13 Years": "2141490",
        "14 to 17 Years": "1058031",
        "18 to 24 Years": "1999120",
        "25 to 44 Years": "5355235",
        "45 to 64 Years": "5120254",
        "65 Years and Over": "2607672",
        "State": "NY",
        "Under 5 Years": "1208495"
    },
    {
        "5 to 13 Years": "1938695",
        "14 to 17 Years": "925060",
        "18 to 24 Years": "1607297",
        "25 to 44 Years": "4782119",
        "45 to 64 Years": "4746856",
        "65 Years and Over": "3187797",
        "State": "FL",
        "Under 5 Years": "1140516"
    },
    {
        "5 to 13 Years": "1558919",
        "14 to 17 Years": "725973",
        "18 to 24 Years": "1311479",
        "25 to 44 Years": "3596343",
        "45 to 64 Years": "3239173",
        "65 Years and Over": "1575308",
        "State": "IL",
        "Under 5 Years": "894368"
    },
    {   
        "5 to 13 Years": "1345341",
        "14 to 17 Years": "679201",
        "18 to 24 Years": "1203944",
        "25 to 44 Years": "3157759",
        "45 to 64 Years": "3414001",
        "65 Years and Over": "1910571",
        "State": "PA",
        "Under 5 Years": "737462"
    }];

var margin = {top: 20, right: 20, bottom: 30, left: 40},
    width = 960 - margin.left - margin.right,
    height = 500 - margin.top - margin.bottom;

var x0 = d3.scale.ordinal()
    .rangeRoundBands([0, width], .1);

var x1 = d3.scale.ordinal();

var y = d3.scale.linear()
    .range([height, 0]);

var color = d3.scale.ordinal()
    .range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);

var xAxis = d3.svg.axis()
    .scale(x0)
    .orient("bottom");


var yAxis = d3.svg.axis()
    .scale(y)
    .orient("left")
    .tickFormat(d3.format(".2s"))


var svg = d3.select("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
  .append("g")
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
    .style("font", "10px sans-serif");

  var ageNames = d3.keys(data[0]).filter(function(key) { return key !== "State"; });

  data.forEach(function(d) {
    d.ages = ageNames.map(function(name) { return {name: name, value: +d[name]}; });
  });

  x0.domain(data.map(function(d) { return d.State; }));
  x1.domain(ageNames).rangeRoundBands([0, x0.rangeBand()]);
  y.domain([0, d3.max(data, function(d) { return d3.max(d.ages, function(d) { return d.value; }); })]);

  var xAxis = svg.append("g")
      .attr("class", "x axis")
      .attr("transform", "translate(0," + height + ")")
      .call(xAxis);

  var yAxis = svg.append("g")
      .attr("class", "y axis")
      .call(yAxis)

    yAxis.append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 6)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text("Population");

  var state = svg.selectAll(".state")
      .data(data)
    .enter().append("g")
      .attr("class", "g")
      .attr("transform", function(d) { return "translate(" + x0(d.State) + ",0)"; });

  state.selectAll("rect")
      .data(function(d) { return d.ages; })
    .enter().append("rect")
      .attr("width", x1.rangeBand())
      .attr("x", function(d) { return x1(d.name); })
      .attr("y", function(d) { return y(d.value); })
      .attr("height", function(d) { return height - y(d.value); })
      .style("fill", function(d) { return color(d.name); });

  var legend = svg.selectAll(".legend")
      .data(ageNames.slice().reverse())
    .enter().append("g")
      .attr("class", "legend")
      .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });

  legend.append("rect")
      .attr("x", width - 18)
      .attr("width", 18)
      .attr("height", 18)
      .style("fill", color);

  legend.append("text")
      .attr("x", width - 24)
      .attr("y", 9)
      .attr("dy", ".35em")
      .style("text-anchor", "end")
      .text(function(d) { return d; });

    yAxis.selectAll("path")
        .style("fill"," none")
      .style("stroke", "#000")
      .style("shape-rendering", "crispEdges");

    xAxis.selectAll("path")
    .style("display", "none");

    yAxis.selectAll("line")
        .style("fill"," none")
      .style("stroke", "#000")
      .style("shape-rendering", "crispEdges");

    xAxis.selectAll("line")
      .style("fill"," none")
      .style("stroke", "#000")
      .style("shape-rendering", "crispEdges");