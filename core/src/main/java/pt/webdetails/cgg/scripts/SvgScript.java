/*!
* Copyright 2002 - 2018 Webdetails, a Hitachi Vantara company.  All rights reserved.
*
* This software was developed by Webdetails and is provided under the terms
* of the Mozilla Public License, Version 2.0, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to  http://mozilla.org/MPL/2.0/. The Initial Developer is Webdetails.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

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
