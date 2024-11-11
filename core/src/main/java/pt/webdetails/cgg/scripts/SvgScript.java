/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package pt.webdetails.cgg.scripts;

import java.util.Map;


import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.bridge.GVTBuilder;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import pt.webdetails.cgg.Chart;
import pt.webdetails.cgg.SVGChart;
import pt.webdetails.cgg.ScriptExecuteException;


public class SvgScript extends BaseScript {

  public SvgScript( final String source, final boolean isMultiPage ) {
    super( source, isMultiPage );
  }

  @Override
  public Chart execute( final Map<String, Object> params ) throws ScriptExecuteException {
    if ( Context.getCurrentContext() == null ) {
      throw new ScriptExecuteException();
    }

    Context.getCurrentContext().getFactory().enterContext();
    try {
      addSVGDocumentToScope();
      executeScript( params );
      final NativeJavaObject nativeDoc = (NativeJavaObject) ScriptableObject.getProperty( getScope(), "_document" );
      final Document unwrappedDoc = (Document) nativeDoc.unwrap();
      return new SVGChart( unwrappedDoc, getIsMultiPage() );
    } catch ( Exception e ) {
      throw new ScriptExecuteException( e );
    } finally {
      if ( Context.getCurrentContext() != null ) {
        Context.exit();
      }
    }
  }

  private void addSVGDocumentToScope() {

    // Create an SVG document
    final SVGDOMImplementation impl = (SVGDOMImplementation) SVGDOMImplementation.getDOMImplementation();
    final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    final SVGOMDocument document = (SVGOMDocument) impl.createDocument( svgNS, "svg", null );

    // Initialize the CSS Engine for the document
    final UserAgent userAgent = new UserAgentAdapter();
    final DocumentLoader loader = new DocumentLoader( userAgent );
    final BridgeContext ctx = new BridgeContext( userAgent, loader );
    new GVTBuilder().build( ctx, document );
    final CSSEngine eng = impl.createCSSEngine( document, ctx );
    document.setCSSEngine( eng );

    final Scriptable scope = getScope();
    // Expose the document to the javascript runtime
    final Object wrappedDocument = Context.javaToJS( document, scope );
    ScriptableObject.putProperty( scope, "_document", wrappedDocument );
  }
}
