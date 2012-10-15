/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg.scripts;

import java.util.Map;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.css.engine.CSSEngine;

import org.mozilla.javascript.*;
import pt.webdetails.cgg.charts.Chart;
import pt.webdetails.cgg.charts.SVGChart;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.w3c.dom.Document;

/**
 *
 * @author pdpi
 */
class SvgScript extends BaseScript {

    private Document document;

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
        return execute((Map<String, Object>) null);
    }

    @Override
    public Chart execute(Map<String, Object> params) {
        ContextFactory.getGlobal().enter();
        try {
            initDocument();
            executeScript(params);
            return new SVGChart((Document) ((NativeJavaObject) ScriptableObject.getProperty(scope, "_document")).unwrap());
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if (Context.getCurrentContext() != null) {
                Context.exit();
            }
        }
        return null;
    }

    private void initDocument() {

        // Create an SVG document
        SVGDOMImplementation impl = (SVGDOMImplementation) SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        document = impl.createDocument(svgNS, "svg", null);
        document.getDocumentElement().setAttribute("preserveAspectRatio", "none");

        // Initialize the CSS Engine for the document
        UserAgent userAgent = new UserAgentAdapter();
        DocumentLoader loader = new DocumentLoader(userAgent);
        BridgeContext ctx = new BridgeContext(userAgent, loader);
        ctx.setDynamic(true);
        ctx.setDynamicState(BridgeContext.DYNAMIC);
        CSSEngine eng = impl.createCSSEngine((SVGOMDocument) document, ctx);
        ((SVGOMDocument) document).setCSSEngine(eng);

        // Expose the document to the javascript runtime
        Object wrappedDocument = Context.javaToJS(document, scope);
        ScriptableObject.putProperty(scope, "_document", wrappedDocument);
    }
}
