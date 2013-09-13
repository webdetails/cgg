/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

lib("protovis-bundle.js");

cgg.init && cgg.init();

cgg.utils.initDocument("dial.svg");

var scale = params.get("scale"),
    colors = params.get("colors"),
    scale = (scale !== null && scale.length > 0 ? scale : [0,25,50,100]),
    colors = (colors !== null && colors.length > 0 ? colors : ["red", "yellow", "green"]),
    min = parseFloat(scale[0]),
    max = parseFloat(scale[scale.length - 1]),
    value = parseFloat(params.get("value"));

/*
 * reset max to biggest of the original maximum or the actual value
 * so we handle values bigger than the declared maximum
 */

min = (value < min ? value : min);
max = (value > max ? value : max);
scale[scale.length - 1] = max;
scale[0] = min;


/*
 * Construct the arc list
 */

var arcs = [];

for (var i = 0; i < colors.length; i++) {
  arcs.push({min: scale[i], max: scale[i+1], color: colors[i]});
}
/* Sizing and scales. */

var w = 600,
    h = 300,
    r = .75 * h,
    a = pv.Scale.linear(min, max).range(0, Math.PI),
    ticks = a.ticks();
    start = -Math.PI;
/* The root panel. */
var vis = new pv.Panel()
    .canvas(document.getElementById("escala_cor"))
    .width(w)
    .height(h);

/* The wedge, with centered label. */
vis.add(pv.Wedge)
    .data(arcs)
    .bottom(25)
    .left(w / 2)
    .innerRadius(r - 15)
    .outerRadius(r)
    .startAngle(function(d){return - Math.PI + a(d.min);})
    .fillStyle(function(d){return d.color;})
    .angle(function(d){return a(d.max) - a(d.min);});

vis.add(pv.Wedge)
    .data(ticks)
    .bottom(25)
    .left(w / 2)
    .innerRadius(r + 10.5)
    .outerRadius(r + 25)
    .startAngle(function(d){ return - Math.PI + a(d) - 0.0025})
    .fillStyle("#66CCFF")
    .angle(0.005)
.add(pv.Wedge)
    .innerRadius(r+39)
    .outerRadius(r+40)
    .angle(0)
.anchor("outer").add(pv.Label)
    .textAngle(0)
    .font("12.4 sans-serif")
    .textStyle("#626a6e")
    .textAlign("center")
    .textBaseline("middle")
    .text(function(d) {return d;});

vis.render();

var rotation = (value - min )/(max - min) * 180;
document.getElementById("ponteiro").setAttribute("transform","rotate("+ rotation + ",300,275)")


/*
 * This is what a CDA query would look like if we had a proper example set up:
 */
/*
var referenceDate = params.get("date"); referenceDate = (referenceDate != null ? referenceDate : '${TODAY()}');
var country = params.get("country"); country = (country != null ? country : "All");
var datasource = datasourceFactory.createDatasource("cda");

datasource.setDefinitionFile("/path/to/cdaFile.cda");
datasource.setDataAccessId("myQueryId");
datasource.setParameter("DATE", referenceDate);
datasource.setParameter("COUNTRY", country);
var data = JSON.parse(String(datasource.execute()));
*/
