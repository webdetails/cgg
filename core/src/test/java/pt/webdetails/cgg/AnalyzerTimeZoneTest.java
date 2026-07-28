/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package pt.webdetails.cgg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import pt.webdetails.cpf.utils.CharsetHelper;

public class AnalyzerTimeZoneTest {
  private static final Pattern DATE_TICK_PATTERN = Pattern.compile( "((?:0[1-9]|1[0-2])/\\d{4})" );
  private static final String[] CHART_TYPES = { "Line", "StackedLine", "StackedArea", "Dot" };

  @Before
  public void setUp() {
    CggBoot.init();
  }

  @Test
  public void preservesJvmTimezoneAndKeepsExpectedMonthlyAxis() throws Exception {
    final TimeZone originalTimeZone = TimeZone.getDefault();
    final TimeZone serverTimeZone = TimeZone.getTimeZone( "GMT+02:00" );
    final TimeZone utcTimeZone = TimeZone.getTimeZone( "UTC" );

    try {
      for ( final String chartType : CHART_TYPES ) {
        TimeZone.setDefault( serverTimeZone );

        final byte[] serverZoneSvg = executeSvg( "timezone/analyzer-timezone-svg-test.js", chartType );

        assertEquals( serverTimeZone.getID(), TimeZone.getDefault().getID() );

        TimeZone.setDefault( utcTimeZone );
        final byte[] utcSvg = executeSvg( "timezone/analyzer-timezone-svg-test.js", chartType );

        final List<String> serverZoneTicks = extractMonthlyTicks( serverZoneSvg );
        final List<String> utcTicks = extractMonthlyTicks( utcSvg );

        assertFalse( chartType, serverZoneTicks.isEmpty() );
        assertEquals( chartType, "07/2005", serverZoneTicks.get( serverZoneTicks.size() - 1 ) );
        assertEquals( chartType, utcTicks, serverZoneTicks );
      }
    } finally {
      TimeZone.setDefault( originalTimeZone );
    }
  }

  private List<String> extractMonthlyTicks( final byte[] svg ) throws Exception {
    final String output = new String( svg, CharsetHelper.getEncoding() );
    final Matcher matcher = DATE_TICK_PATTERN.matcher( output );
    final List<String> ticks = new ArrayList<>();

    while ( matcher.find() ) {
      ticks.add( matcher.group( 1 ) );
    }

    return ticks;
  }

  private byte[] executeSvg( final String resourcePath, final String chartType ) throws Exception {
    final URL resource = AnalyzerTimeZoneTest.class.getClassLoader().getResource( resourcePath );
    if ( resource == null ) {
      throw new IllegalArgumentException( resourcePath );
    }

    final File file = new File( resource.toURI() );
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final URL scriptContext = file.getParentFile().toURI().toURL();
    final DefaultCgg cgg = new DefaultCgg( out, scriptContext );
    final HashMap<String, Object> parameters = new HashMap<>();
    parameters.put( "cccVersion", "2.0-analyzer" );
    parameters.put( "chartType", chartType );

    cgg.draw( new DrawParameters(
      file.getName(),
      "SVG",
      "svg",
      800,
      600,
      false,
      Locale.US,
      parameters ) );

    return out.toByteArray();
  }
}