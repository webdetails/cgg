/*!
* Copyright 2002 - 2021 Webdetails, a Hitachi Vantara company.  All rights reserved.
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

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DefaultFontFamilyResolver;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Node;

/**
 * @author pdpi
 */

public class BaseScope extends ImporterTopLevel {

  private static final long serialVersionUID = 8594060356268613175L;
  protected static final Log logger = LogFactory.getLog( BaseScope.class );
  protected boolean sealedStdLib;
  private boolean initialized;
  private ScriptFactory scriptFactory;
  private HashMap<String, org.mozilla.javascript.Script> userScripts;
  private HashMap<String, org.mozilla.javascript.Script> processed;

  public BaseScope() {
    userScripts = new HashMap<String, org.mozilla.javascript.Script>();
    processed = new HashMap<String, org.mozilla.javascript.Script>();
    sealedStdLib = false;
  }

  public void init( final Context cx ) {
    if ( initialized == false ) {
      // Define some global functions particular to the shell. Note
      // that these functions are not part of ECMA.
      initStandardObjects( cx, sealedStdLib );
      final String[] names = {
        "print", "load", "lib", "_loadSvg", "_xmlToString", "getTextLenCGG", "getTextHeightCGG", "res", "readResource"
      };
      defineFunctionProperties( names, BaseScope.class, ScriptableObject.DONTENUM );
      initialized = true;
    }
  }

  public static Object print( final Context cx, final Scriptable thisObj,
                              final Object[] args, final Function funObj ) {

    for ( final Object arg : args ) {
      final String s = Context.toString( arg );
      logger.info( s );
    }
    return Context.getUndefinedValue();
  }

  public static Object load( final Context cx, final Scriptable thisObj,
                             final Object[] args, final Function funObj ) {
    final Object arg = unwrapArgument( args[ 0 ] );

    if ( arg == null ) {
      return Context.toBoolean( false );
    }

    try {
      final String file = arg.toString();
      final BaseScope scope = (BaseScope) thisObj;
      return Context.toBoolean( scope.loadScript( cx, file ) );
    } catch ( Exception e ) {
      logger.warn( "Failed to call 'load'", e );
      return Context.toBoolean( false );
    }
  }

  public boolean loadScript( Context cx, String file ) throws IOException, ScriptResourceNotFoundException {
    if ( file == null ) {
      return false;
    }

    String contextResourceURI = scriptFactory.getContextResourceURI( file );
    org.mozilla.javascript.Script s = userScripts.get( contextResourceURI );
    if ( s == null ) {
      Reader contextLibraryScript = scriptFactory.getContextLibraryScript( file );
      try {
        logger.info( "Compiled context reader for " + file );
        s = cx.compileReader( contextLibraryScript, file, 1, null );
        userScripts.put( contextResourceURI, s );
      } finally {
        contextLibraryScript.close();
      }
    }
    if ( s != null ) {
      s.exec( cx, this );
    }
    return true;
  }

  private static Object unwrapArgument( final Object arg ) {
    if ( arg instanceof NativeJavaObject ) {
      final NativeJavaObject no = (NativeJavaObject) arg;
      return no.unwrap();
    }
    return arg;
  }

  public static Object _loadSvg( final Context cx, final Scriptable thisObj,
                                 final Object[] args, final Function funObj ) {
    final Object arg = unwrapArgument( args[ 0 ] );

    if ( arg == null ) {
      return Context.toBoolean( false );
    }
    final String file = arg.toString();
    try {
      final BaseScope scope = (BaseScope) thisObj;
      final String parser = "org.apache.xerces.parsers.SAXParser"; //XMLResourceDescriptor.getXMLParserClassName();
      final SAXSVGDocumentFactory f = new SAXSVGDocumentFactory( parser );
      final String uri = scope.scriptFactory.getContextResourceURI( file );
      final InputStream stream = scope.scriptFactory.getContextResource( file );
      try {
        final SVGOMDocument doc = (SVGOMDocument) f.createDocument( uri, stream );

        // Initialize the CSS Engine for the document
        final SVGDOMImplementation impl = (SVGDOMImplementation) SVGDOMImplementation.getDOMImplementation();
        final UserAgent userAgent = new UserAgentAdapter();
        final DocumentLoader loader = new DocumentLoader( userAgent );
        final BridgeContext ctx = new BridgeContext( userAgent, loader );
        new GVTBuilder().build( ctx, doc );
        final CSSEngine eng = impl.createCSSEngine( doc, ctx );
        doc.setCSSEngine( eng );
        return Context.javaToJS( doc, scope );
      } finally {
        stream.close();
      }
    } catch ( Exception e ) {
      logger.warn( "Failed to call '_loadSVG'", e );
      return Context.getUndefinedValue();
    }
  }

