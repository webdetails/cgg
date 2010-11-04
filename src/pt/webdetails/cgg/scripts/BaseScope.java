/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.scripts;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author pdpi
 */
class BaseScope extends ImporterTopLevel {

    private static final Log logger = LogFactory.getLog(BaseScope.class);
    private boolean sealedStdLib = false;
    boolean initialized;

    public BaseScope() {
        super();
    }

    public void init(Context cx) {
        // Define some global functions particular to the shell. Note
        // that these functions are not part of ECMA.
        initStandardObjects(cx, sealedStdLib);
        String[] names = {
            "print"};
        defineFunctionProperties(names, BaseScope.class,
                ScriptableObject.DONTENUM);

        initialized = true;
    }

    public static Object print(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {

        for (Object arg : args) {
            String s = Context.toString(arg);
            logger.info(s);
        }
        return Context.getUndefinedValue();
    }
}
