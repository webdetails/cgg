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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.LambdaFunction;
import org.mozilla.javascript.ScriptRuntime;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.function.BooleanSupplier;

/**
 * Implementation of the global {@code setTimeout}, {@code clearTimeout} and {@code queueMicrotask}.
 * <p>
 * The time of timeouts is simulated, and no real waiting for elapsed time occurs.
 * However, the relative order of timeouts is respected according to the specified time delay.
 * <p>
 * Micro-tasks, such as those resulting from promises, have priority over scheduled timeouts.
 * <p>
 * To schedule a timeout, call {@link #setTimeout(Object[])}.
 * To remove a previously scheduled timeout, call {@link #clearTimeout(Object[])}.
 * To queue a micro-task, call {@link #queueMicrotask(Context, Object[])}.
 * To process any queued micro-tasks and scheduled timeouts, until no more exist, call {@link #run(Context)}.
 * <p>
 * The implementation follows the
 * <a href="https://html.spec.whatwg.org/multipage/webappapis.html#timers"> HTML specification</a>.
 */
public class TimeoutController {

  private static final Log logger = LogFactory.getLog( TimeoutController.class );

  private static class TimeoutInfo implements Comparable<TimeoutInfo> {
    public final int id;
    public final int nestingLevel;
    public final long atTime;
    public final Function callback;
    public final Object[] args;

    public TimeoutInfo( int id, int nestingLevel, long atTime, Function callback, Object[] args ) {
      this.id = id;
      this.nestingLevel = nestingLevel;
      this.atTime = atTime;
      this.callback = callback;
      this.args = args;
    }

    // Ids are unique, so this is consistent with equals.
    @Override
    public int compareTo( TimeoutInfo other ) {
      int compare = Long.compare( this.atTime, other.atTime );
      if ( compare != 0 ) {
        return compare;
      }

      return Integer.compare( this.id, other.id );
    }

    @Override
    public boolean equals( Object other ) {
      return other instanceof TimeoutInfo && ( (TimeoutInfo) other ).id == this.id;
    }

    @Override
    public int hashCode() {
      return Integer.hashCode( id );
    }
  }

  private int nextId;
  private int nestingLevel;

  // Time is simulated.
  private long currentTime;

  private final HashMap<Integer, TimeoutInfo> timeoutsById;
  private final PriorityQueue<TimeoutInfo> timeoutsQueue;
  private final BaseScope scope;

  public TimeoutController( BaseScope scope ) {
    this.nextId = 1;
    this.nestingLevel = 0;
    this.timeoutsById = new HashMap<>();
    this.timeoutsQueue = new PriorityQueue<>();
    this.scope = Objects.requireNonNull( scope );
  }

  public Object setTimeout( Object[] args ) {
    if ( args == null || args.length == 0 ) {
      throw ScriptRuntime.typeError( "First argument is required" );
    }

    // Arguments are converted in order, as per WebIDL: (Function or DOMString) handler, long timeout.
    Function callback = readSetTimeoutCallback( args[ 0 ] );

    int timeout = args.length > 1 ? ScriptRuntime.toInt32( args[ 1 ] ) : 0;
    if ( timeout < 0 ) {
      timeout = 0;
    }

    if ( nestingLevel > 5 && timeout < 4 ) {
      timeout = 4;
    }

    long atTime = currentTime + timeout;

    Object[] funArgs = args.length > 2
      ? Arrays.copyOfRange( args, 2, args.length )
      : ScriptRuntime.emptyArgs;

    return addTimeoutInfo( atTime, nestingLevel + 1, callback, funArgs ).id;
  }

  public void clearTimeout( Object[] args ) {
    // WebIDL: optional long id = 0.
    int timeoutId = args != null && args.length > 0 ? ScriptRuntime.toInt32( args[ 0 ] ) : 0;
    if ( timeoutId <= 0 ) {
      return;
    }

    TimeoutInfo timeoutInfo = timeoutsById.get( timeoutId );
    if ( timeoutInfo == null ) {
      return;
    }

    removeTimeoutInfo( timeoutInfo );
  }

  /**
   * Implements the global {@code queueMicrotask}, as per the
   * <a href="https://html.spec.whatwg.org/multipage/timers-and-user-prompts.html#microtask-queuing">
   * HTML specification</a>.
   */
  public void queueMicrotask( Context cx, Object[] args ) {
    if ( args == null || args.length == 0 ) {
      throw ScriptRuntime.typeError( "First argument is required" );
    }

    if ( !( args[ 0 ] instanceof Function ) ) {
      throw ScriptRuntime.typeError( "First argument must be a function" );
    }

    final Function callback = (Function) args[ 0 ];

    cx.enqueueMicrotask( () -> {
      try {
        // Spec says `this` is undefined, which non-strict functions see as the global object.
        // Rhino does not support an undefined `this` in strict functions, and passing
        // Undefined.SCRIPTABLE_UNDEFINED (or null) would expose an odd non-global object.
        callback.call( cx, scope, scope, ScriptRuntime.emptyArgs );
      } catch ( RuntimeException ex ) {
        // Context#processMicrotasks does not catch, so an error would prevent other micro-tasks from running.
        logger.error( "Error in queueMicrotask callback: " + ex.getMessage(), ex );
      }
    } );
  }