  public static Object lib( final Context cx, final Scriptable thisObj,
                            final Object[] args, final Function funObj ) {
    final Object arg = unwrapArgument( args[ 0 ] );

    if ( arg == null ) {
      return Context.toBoolean( false );
    }

    final boolean isSilent = args.length > 1 && Context.toBoolean( args[ 1 ] );

    try {
      final String file = arg.toString();
      final BaseScope scope = (BaseScope) thisObj;
      return Context.toBoolean( scope.loadSystemScript( cx, file ) );
    } catch ( Exception e ) {
      if ( !isSilent ) {
        logger.warn( "Failed to call 'lib'", e );
      }
      return Context.toBoolean( false );
    }
  }

  public boolean loadSystemScript( Context cx, String file ) throws IOException, ScriptResourceNotFoundException {
    if ( file == null ) {
      return false;
    }

    String contextResourceURI = file;
    org.mozilla.javascript.Script s = processed.get( contextResourceURI );
    if ( s == null ) {
      Reader script = scriptFactory.getSystemLibraryScript( file );
      try {
        logger.info( "Compiled system reader for " + file );
        s = cx.compileReader( script, file, 1, null );
        processed.put( contextResourceURI, s );
      } finally {
        script.close();
      }
    }
    if ( s != null ) {
      s.exec( cx, this );
    }
    return true;
  }

  public static Object readResource( final Context cx, final Scriptable thisObj,
                                     final Object[] args, final Function funObj ) {
    final Object arg = unwrapArgument( args[ 0 ] );

    if ( arg == null ) {
      return Context.toString( "" );
    }

    try {
      final String url = arg.toString();
      final BaseScope scope = (BaseScope) thisObj;

      return Context.toString( scope.readResourceAsText( cx, url ) );

    } catch ( Exception e ) {
      logger.warn( "Failed to call 'load'", e );
      return Context.toBoolean( false );
    }
  }

