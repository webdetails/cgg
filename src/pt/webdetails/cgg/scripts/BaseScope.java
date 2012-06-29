/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.scripts;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.io.FileReader;
import java.io.StringWriter;

import javax.swing.JLabel;
import javax.swing.text.AttributeSet.FontAttribute;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.css.engine.CSSEngine;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.font.FontFamilyResolver;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.font.UnresolvedFontFamily;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.pentaho.reporting.libraries.libsparklines.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author pdpi
 */
class BaseScope extends ImporterTopLevel {

    protected static final Log logger = LogFactory.getLog(BaseScope.class);
    protected boolean sealedStdLib = false;
    boolean initialized;
    protected String basePath, systemPath;

    public BaseScope() {
        super();
    }

    public void init(Context cx) {
        // Define some global functions particular to the shell. Note
        // that these functions are not part of ECMA.
        initStandardObjects(cx, sealedStdLib);
        String[] names = {
            "print", "load", "lib", "_loadSvg", "_xmlToString", "getTextLenCGG", "getTextHeightCGG"};
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

    public static Object load(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {

        String file = args[0] instanceof NativeJavaObject ? ((NativeJavaObject)args[0]).unwrap().toString(): args[0].toString();
        try {
            BaseScope scope = (BaseScope) thisObj;
            cx.evaluateReader(scope, new FileReader(scope.basePath + "/" + file), file, 1, null);
        } catch (Exception e) {
            logger.error(e);
            return Context.toBoolean(false);
        }
        return Context.toBoolean(true);
    }

    public static Object getTextLenCGG(Context cx, Scriptable thisObj,
    		Object[] args, Function funObj) 
    {
    	String text = Context.toString(args[0]);
        String fontFamily = Context.toString(args[1]);
        String fontSize = Context.toString(args[2]).trim();
        String fontStyle = "normal";
        String fontWeight = "normal";

        if(args.length > 3){
            fontStyle = Context.toString(args[3]);
            
            if(args.length > 4){
                fontWeight = Context.toString(args[4]);
            }
        }

        Font ffont = getFont(fontFamily, fontSize, fontStyle, fontWeight);
        
        JLabel label = new JLabel();
        
        FontMetrics fMetric = label.getFontMetrics(ffont);
        
        int width = fMetric.stringWidth(text);
        
        return Context.toNumber(width);
    }
    
    public static Object getTextHeightCGG(Context cx, Scriptable thisObj,
    		Object[] args, Function funObj) 
    {
      // String text = Context.toString(args[0]);
      String fontFamily = Context.toString(args[1]);
      String fontSize = Context.toString(args[2]).trim();
      String fontStyle = "normal";
      String fontWeight = "normal";

      if(args.length > 3){
        fontStyle = Context.toString(args[3]);

        if(args.length > 4){
          fontWeight = Context.toString(args[4]);
        }
      }

      Font ffont = getFont(fontFamily, fontSize, fontStyle, fontWeight);
      
      JLabel label = new JLabel();
      
      FontMetrics fMetric = label.getFontMetrics(ffont);
      
      int height = fMetric.getHeight();
      
      return Context.toNumber(height);
    }
    
    private static Font getFont(String fontFamily, String fontSize, String fontStyle, String fontWeight){
    	// Get size unit
        boolean convert = false;
        if(fontSize.endsWith("px")){
          convert = true;
          fontSize = fontSize.substring(0, fontSize.length() -2);
        } else if(fontSize.endsWith("pt")){
          fontSize = fontSize.substring(0, fontSize.length() -2);
        }
        
        //parse size
        float size = 15;
        try
        {
          size = Integer.parseInt(fontSize);
        }
        catch (NumberFormatException nfe) 
        {
        }
        
        //size conversion
        if(convert){//px->pt
          // pt = (3/4) * css_pixel
          // 3 / 4 = 72 /96
          // (see: http://static.zealous-studios.co.uk/projects/web_tests/PPI%20tests.html)
          size = 0.75f * size;
        }
        
        // java on windows point size correction
        // (see: http://www.3rd-evolution.de/tkrammer/docs/java_font_size.html)
        int screenDpi = Toolkit.getDefaultToolkit().getScreenResolution();
        int isize = Math.round(size * screenDpi / 72.0f);
        
        int javaFontStyle = parseCssFontStyleAndWeight(fontStyle, fontWeight);

        return decodeFont(fontFamily, javaFontStyle, isize);
    }

    private static int parseCssFontStyleAndWeight(String fontStyle, String fontWeight)
    {
       //Font.ITALIC
       //Font.BOLD
       //Font.PLAIN
        boolean isItalic = false;
        boolean isBold = false;

        if(fontStyle != null)
        {
            fontStyle = fontStyle.toLowerCase();

            if(fontStyle.equals("italic") || fontStyle.equals("oblique")){
                isItalic = true;
            }
        }

        if(fontWeight != null)
        {
            fontWeight = fontWeight.toLowerCase();

            if(fontWeight.equals("bold")  ||
               fontWeight.equals("bolder") ||
               fontWeight.equals("700") ||
               fontWeight.equals("800") ||
               fontWeight.equals("900")){
                isBold = true;
            }
        }

        if(isItalic)
        {
            return isBold ? (Font.ITALIC | Font.BOLD) : Font.ITALIC;
        }

        return isBold ? Font.BOLD : Font.PLAIN;
    }

    private static Font decodeFont(String fontFamily, int fontStyle, int isize)
    {
        String fontStyleText = "";
        switch(fontStyle)
        {
            case Font.BOLD:
                fontStyleText = "BOLD ";
                break;

            case Font.ITALIC:
                fontStyleText = "ITALIC ";
                break;

            case (Font.ITALIC | Font.BOLD):
                fontStyleText = "BOLDITALIC ";
                break;
        }

        String capFontFamily = fontFamily.substring(0,1).toUpperCase() + fontFamily.substring(1,fontFamily.length());

        Font ffont = Font.decode(capFontFamily + " " + fontStyleText  + isize);
        if(ffont.getFamily().equals(Font.DIALOG) && !fontFamily.equals("dialog"))
        {
            // defaulted, try family
            GVTFontFamily awtFamily =  FontFamilyResolver.resolve(fontFamily);
            if(awtFamily == null) {
                awtFamily = FontFamilyResolver.defaultFont;
            }

            ffont = new Font(awtFamily.getFamilyName(), fontStyle, isize);
        }

        return ffont;
    }

    public static Object _loadSvg(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {

        String file = args[0].toString();
        try {
            BaseScope scope = (BaseScope) thisObj;
            String parser = "org.apache.xerces.parsers.SAXParser";//XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            String uri = "file:" + scope.basePath + "/" + file;
            Document doc = factory.createDocument(uri);

            // Initialize the CSS Engine for the document
            SVGDOMImplementation impl = (SVGDOMImplementation) SVGDOMImplementation.getDOMImplementation();
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext ctx = new BridgeContext(userAgent, loader);
            CSSEngine eng = impl.createCSSEngine((SVGOMDocument) doc, ctx);
            ((SVGOMDocument) doc).setCSSEngine(eng);

            return Context.javaToJS(doc, scope);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
            return Context.getUndefinedValue();
        }
    }

    public static Object lib(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {

        String file = args[0].toString();
        try {
            BaseScope scope = (BaseScope) thisObj;
            cx.evaluateReader(scope, new FileReader(scope.systemPath + "/" + file), file, 1, null);
        } catch (Exception e) {
            logger.error(e);
            return Context.toBoolean(false);
        }
        return Context.toBoolean(true);
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public void setSystemPath(String systemPath) {
        this.systemPath = systemPath;
    }

    public static Object _xmlToString(Context cx, Scriptable thisObj,
            Object[] args, Function funObj) {
        Node node = (Node) ((NativeJavaObject) args[0]).unwrap();
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);

            BaseScope scope = (BaseScope) thisObj;
            return Context.javaToJS(stringWriter.getBuffer().toString(), scope);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