  /**
   * Discards all scheduled timeouts and resets the simulated time.
   */
  public void clear() {
    timeoutsById.clear();
    timeoutsQueue.clear();
    nestingLevel = 0;
    currentTime = 0;
  }

  /**
   * Processes all micro-tasks and any scheduled timeouts to completion.
   * <p>
   * Does not wait for the scheduled time and instead immediately executes the next scheduled timeout,
   * while respecting scheduled time ordering.
   *
   * @param cx The context to use.
   */
  public void run( Context cx ) {
    run( cx, () -> false );
  }

  /**
   * Processes all micro-tasks and any scheduled timeouts, until a condition is met, or to completion.
   * <p>
   * The condition is checked before, and after each processing of, all pending micro-tasks, including those of
   * an executed timeout.
   *
   * @param cx The context to use.
   * @param isDone The condition which, when met, stops processing.
   */
  public void run( Context cx, BooleanSupplier isDone ) {
    while ( !isDone.getAsBoolean() ) {
      // Process any pending micro-tasks.
      cx.processMicrotasks();

      // Enqueues any scheduled set-timeout (repeat, if any).
      if ( isDone.getAsBoolean() || !enqueueNextTimeout( cx ) ) {
        return;
      }
    }
  }

  /**
   * Enqueues a micro-task for the next scheduled set-timeout, if any.
   * @param cx The context to use.
   * @return {@code true} if a set-timeout existed and was enqueued as a micro-task for execution;
   *         {@code false}, otherwise.
   */
  private boolean enqueueNextTimeout( Context cx ) {

    // Ignores/skips/advances time, while preserving time-ordering.
    TimeoutInfo nextTimeoutInfo = timeoutsQueue.peek();
    if ( nextTimeoutInfo == null ) {
      return false;
    }

    removeTimeoutInfo( nextTimeoutInfo );

    // Queue a micro-task with the set-timeout function.
    // Call cx.processMicrotasks() again to run it, and any other micro-tasks it enqueues, as well as to check for
    // additional scheduled timeouts.
    cx.enqueueMicrotask( () -> {
      // A nested run, e.g. via cgg.run() within a timeout callback, must not lose the outer timeout's nesting-level.
      final int nestingLevelPrev = nestingLevel;
      try {
        // Change the nesting-level to that of the set-timeout, while executing it.
        nestingLevel = nextTimeoutInfo.nestingLevel;

        // Advance time.
        currentTime = nextTimeoutInfo.atTime;

        nextTimeoutInfo.callback.call( cx, scope, scope, nextTimeoutInfo.args );
      } catch ( RuntimeException ex ) {
        // As in a browser, an error in a timeout callback must not prevent other timeouts from running.
        logger.error( "Error in setTimeout callback: " + ex.getMessage(), ex );
      } finally {
        nestingLevel = nestingLevelPrev;
      }
    } );

    return true;
  }

  private TimeoutInfo addTimeoutInfo( long atTime, int nestingLevel, Function callback, Object[] funArgs ) {
    TimeoutInfo timeoutInfo = new TimeoutInfo( takeNextId(), nestingLevel, atTime, callback, funArgs );
    timeoutsById.put( timeoutInfo.id, timeoutInfo );
    timeoutsQueue.offer( timeoutInfo );

    return timeoutInfo;
  }

  private void removeTimeoutInfo( TimeoutInfo timeoutInfo ) {
    timeoutsById.remove( timeoutInfo.id );
    timeoutsQueue.remove( timeoutInfo );
  }

  private int takeNextId() {
    return nextId++;
  }

  // region Helpers
  private Function readSetTimeoutCallback( Object arg ) {
    if ( arg instanceof Function ) {
      return (Function) arg;
    }

    // Any other value is converted to a string now, and evaluated in the global scope when the timeout runs.
    final String code = ScriptRuntime.toString( arg );

    return new LambdaFunction( scope, 0, ( lambdaCx, lambdaScope, thisObj, args ) -> {
      ScriptRuntime.evalSpecial( lambdaCx, scope, scope, new Object[] { code }, null, 0 );
      return null;
    } );
  }
  // endregion
}
