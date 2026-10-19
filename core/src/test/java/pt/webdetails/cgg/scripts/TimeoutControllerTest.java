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

import static org.junit.Assert.assertEquals;

/**
 * Guards the synchronous "event loop" that CGG scripts rely on: calling {@code _runTimeouts()} (exposed to
 * scripts as {@code cgg.run()}) must, before returning, run all pending native Promise reactions and all
 * scheduled {@code setTimeout} callbacks. Chart rendering code (e.g. CDF's BaseCccComponent) depends on this.
 */
public class TimeoutControllerTest {

  private Context cx;
  private BaseScope scope;

  @Before
  public void setUp() {
    cx = new ContextFactory().enterContext();
    cx.setGeneratingDebug( false );
    cx.setOptimizationLevel( -1 );
    cx.setLanguageVersion( Context.VERSION_ES6 );

    scope = new BaseScope();
    scope.init( cx );
  }

  @After
  public void tearDown() {
    Context.exit();
  }

  private String eval( final String source ) {
    return Context.toString( cx.evaluateString( scope, source, "test", 1, null ) );
  }

  @Test
  public void runDrainsPromiseReactionsSynchronously() {
    assertEquals( "done", eval(
      "var r = 'pending';"
        + "Promise.resolve('done').then(function(v) { r = v; });"
        + "_runTimeouts();"
        + "r" ) );
  }

  @Test
  public void runExecutesTimeoutsInDelayThenCreationOrder() {
    assertEquals( "a,b,c,d", eval(
      "var log = [];"
        + "setTimeout(function() { log.push('c'); }, 20);"
        + "setTimeout(function() { log.push('a'); }, 10);"
        + "setTimeout(function() { log.push('d'); }, 20);"
        + "setTimeout(function() { log.push('b'); }, 10);"
        + "_runTimeouts();"
        + "log.join()" ) );
  }

  @Test
  public void microTasksRunBeforeTimeouts() {
    assertEquals( "sync,micro,timeout,micro2", eval(
      "var log = [];"
        + "setTimeout(function() {"
        + "  log.push('timeout');"
        + "  Promise.resolve().then(function() { log.push('micro2'); });"
        + "});"
        + "Promise.resolve().then(function() { log.push('micro'); });"
        + "log.push('sync');"
        + "_runTimeouts();"
        + "log.join()" ) );
  }

  @Test
  public void runStopsWhenDone() {
    // The condition is checked after the micro-tasks of each timeout, so 'c' runs, but 'd' does not.
    assertEquals( "a,b,c|a,b,c,d", eval(
      "var log = [];"
        + "setTimeout(function() { log.push('a'); }, 0);"
        + "setTimeout(function() { log.push('b'); Promise.resolve().then(function() { log.push('c'); }); }, 1);"
        + "setTimeout(function() { log.push('d'); }, 2);"
        + "_runTimeouts(function() { return log.length >= 2; });"
        + "var r = log.join();"
        + "_runTimeouts();"
        + "r + '|' + log.join()" ) );
  }

  @Test
  public void nestedRunPreservesOuterNestingLevel() {
    // Nesting level > 5 clamps both delays to 4ms, preserving creation order.
    assertEquals( "a,b", eval(
      "var log = [];"
        + "function nest(n) {"
        + "  if(n > 0) { setTimeout(function() { nest(n - 1); }, 0); return; }"
        + "  setTimeout(function() {}, 0);"
        + "  _runTimeouts();"
        + "  setTimeout(function() { log.push('a'); }, 3);"
        + "  setTimeout(function() { log.push('b'); }, 1);"
        + "}"
        + "nest(7);"
        + "_runTimeouts();"
        + "log.join()" ) );
  }

  @Test
  public void timeoutReceivesExtraArguments() {
    assertEquals( "1-x", eval(
      "var r;"
        + "setTimeout(function(a, b) { r = a + '-' + b; }, 0, 1, 'x');"
        + "_runTimeouts();"
        + "r" ) );
  }

  @Test
  public void clearTimeoutCancelsTimeout() {
    assertEquals( "b", eval(
      "var log = [];"
        + "var id = setTimeout(function() { log.push('a'); }, 0);"
        + "setTimeout(function() { log.push('b'); }, 0);"
        + "clearTimeout(id);"
        + "_runTimeouts();"
        + "log.join()" ) );
  }

  @Test
  public void stringTimeoutIsEvaluatedInGlobalScope() {
    assertEquals( "42", eval(
      "var r = 0;"
        + "setTimeout('r = 42;', 0);"
        + "_runTimeouts();"
        + "String(r)" ) );
  }

