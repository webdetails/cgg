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


var settings = {
          width: 300,
          height: 200,
          animate: true,
          title: "line chart - Timeseries with extra options",
          titlePosition: "top",
          titleSize: 40,
          legend: true,
          legendPosition: "top",
          legendAlign: "right",

          orientation: 'vertical',
          timeSeries: true,
          timeSeriesFormat: "%Y-%m-%d",

          showValues: false,
          showDots: true,
          yAxisSize: 30,
          xAxisSize: 30,
          xAxisFullGrid: true,
          yAxisFullGrid: true,
          extensionPoints: {
            titleLabel_font: "13px serif",
            dot_fillStyle: "white",
            dot_shape: "square",
            dot_lineWidth: 2,
            dot_shapeRadius: 4,
            line_lineWidth: 0.5
          }

        };

