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


package pt.webdetails.cgg.scripts;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.Scriptable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Guards the JavaScript number-to-string surface that cgg relies on to render chart axis labels,
 * tick values and tooltips.
 *
 * <p>Rhino's entire number formatting stack ({@code Number.prototype.toFixed}, {@code toExponential},
 * {@code toPrecision} and {@code Number.prototype.toString}) was reimplemented upstream. These tests
 * pin the observable behaviour so a change in that stack cannot silently alter rendered output.</p>
 */
public class RhinoNumberFormattingTest {

  /**
   * Generous upper bound for formatting a single value. The formatting routine is expected to be
   * sub-millisecond; anything approaching this budget indicates an algorithmic blow-up rather than
   * a slow machine.
   */
  private static final long FORMAT_BUDGET_MILLIS = 10000L;

  private Context cx;
  private Scriptable scope;

  @Before
  public void setUp() {
    // Mirrors the context configuration cgg uses in production -- see AbstractScriptFactory.
    ContextFactory contextFactory = new ContextFactory();
    cx = contextFactory.enterContext();
    cx.setGeneratingDebug( false );
    cx.setOptimizationLevel( -1 );
    cx.setLanguageVersion( Context.VERSION_ES6 );
    scope = cx.initStandardObjects();
  }

  @After
  public void tearDown() {
    Context.exit();
  }

  private String eval( final String source ) {
    return Context.toString( cx.evaluateString( scope, source, "test", 1, null ) );
  }

  @Test
  public void toFixedFormatsTypicalAxisValues() {
    assertEquals( "1.50", eval( "(1.5).toFixed(2)" ) );
    assertEquals( "0", eval( "(0.4).toFixed(0)" ) );
    assertEquals( "1", eval( "(0.5).toFixed(0)" ) );
    assertEquals( "123.46", eval( "(123.456).toFixed(2)" ) );
    assertEquals( "-123.46", eval( "(-123.456).toFixed(2)" ) );
    assertEquals( "0.30000000000000004", eval( "(0.1 + 0.2).toFixed(17)" ) );
    assertEquals( "100.00", eval( "(100).toFixed(2)" ) );
  }

  @Test
  public void toFixedFallsBackToToStringAtTheOneE21Boundary() {
    // At or above 1e21 toFixed is specified to defer to ToString.
    assertEquals( "1e+21", eval( "(1e21).toFixed(2)" ) );
  }

  @Test
  public void numberToStringMatchesTheSpecifiedShortestRoundTrip() {
    assertEquals( "0", eval( "(0).toString()" ) );
    assertEquals( "0.000001", eval( "(0.000001).toString()" ) );
    assertEquals( "1e-7", eval( "(0.0000001).toString()" ) );
    assertEquals( "1e+21", eval( "(1e21).toString()" ) );
    assertEquals( "3.141592653589793", eval( "Math.PI.toString()" ) );
    assertEquals( "1.7976931348623157e+308", eval( "Number.MAX_VALUE.toString()" ) );
  }

  /**
   * At the very bottom of the denormal range Rhino does not emit the shortest representation that
   * V8 does -- it renders Number.MIN_VALUE as "4.9e-324" rather than "5e-324". What must hold is
   * that the rendered text still parses back to the identical double, which is the property chart
   * output actually depends on.
   */
  @Test
  public void denormalBoundaryValuesSurviveAStringRoundTrip() {
    assertEquals( "true", eval( "parseFloat(Number.MIN_VALUE.toString()) === Number.MIN_VALUE" ) );
    assertEquals( "true", eval( "parseFloat((1e-323).toString()) === 1e-323" ) );
    assertEquals( "true", eval( "parseFloat((1.5e-323).toString()) === 1.5e-323" ) );
    assertEquals( "true", eval( "parseFloat((5e-324).toString()) === 5e-324" ) );
  }

  @Test
  public void toPrecisionAndToExponentialAreStable() {
    assertEquals( "123.46", eval( "(123.456).toPrecision(5)" ) );
    assertEquals( "1.2346e+2", eval( "(123.456).toExponential(4)" ) );
    assertEquals( "3.14159e+0", eval( "Math.PI.toExponential(5)" ) );
    assertEquals( "0.000012300", eval( "(0.0000123).toPrecision(5)" ) );
  }

  @Test
  public void nonFiniteValuesFormatAsSpecialTokens() {
    assertEquals( "NaN", eval( "(NaN).toFixed(2)" ) );
    assertEquals( "Infinity", eval( "(Infinity).toFixed(2)" ) );
    assertEquals( "-Infinity", eval( "(-Infinity).toFixed(2)" ) );
    assertEquals( "NaN", eval( "(NaN).toExponential(2)" ) );
    assertEquals( "Infinity", eval( "(Infinity).toPrecision(3)" ) );
  }

  @Test
  public void negativePrecisionIsRejectedAsARangeError() {
    // cgg always runs at VERSION_ES6, where Rhino's legacy negative-precision extension is disabled.
    // It must surface as a JavaScript RangeError, never as a leaked Java exception.
    assertRangeError( "(1.5).toFixed(-5)" );
    assertRangeError( "(123.456).toFixed(-2)" );
  }

  @Test
  public void outOfRangePrecisionIsRejectedAsARangeError() {
    assertRangeError( "(1.5).toFixed(101)" );
    assertRangeError( "(1.5).toExponential(101)" );
    assertRangeError( "(1.5).toPrecision(0)" );
    assertRangeError( "(1.5).toPrecision(101)" );

    // Rhino accepts precisions above the ECMAScript maximum of 21, up to 100. Pinned deliberately:
    // this is an engine extension, and a change to it would alter rendered label text.
    assertEquals( "1.500000000000000000000", eval( "(1.5).toPrecision(22)" ) );
  }

  private void assertRangeError( final String source ) {
    try {
      final String result = eval( source );
      fail( "Expected a RangeError from '" + source + "' but it returned '" + result + "'" );
    } catch ( EcmaError e ) {
      assertEquals( "'" + source + "' must raise a RangeError", "RangeError", e.getName() );
    }
  }

  /**
   * CVE-2025-66453: formatting an attacker-controlled float could drive Rhino's dtoa routine into
   * raising 5 to an enormous power, burning CPU without bound. Chart scripts format values that
   * originate in query results, so the value reaching toFixed is not necessarily trusted.
   */
  @Test
  public void formattingAdversarialFloatsCompletesInBoundedTime() {
    final String[] adversarialValues = {
      "Number.MIN_VALUE",
      "5e-324",
      "4.9e-324",
      "1e-323",
      "1e-310",
      "2.2250738585072014e-308",
      "1e-300",
      "1e-100",
      "1e-7",
      "0.1",
      "1e20",
      "1.7976931348623157e+308"
    };

    for ( final String value : adversarialValues ) {
      for ( final int precision : new int[] { 0, 1, 20, 100 } ) {
        final String source = "(" + value + ").toFixed(" + precision + ")";
        final long startedAt = System.nanoTime();
        eval( source );
        final long elapsedMillis = ( System.nanoTime() - startedAt ) / 1000000L;
        assertTrue(
          "Formatting '" + source + "' took " + elapsedMillis + "ms, over the "
            + FORMAT_BUDGET_MILLIS + "ms budget",
          elapsedMillis < FORMAT_BUDGET_MILLIS );
      }
    }
  }
}
