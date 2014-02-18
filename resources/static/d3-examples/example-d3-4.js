
lib("cgg-env.js");
cgg.init();

var d3 = require("d3.v3");

function removeAnimations(){
    // add a duration function to the selection prototype
    d3.selection.prototype.duration   = function(){ return this }
    // hack the transition function of d3's select API
    d3.selection.prototype.transition = function(){ return this }
    // hack the transition function of d3's select API
    d3.selection.prototype.delay = function(){ return this }
    d3.selection.prototype.ease = function(){ return this }
}

removeAnimations();

var data = [
  {
    "apples": 53245,
    "oranges": 200
  }, {
    "apples": 28479,
    "oranges": 0
  }, {
    "apples": 19697,
    "oranges": 200
  }, {
    "apples": 24037,
    "oranges": 0 
  }, {
    "apples": 40245,
    "oranges": 200
  }];

var width = 960,
    height = 500,
    radius = Math.min(width, height) / 2;

var color = d3.scale.category20();

var pie = d3.layout.pie()
    .value(function(d) { return d.apples; })
    .sort(null);

var arc = d3.svg.arc()
    .innerRadius(radius - 100)
    .outerRadius(radius - 20);

var svg = d3.select("svg")
    .attr("width", width)
    .attr("height", height)
  .append("g")
    .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

  var path = svg.datum(data).selectAll("path")
      .data(pie)
    .enter().append("path")
      .attr("fill", function(d, i) { return color(i); })
      .attr("d", arc)
      .each(function(d) { this._current = d; }); // store the initial angles

  d3.selectAll("input")
      .on("change", change);

  var timeout = setTimeout(function() {
    d3.select("input[value=\"oranges\"]").property("checked", true).each(change);
  }, 2000);

  function change() {
    var value = this.value;
    clearTimeout(timeout);
    pie.value(function(d) { return d[value]; }); // change the value function
    path = path.data(pie); // compute the new angles
    path.transition().duration(750).attrTween("d", arcTween); // redraw the arcs
  }

function type(d) {
  d.apples = +d.apples || 0;
  d.oranges = +d.oranges || 0;
  return d;
}

// Store the displayed angles in _current.
// Then, interpolate from _current to the new angles.
// During the transition, _current is updated in-place by d3.interpolate.
function arcTween(a) {
  var i = d3.interpolate(this._current, a);
  this._current = i(0);
  return function(t) {
    return arc(i(t));
  };
}