  public String readResourceAsText( Context cx, String file ) {
    try {
      BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( scriptFactory.getWebResource( file ) ) );

      StringBuilder textBuilder = new StringBuilder();
      String line;
      while ( ( line = bufferedReader.readLine() ) != null ) {
        textBuilder.append( line );
      }

      bufferedReader.close();

      return textBuilder.toString();
    } catch ( Exception e ) {
      logger.warn( "Failed to call 'readResourceAsText'", e );
      return null;
    }
  }

  //A res is an auxiliary script which is defined by a relative path from the original script.
  public static Object res( final Context cx, final Scriptable thisObj,
                            final Object[] args, final Function funObj ) {
    return load( cx, thisObj, args, funObj );
  }

  public static Object _xmlToString( final Context cx, final Scriptable thisObj,
                                     final Object[] args, final Function funObj ) {
    final Object arg = unwrapArgument( args[ 0 ] );
    final Node node = (Node) arg;
    try {
      final Source source = new DOMSource( node );
      final StringWriter stringWriter = new StringWriter();
      final Result result = new StreamResult( stringWriter );
      final TransformerFactory factory = TransformerFactory.newInstance();
      final Transformer transformer = factory.newTransformer();
      transformer.setOutputProperty( OutputKeys.INDENT, "yes" );
      transformer.setOutputProperty( "{http://xml.apache.org/xslt}indent-amount", "2" );
      transformer.transform( source, result );

      final BaseScope scope = (BaseScope) thisObj;
      return Context.javaToJS( stringWriter.getBuffer().toString(), scope );
    } catch ( TransformerConfigurationException e ) {
      logger.warn( "Failed to call '_xmlToString'", e );
    } catch ( TransformerException e ) {
      logger.warn( "Failed to call '_xmlToString'", e );
    }
    return null;
  }

  public void setScriptFactory( final ScriptFactory scriptFactory ) {
    this.scriptFactory = scriptFactory;
  }

  public static Object getTextLenCGG( Context cx, Scriptable thisObj,
                                      Object[] args, Function funObj ) {

    try {
      String text = Context.toString( args[ 0 ] );
      String fontFamily = Context.toString( args[ 1 ] );
      String fontSize = Context.toString( args[ 2 ] ).trim();
      String fontStyle = "normal";
      String fontWeight = "normal";

      if ( args.length > 3 ) {
        fontStyle = Context.toString( args[ 3 ] );

        if ( args.length > 4 ) {
          fontWeight = Context.toString( args[ 4 ] );
        }
      }

      Font ffont = getFont( fontFamily, fontSize, fontStyle, fontWeight );

      JLabel label = new JLabel();

      FontMetrics fMetric = label.getFontMetrics( ffont );

      int width = fMetric.stringWidth( text );

      return Context.toNumber( width );

    } catch ( RuntimeException e ) {
      logger.warn( "Failed to call 'getTextLenCGG'", e );
      return Context.toNumber( 0 );
    }
  }

  public static Object getTextHeightCGG( Context cx, Scriptable thisObj,
                                         Object[] args, Function funObj ) {

    try {
      // String text = Context.toString(args[0]);
      String fontFamily = Context.toString( args[ 1 ] );
      String fontSize = Context.toString( args[ 2 ] ).trim();
      String fontStyle = "normal";
      String fontWeight = "normal";

      if ( args.length > 3 ) {
        fontStyle = Context.toString( args[ 3 ] );

        if ( args.length > 4 ) {
          fontWeight = Context.toString( args[ 4 ] );
        }
      }

      Font ffont = getFont( fontFamily, fontSize, fontStyle, fontWeight );

      JLabel label = new JLabel();

      FontMetrics fMetric = label.getFontMetrics( ffont );

      int height = fMetric.getHeight();

      return Context.toNumber( height );

    } catch ( RuntimeException e ) {
      logger.warn( "Failed to call 'getTextHeightCGG'", e );
      return Context.toNumber( 10 );
    }
  }

  private static Font getFont( String fontFamily, String fontSize, String fontStyle, String fontWeight ) {
    // Get size unit
    boolean convert = false;
    if ( fontSize.endsWith( "px" ) ) {
      //          convert = true;
      fontSize = fontSize.substring( 0, fontSize.length() - 2 );
    } else if ( fontSize.endsWith( "pt" ) ) {
      fontSize = fontSize.substring( 0, fontSize.length() - 2 );
    }

    //parse size
    float size = 15;
    try {
      size = Integer.parseInt( fontSize );
    } catch ( NumberFormatException nfe ) {
      // NOOP
    }


    int isize = Math.round( size );
    //size conversion
    if ( convert ) { //px->pt
      // pt = (3/4) * css_pixel
      // 3 / 4 = 72 /96
      // (see: http://static.zealous-studios.co.uk/projects/web_tests/PPI%20tests.html)
      size = 0.75f * size;


      // java on windows point size correction
      // (see: http://www.3rd-evolution.de/tkrammer/docs/java_font_size.html)
      int screenDpi = Toolkit.getDefaultToolkit().getScreenResolution();
      isize = Math.round( size * screenDpi / 72.0f );
    }


    int javaFontStyle = parseCssFontStyleAndWeight( fontStyle, fontWeight );

    return decodeFont( fontFamily, javaFontStyle, isize );
  }

  private static int parseCssFontStyleAndWeight( String fontStyle, String fontWeight ) {
    //Font.ITALIC
    //Font.BOLD
    //Font.PLAIN
    boolean isItalic = false;
    boolean isBold = false;

    if ( fontStyle != null ) {
      fontStyle = fontStyle.toLowerCase();

      if ( fontStyle.equals( "italic" ) || fontStyle.equals( "oblique" ) ) {
        isItalic = true;
      }
    }

    if ( fontWeight != null ) {
      fontWeight = fontWeight.toLowerCase();

      if ( fontWeight.equals( "bold" )
        || fontWeight.equals( "bolder" )
        || fontWeight.equals( "700" )
        || fontWeight.equals( "800" )
        || fontWeight.equals( "900" ) ) {
        isBold = true;
      }
    }

    if ( isItalic ) {
      return isBold ? ( Font.ITALIC | Font.BOLD ) : Font.ITALIC;
    }

    return isBold ? Font.BOLD : Font.PLAIN;
  }

  private static Font decodeFont( String fontFamily, int fontStyle, int isize ) {
    String fontStyleText = "";
    switch ( fontStyle ) {
      case Font.BOLD:
        fontStyleText = "BOLD ";
        break;

      case Font.ITALIC:
        fontStyleText = "ITALIC ";
        break;

      case ( Font.ITALIC | Font.BOLD ):
        fontStyleText = "BOLDITALIC ";
        break;
    }

    String capFontFamily = fontFamily.substring( 0, 1 ).toUpperCase() + fontFamily.substring( 1, fontFamily.length() );

    Font ffont = Font.decode( capFontFamily + " " + fontStyleText + isize );
    if ( ffont.getFamily().equals( Font.DIALOG ) && !fontFamily.equals( "dialog" ) ) {
      // defaulted, try family
      GVTFontFamily awtFamily = DefaultFontFamilyResolver.SINGLETON.resolve( fontFamily );
      if ( awtFamily == null ) {
        awtFamily = DefaultFontFamilyResolver.SINGLETON.getDefault();
      }

      ffont = new Font( awtFamily.getFamilyName(), fontStyle, isize );
    }

    return ffont;
  }
}
