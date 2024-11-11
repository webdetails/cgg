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


importPackage(java.awt);

graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

graphics.setColor(Color.red);
var xarr = [2,84,84,22,17,17,2],
    yarr = [5,5,20,20,30,20,20];
graphics.drawPolygon(xarr,yarr, 7);
graphics.fillPolygon(xarr,yarr, 7);

var label = params.get('name');
label = label == null ? new java.lang.String("") : label;
graphics.setColor(java.awt.Color.white);
graphics.setFont(new Font('Courier',Font.BOLD, 11));
graphics.drawString(label, 43 - label.length() * 4, 17.0);
