/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package pt.webdetails.cgg;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Checks that the Intl polyfill, installed by {@code cgg-env.js}, loads in CGG's Rhino environment.
 * <p>
 * A failure to load it is only logged, by {@code lib}, leaving {@code Intl} undefined.
 */
public class CggIntlTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  private TimeZone defaultTimeZone;

  @Before
  public void setUp() {
    CggBoot.init();
    defaultTimeZone = TimeZone.getDefault();
  }

  @After
  public void tearDown() {
    TimeZone.setDefault( defaultTimeZone );
  }

  @Test
  public void intlPolyfillIsInstalled() throws Exception {
    final String svg = drawIntlResult( Locale.US, "[\n"
      + "  typeof Intl.DateTimeFormat.__setDefaultTimeZone,\n"
      + "  new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD'}).format(1234.5),\n"
      + "  new Intl.DateTimeFormat('en-US', {timeZone: 'UTC', year: 'numeric', month: 'long', day: 'numeric'})\n"
      + "    .format(Date.UTC(2026, 6, 1)),\n"
      + "  new Intl.PluralRules('en-US').select(1)\n"
      + "].join('|')" );

    assertTrue( svg, svg.contains( "data-intl=\"function|$1,234.50|July 1, 2026|one\"" ) );
  }

  /**
   * Local dates, as built for chart data, format to the same local date, whatever the JVM default time zone.
   * <p>
   * In {@code America/Sao_Paulo}, daylight saving time started at 2018-11-04 00:00, so that local midnight does not
   * exist. Dates across daylight saving transitions shifted by one day when formatted with a fixed UTC offset.
   */
  @Test
  public void localDatesFormatToTheSameDateWhateverTheJvmTimeZone() throws Exception {
    TimeZone.setDefault( TimeZone.getTimeZone( "America/Sao_Paulo" ) );

    final String svg = drawIntlResult( Locale.US, "[\n"
      + "  new Date(2018, 10, 4).getTimezoneOffset(),\n"
      + "  new Intl.DateTimeFormat().resolvedOptions().timeZone,\n"
      + "  [new Date(2018, 1, 17), new Date(2018, 1, 18), new Date(2018, 10, 3), new Date(2018, 10, 4),\n"
      + "    new Date(2026, 2, 29), new Date(2026, 9, 25)].map(function(d) {\n"
      + "    return new Intl.DateTimeFormat('en-US', {year: 'numeric', month: 'numeric', day: 'numeric'}).format(d);\n"
      + "  }).join(',')\n"
      + "].join('|')" );

    assertTrue( svg, svg.contains(
      "data-intl=\"0|UTC|2/17/2018,2/18/2018,11/3/2018,11/4/2018,3/29/2026,10/25/2026\"" ) );

    // The JVM default time zone is not changed.
    assertEquals( "America/Sao_Paulo", TimeZone.getDefault().getID() );
  }

  /**
   * Dates around daylight saving transitions, and around a skipped calendar day, format to the same date and month
   * in every JVM default time zone.
   */
  @Test
  public void datesAroundTransitionsFormatTheSameInAnyJvmTimeZone() throws Exception {
    final String[] zones = {
      "America/Sao_Paulo", "America/New_York", "Europe/Lisbon", "Asia/Kolkata", "Pacific/Auckland",
      "Pacific/Kiritimati", "Pacific/Pago_Pago", "Pacific/Apia", "UTC"
    };

    // {year, month (1-based), day}
    final int[][] dates = {
      {2018, 2, 18}, {2018, 11, 3}, {2018, 11, 4},
      {2026, 3, 8}, {2026, 3, 29}, {2026, 4, 5}, {2026, 9, 27}, {2026, 10, 25}, {2026, 11, 1},
      {2011, 12, 29}, {2011, 12, 30}, {2011, 12, 31},
      {2026, 1, 1}, {2026, 12, 31}
    };

    final StringBuilder dateArgs = new StringBuilder();
    final StringBuilder expected = new StringBuilder();
    for ( final int[] d : dates ) {
      if ( dateArgs.length() > 0 ) {
        dateArgs.append( ',' );
        expected.append( ',' );
      }
      dateArgs.append( "[" ).append( d[ 0 ] ).append( "," ).append( d[ 1 ] - 1 ).append( "," ).append( d[ 2 ] )
        .append( "]" );
      expected.append( d[ 1 ] ).append( '/' ).append( d[ 2 ] ).append( '/' ).append( d[ 0 ] )
        .append( ';' ).append( d[ 1 ] ).append( '/' ).append( d[ 0 ] );
    }

    final String expression = "[" + dateArgs + "].map(function(a) {\n"
      + "  var d = new Date(a[0], a[1], a[2]);\n"
      + "  return new Intl.DateTimeFormat('en-US', {year: 'numeric', month: 'numeric', day: 'numeric'}).format(d)\n"
      + "    + ';' + new Intl.DateTimeFormat('en-US', {year: 'numeric', month: 'numeric'}).format(d);\n"
      + "}).join(',')";

    for ( final String zone : zones ) {
      TimeZone.setDefault( TimeZone.getTimeZone( zone ) );

      final String svg = drawIntlResult( Locale.US, expression );

      assertTrue( zone + ": " + svg, svg.contains( "data-intl=\"" + expected + "\"" ) );
    }
  }

  @Test
  public void datesFormatInTheUserLocale() throws Exception {
    final String expression = "[\n"
      + "  new Intl.DateTimeFormat(__locale, {year: 'numeric', month: 'numeric'}).format(new Date(2004, 0, 1)),\n"
      + "  new Intl.DateTimeFormat(__locale, {month: '2-digit', day: '2-digit'}).format(new Date(2021, 2, 7))\n"
      + "].join('|')";

    assertTrue( drawIntlResult( Locale.US, expression ).contains( "data-intl=\"1/2004|03/07\"" ) );
    assertTrue( drawIntlResult( Locale.JAPAN, expression ).contains( "data-intl=\"2004/1|03/07\"" ) );
    assertTrue( drawIntlResult( new Locale( "pt", "PT" ), expression ).contains( "data-intl=\"01/2004|07/03\"" ) );
  }

  private String drawIntlResult( final Locale locale, final String resultExpression ) throws Exception {
    final File dir = tmp.newFolder();
    Files.write( new File( dir, "blank.svg" ).toPath(),
      ( "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" width=\"10px\" height=\"10px\"></svg>" )
        .getBytes( StandardCharsets.UTF_8 ) );

    final File script = new File( dir, "intl-svg-test.js" );
    Files.write( script.toPath(),
      ( "lib('cgg-env.js');\n"
        + "cgg.init();\n"
        + "cgg.utils.initDocument('blank.svg');\n"
        + "var result = typeof Intl !== 'object' ? 'no-intl' : " + resultExpression + ";\n"
        + "_document.getDocumentElement().setAttribute('data-intl', result);\n" )
        .getBytes( StandardCharsets.UTF_8 ) );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    new DefaultCgg( out, dir.toURI().toURL() ).draw( new DrawParameters(
      script.getName(), "SVG", "svg", 10, 10, false, locale, new HashMap<>() ) );

    return new String( out.toByteArray(), StandardCharsets.UTF_8 );
  }
}
