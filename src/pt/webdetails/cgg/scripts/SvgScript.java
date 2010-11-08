/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.scripts;

import java.util.Map;

import org.mozilla.javascript.*;
import pt.webdetails.cgg.charts.Chart;
import pt.webdetails.cgg.charts.SVGChart;

/**
 *
 * @author pdpi
 */
class SvgScript extends BaseScript {

    
    private Map<String, String> params;

    SvgScript() {
    }

    SvgScript(String source) {
        super(source);
    }

    @Override
    public void setScope(Scriptable scope) {
        super.setScope(scope);
        initializeObjects();
    }

    @Override
    public Chart execute() {
        return execute((Map<String, String>) null);
    }

    @Override
    public Chart execute(Map<String, String> params) {
        Context cx = ContextFactory.getGlobal().enterContext();
        try {
            executeScript(params);
            Object result = cx.evaluateString(scope, "output", "<cmd>", 1, null);
            String output = Context.toString(result);//.replaceAll("</?div.*?>", "");
            return new SVGChart(output);
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (Context.getCurrentContext() != null) {
                Context.exit();
            }
        }
        return null;
    }
}
