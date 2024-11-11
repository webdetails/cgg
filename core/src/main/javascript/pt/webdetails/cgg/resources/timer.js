/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

(function() {
    // Synchronous, simulated-time, setTimeout implementation.
    // Based in https://html.spec.whatwg.org/multipage/webappapis.html#timers

    var taskQueue = [],
        tasksById = {},
        lastTid = 0,
        time = 0,
        level = 0;

    globalThis.__timer__run__ = run;
    globalThis.setTimeout = setTimeout;
    globalThis.clearTimeout = clearTimeout;

    // -------------

    function run() {
        var task;
        while((task = taskQueue.shift())) {
            delete tasksById[task.id];

            if(!task.cleared) {
                // Advance time
                time  = task.at;
                level = task.level + 1;

                try {
                    task.fun.apply(null, task.args);
                } catch(ex) {
                    print("ERROR - setTimeout: " + ex.message + ":\n" + ex.stack);
                }
            }
        }
    }

    function setTimeout(funOrCode, delay) {
        var efDelay = delay == null ? 0 : Number(delay);
        if(isNaN(efDelay) || efDelay < 0) efDelay = 0;

        if(level > 5 && efDelay < 4) efDelay = 4;

        var args = Array.prototype.slice.call(arguments, 2),
            fun  = typeof funOrCode === "function"
                ? funOrCode
                : buildEvaluator(String(funOrCode)),
            task = {
                    id:      ++lastTid,
                    level:   level,
                    fun:     fun,
                    args:    args,
                    at:      time + efDelay,
                    cleared: false
                };

        taskQueue.push(task);
        tasksById[task.id] = task;

        // Just easier this way...
        // Insert sort would be better...
        taskQueue.sort(tasksComparer);

        return task.id;
    }

    function clearTimeout(handle) {
        if(handle == null) handle = 0;

        var task = O_getOwn.call(activeTasksById, handle);
        if(task) task.cleared = true;
    }

    function O_getOwn(p, dv) {
        return Object.prototype.hasOwnProperty.call(this, p) ? this[p] : dv;
    }

    // -------------

    function tasksComparer(ta, tb) {
        return compare(ta.at, tb.at) || compare(ta.id, tb.id);
    }

    function buildEvaluator(__code__) {
        // Wrap twice so that passed in arguments are inaccessible.
        return function() {
            (function() { eval(__code__); })();
        };
    }

    function compare(a, b) {
        return a === b ? 0 : a > b ? 1 : -1;
    }
}());
