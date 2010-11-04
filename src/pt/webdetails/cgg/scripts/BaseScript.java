/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.scripts;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import pt.webdetails.cgg.datasources.DatasourceFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author pdpi
 */
public abstract class BaseScript implements Script {

    Scriptable scope;
    BaseScript() {
    
    }

    public void initializeObjects() {
        ContextFactory.getGlobal().enterContext();
        Object wrappedFactory = Context.javaToJS(new DatasourceFactory(), scope);
        ScriptableObject.putProperty(scope, "datasourceFactory", wrappedFactory);
    }

    public void setScope(Scriptable scope) {
        this.scope = scope;
        initializeObjects();
    }
}
