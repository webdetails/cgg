/*
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 */

package pt.webdetails.cgg.scripts;

import java.util.Map;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;

import pt.webdetails.cgg.Chart;
import pt.webdetails.cgg.SVGChart;
import pt.webdetails.cgg.ScriptExecuteException;

/**
 * @author pdpi
 */
public class SvgScript extends BaseScript
{
  private static final Log logger = LogFactory.getLog(SvgScript.class);
  
  public SvgScript(final String source)
  {
    super(source);
  }

  @Override
  public Chart execute(final Map<String, Object> params) throws ScriptExecuteException
  {
    ContextFactory.getGlobal().enter();
    try
    {
      addSVGDocumentToScope();
      executeScript(params);
      final NativeJavaObject nativeDoc = (NativeJavaObject) ScriptableObject.getProperty(getScope(), "_document");
      final Document unwrappedDoc = (Document) nativeDoc.unwrap();
      return new SVGChart(unwrappedDoc);
    }
    catch (Exception e)
    {
      throw new ScriptExecuteException(e);
    }
    finally
    {
      if (Context.getCurrentContext() != null)
      {
        Context.exit();
      }
    }
  }

  private void addSVGDocumentToScope()
  {

    // Create an SVG document
    final SVGDOMImplementation impl = (SVGDOMImplementation) SVGDOMImplementation.getDOMImplementation();
    final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    final SVGOMDocument document = (SVGOMDocument) impl.createDocument(svgNS, "svg", null);

    // Initialize the CSS Engine for the document
    final UserAgent userAgent = new UserAgentAdapter();
    final DocumentLoader loader = new DocumentLoader(userAgent);
    final BridgeContext ctx = new BridgeContext(userAgent, loader);
    final CSSEngine eng = impl.createCSSEngine(document, ctx);
    document.setCSSEngine(eng);

    final Scriptable scope = getScope();
    // Expose the document to the javascript runtime
    final Object wrappedDocument = Context.javaToJS(document, scope);
    ScriptableObject.putProperty(scope, "_document", wrappedDocument);
  }
}