  @Test
  public void stringTimeoutHasGlobalThis() {
    assertEquals( "42", eval(
      "var r = 0;"
        + "setTimeout('this.r = 42;', 0);"
        + "_runTimeouts();"
        + "String(r)" ) );
  }

  @Test
  public void errorInTimeoutDoesNotStopOtherTimeouts() {
    assertEquals( "b", eval(
      "var log = [];"
        + "setTimeout(function() { throw new Error('boom'); }, 0);"
        + "setTimeout(function() { log.push('b'); }, 1);"
        + "_runTimeouts();"
        + "log.join()" ) );
  }

  @Test
  public void javaObjectArgumentsArePassedAsIs() {
    assertEquals( "java.util.ArrayList", eval(
      "var r;"
        + "setTimeout(function(v) { r = String(v.getClass().getName()); }, 0, new java.util.ArrayList());"
        + "_runTimeouts();"
        + "r" ) );
  }

  @Test
  public void nonFunctionHandlerIsConvertedToStringAtCallTime() {
    assertEquals( "1", eval(
      "var x = 0, code = 'x = 1';"
        + "setTimeout({ toString: function() { return code; } });"
        + "code = 'x = 2';"
        + "_runTimeouts();"
        + "String(x)" ) );
  }

  @Test
  public void nullAndUndefinedHandlersAreEvaluatedAsCode() {
    assertEquals( "ok", eval(
      "setTimeout(null);"
        + "setTimeout(undefined);"
        + "_runTimeouts();"
        + "'ok'" ) );
  }

  @Test
  public void missingHandlerThrowsTypeError() {
    assertEquals( "TypeError", eval( "try { setTimeout(); 'none' } catch(e) { e.name }" ) );
  }

  @Test
  public void symbolTimeoutThrowsTypeError() {
    assertEquals( "TypeError", eval( "try { setTimeout(function() {}, Symbol()); 'none' } catch(e) { e.name }" ) );
  }

  @Test
  public void symbolTimeoutIdThrowsTypeError() {
    assertEquals( "TypeError", eval( "try { clearTimeout(Symbol()); 'none' } catch(e) { e.name }" ) );
  }

  @Test
  public void invalidAndNegativeTimeoutsAreTreatedAsZero() {
    assertEquals( "a,b,c,d", eval(
      "var log = [];"
        + "setTimeout(function() { log.push('d'); }, 1);"
        + "setTimeout(function() { log.push('a'); }, -5);"
        + "setTimeout(function() { log.push('b'); }, 'foo');"
        + "setTimeout(function() { log.push('c'); }, null);"
        + "_runTimeouts();"
        + "log.join()" ) );
  }

  @Test
  public void queueMicrotaskInterleavesWithPromisesBeforeTimeouts() {
    assertEquals( "sync,m1,p1,m2,timeout", eval(
      "var log = [];"
        + "setTimeout(function() { log.push('timeout'); }, 0);"
        + "queueMicrotask(function() { log.push('m1'); queueMicrotask(function() { log.push('m2'); }); });"
        + "Promise.resolve().then(function() { log.push('p1'); });"
        + "log.push('sync');"
        + "_runTimeouts();"
        + "log.join()" ) );
  }

  @Test
  public void queueMicrotaskCallsWithGlobalThis() {
    // Spec passes an undefined `this`, which non-strict functions see as the global object.
    assertEquals( "true", eval(
      "var t;"
        + "queueMicrotask(function() { t = this === globalThis; });"
        + "_runTimeouts();"
        + "String(t)" ) );
  }

  @Test
  public void queueMicrotaskRequiresFunction() {
    assertEquals( "TypeError,TypeError", eval(
      "var r = [];"
        + "try { queueMicrotask(); } catch(e) { r.push(e.name); }"
        + "try { queueMicrotask('x = 1'); } catch(e) { r.push(e.name); }"
        + "r.join()" ) );
  }

  @Test
  public void errorInMicrotaskDoesNotStopOtherMicrotasks() {
    assertEquals( "b,t", eval(
      "var log = [];"
        + "queueMicrotask(function() { throw new Error('boom'); });"
        + "queueMicrotask(function() { log.push('b'); });"
        + "setTimeout(function() { log.push('t'); }, 0);"
        + "_runTimeouts();"
        + "log.join()" ) );
  }

  @Test
  public void setTimeoutWorksWhenCalledWithOtherThis() {
    assertEquals( "ok", eval(
      "var r;"
        + "var win = { setTimeout: setTimeout };"
        + "win.setTimeout(function() { r = 'ok'; }, 0);"
        + "_runTimeouts.call({});"
        + "r" ) );
  }
}
