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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mozilla.javascript.Context;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * End-to-end check that {@code cgg.run()} drains native Promise reactions and {@code setTimeout} callbacks
 * before the chart output is produced, as relied upon by CDF's BaseCccComponent.
 */
public class CggEventLoopTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Before
  public void setUp() {
    CggBoot.init();
  }

  @Test
  public void cggRunDrainsPromisesAndTimeoutsBeforeOutput() throws Exception {
    final File dir = createChartFolder();

    final File script = writeScript( dir, "promise-svg-test.js",
      "var log = [];\n"
        + "Promise.resolve('promise').then(function(v) {\n"
        + "  log.push(v);\n"
        + "  setTimeout(function() { log.push('timeout'); }, 10);\n"
        + "});\n"
        + "cgg.run();\n"
        + "_document.getDocumentElement().setAttribute('data-log', log.join());\n" );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    draw( new DefaultCgg( out, dir.toURI().toURL() ), script );

    final String svg = new String( out.toByteArray(), StandardCharsets.UTF_8 );
    assertTrue( svg, svg.contains( "data-log=\"promise,timeout\"" ) );
  }

  @Test
  public void drawDrainsPromisesAndTimeoutsAfterScriptReturns() throws Exception {
    final File dir = createChartFolder();

    final File script = writeScript( dir, "auto-run-svg-test.js",
      "var root = _document.getDocumentElement();\n"
        + "Promise.resolve().then(function() { root.setAttribute('data-promise', 'done'); });\n"
        + "setTimeout(function() { root.setAttribute('data-timeout', 'done'); }, 10);\n" );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    draw( new DefaultCgg( out, dir.toURI().toURL() ), script );

    final String svg = new String( out.toByteArray(), StandardCharsets.UTF_8 );
    assertTrue( svg, svg.contains( "data-promise=\"done\"" ) );
    assertTrue( svg, svg.contains( "data-timeout=\"done\"" ) );
  }

  @Test
  public void timeoutsOfAbortedScriptDoNotRunInLaterDraw() throws Exception {
    final File dir = createChartFolder();

    final File failingScript = writeScript( dir, "failing-svg-test.js",
      "setTimeout(function() { globalThis.leaked = true; }, 0);\n"
        + "Promise.resolve().then(function() { globalThis.leakedMicrotask = true; });\n"
        + "throw new Error('abort');\n" );

    final File script = writeScript( dir, "later-svg-test.js",
      "cgg.run();\n"
        + "_document.getDocumentElement().setAttribute('data-leaked', "
        + "String(globalThis.leaked === true) + ',' + String(globalThis.leakedMicrotask === true));\n" );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    final DefaultCgg cgg = new DefaultCgg( out, dir.toURI().toURL() );

    try {
      draw( cgg, failingScript );
      fail( "Expected the script to fail." );
    } catch ( ScriptExecuteException expected ) {
      // Expected.
    }

    out.reset();
    draw( cgg, script );

    final String svg = new String( out.toByteArray(), StandardCharsets.UTF_8 );
    assertTrue( svg, svg.contains( "data-leaked=\"false,false\"" ) );
  }

  @Test
  public void syncRequireDoesNotRunPendingMicrotasks() throws Exception {
    final File dir = createChartFolder();

    Files.write( new File( dir, "amd-test.js" ).toPath(),
      "define(function() { return { loaded: true }; });".getBytes( StandardCharsets.UTF_8 ) );

    final File script = writeScript( dir, "sync-require-svg-test.js",
      "var ready = false;\n"
        + "var observed;\n"
        + "Promise.resolve().then(function() { observed = ready; });\n"
        + "var amd = require('res:amd-test.js');\n"
        + "ready = true;\n"
        + "cgg.run();\n"
        + "_document.getDocumentElement().setAttribute('data-observed', String(observed) + ',' + amd.loaded);\n" );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    draw( new DefaultCgg( out, dir.toURI().toURL() ), script );

    final String svg = new String( out.toByteArray(), StandardCharsets.UTF_8 );
    assertTrue( svg, svg.contains( "data-observed=\"true,true\"" ) );
  }

  @Test
  public void syncRequireWaitsForDeferredPluginCompletion() throws Exception {
    final File dir = createChartFolder();

    final File script = writeScript( dir, "deferred-plugin-svg-test.js",
      "define('timeout-plugin', { load: function(id, req, onload) {\n"
        + "  setTimeout(function() { onload('timeout:' + id); }, 10);\n"
        + "} });\n"
        + "define('promise-plugin', { load: function(id, req, onload) {\n"
        + "  Promise.resolve().then(function() { onload('promise:' + id); });\n"
        + "} });\n"
        + "var a = require('timeout-plugin!a');\n"
        + "var b = require('promise-plugin!b');\n"
        + "_document.getDocumentElement().setAttribute('data-loaded', a + ',' + b);\n" );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    draw( new DefaultCgg( out, dir.toURI().toURL() ), script );

    final String svg = new String( out.toByteArray(), StandardCharsets.UTF_8 );
    assertTrue( svg, svg.contains( "data-loaded=\"timeout:a,promise:b\"" ) );
  }

  @Test
  public void drawFailsWhenContextAlreadyEntered() throws Exception {
    final File dir = createChartFolder();
    final File script = writeScript( dir, "nested-svg-test.js", "" );
    final DefaultCgg cgg = new DefaultCgg( new ByteArrayOutputStream(), dir.toURI().toURL() );

    final Context outer = Context.enter();
    try {
      draw( cgg, script );
      fail( "Expected drawing within an entered context to fail." );
    } catch ( IllegalStateException expected ) {
      // The caller's context must remain entered.
      assertSame( outer, Context.getCurrentContext() );
    } finally {
      Context.exit();
    }

    assertNull( Context.getCurrentContext() );
  }

  private File createChartFolder() throws IOException {
    final File dir = tmp.newFolder();
    Files.write( new File( dir, "blank.svg" ).toPath(),
      ( "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
        + "<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" width=\"10px\" height=\"10px\"></svg>" )
        .getBytes( StandardCharsets.UTF_8 ) );
    return dir;
  }

  private static File writeScript( final File dir, final String name, final String body ) throws IOException {
    final File script = new File( dir, name );
    Files.write( script.toPath(),
      ( "lib('cgg-env.js');\n"
        + "cgg.init();\n"
        + "cgg.utils.initDocument('blank.svg');\n"
        + body )
        .getBytes( StandardCharsets.UTF_8 ) );
    return script;
  }

  private static void draw( final DefaultCgg cgg, final File script ) throws Exception {
    cgg.draw( new DrawParameters(
      script.getName(), "SVG", "svg", 10, 10, false, Locale.US, new HashMap<>() ) );
  }
}
