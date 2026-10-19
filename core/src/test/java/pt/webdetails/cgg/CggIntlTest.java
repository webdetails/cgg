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

import static org.junit.Assert.assertTrue;

/**
 * Checks that the Intl polyfill, installed by {@code cgg-env.js}, loads in CGG's Rhino environment.
 * <p>
 * A failure to load it is only logged, by {@code lib}, leaving {@code Intl} undefined.
 */
public class CggIntlTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Before
  public void setUp() {
    CggBoot.init();
  }

  @Test
  public void intlPolyfillIsInstalled() throws Exception {
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
        + "var result = typeof Intl !== 'object' ? 'no-intl' : [\n"
        + "  typeof Intl.DateTimeFormat.__setDefaultTimeZone,\n"
        + "  new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD'}).format(1234.5),\n"
        + "  new Intl.DateTimeFormat('en-US', {timeZone: 'UTC', year: 'numeric', month: 'long', day: 'numeric'})\n"
        + "    .format(Date.UTC(2026, 6, 1)),\n"
        + "  new Intl.PluralRules('en-US').select(1)\n"
        + "].join('|');\n"
        + "_document.getDocumentElement().setAttribute('data-intl', result);\n" )
        .getBytes( StandardCharsets.UTF_8 ) );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    new DefaultCgg( out, dir.toURI().toURL() ).draw( new DrawParameters(
      script.getName(), "SVG", "svg", 10, 10, false, Locale.US, new HashMap<>() ) );

    final String svg = new String( out.toByteArray(), StandardCharsets.UTF_8 );
    assertTrue( svg, svg.contains( "data-intl=\"function|$1,234.50|July 1, 2026|one\"" ) );
  }
}
