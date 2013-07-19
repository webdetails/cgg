/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
package pt.webdetails.cgg;


import pt.webdetails.cpf.repository.RepositoryAccess;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Date;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pt.webdetails.cgg.charts.Chart;
import pt.webdetails.cgg.charts.SVGChart;
import pt.webdetails.cgg.scripts.Script;
import pt.webdetails.cgg.scripts.ScriptFactory;
import pt.webdetails.cgg.scripts.ScriptFactory.ScriptType;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

@Path("/cgg/api/services")
public class CggService {
  
  
  private static final Log logger = LogFactory.getLog(CggService.class);
  private OutputStream outputStream;
  
   private enum OutputType {

        SVG, PNG, PDF;
        
        public static OutputType parse(String value){
          return valueOf(StringUtils.upperCase(value));
        }
    }  
  
   
 private static final String MIME_SVG = "image/svg+xml";
 
 
 public void setOutputStream(final OutputStream stream){
     outputStream = stream;
 }
  
 @GET
 @Path("/refresh")
 @Produces("text/plain")
 @Consumes({APPLICATION_XML, APPLICATION_JSON})
 public Response refresh() {
    ScriptFactory.getInstance().clearCachedScopes();
    return Response.ok().build();
 }
       
 @GET
 @Path("/draw")
// @Produces("text/plain")
 @Consumes({ APPLICATION_XML, APPLICATION_JSON }) 
 public void draw(
         @QueryParam("script") String script,
         @DefaultValue("svg") @QueryParam("type") String type,
         @DefaultValue("png") @QueryParam("outputType") String outputType,
         @DefaultValue("") @QueryParam("attachmentName") String attachmentName,
         @DefaultValue("0") @QueryParam("width") Long width,
         @DefaultValue("0") @QueryParam("height") Long height,
         @Context HttpServletResponse servletResponse, @Context HttpServletRequest servletRequest) 
 {
    this.draw(script, type, outputType, attachmentName, null, width, height, servletResponse, servletRequest);
 }

 public void draw(
          String script,
          String type,
          String outputType,
          String attachmentName,
          String multiChartOverflow,
          Long width,
          Long height,
          HttpServletResponse servletResponse, 
          HttpServletRequest servletRequest) {
  
        try {

            HashMap<String, Object> params = new HashMap<String, Object>();
            @SuppressWarnings("unchecked")
            
             
            Enumeration<String> inputParams;
            if(servletRequest != null){
                inputParams = servletRequest.getParameterNames();
                while (inputParams.hasMoreElements()) {
                    String paramName = inputParams.nextElement();
                    if (paramName.startsWith("param")) {
                        String pName = paramName.substring(5);
                        params.put(pName, servletRequest.getParameter(paramName));
                    }
                }
            }
            
            if(multiChartOverflow != null && !multiChartOverflow.isEmpty()) {
            	params.put("multiChartOverflow", multiChartOverflow);
            }
            
            OutputType scriptOutputType = OutputType.parse(outputType);
            ScriptType scriptType = ScriptType.parse(type);

            if (!attachmentName.isEmpty()) {
                String fileName = attachmentName.indexOf(".") > 0 ? attachmentName : attachmentName + "." + type;
                setResponseHeaders(getMimeType(fileName), fileName, servletResponse);
            }
            else{
                setResponseHeaders(getMimeType(scriptOutputType.name()), servletResponse);
            }          
            
            logger.debug("Starting:" + new Date().getTime());
            
            try {
                Chart chart = evaluateChart(params, script, scriptType, width, height);
                getOutput(chart, scriptOutputType, servletResponse == null ? outputStream : servletResponse.getOutputStream(), servletResponse);
                logger.debug("Image exported:" + new Date().getTime());
            } catch (Exception e) {
                logger.error(e);
            }
        } catch (Exception ex) {
            logger.fatal(ex);
        }  
  }
  
  
   public enum FileType
    {
      JPG, JPEG, PNG, GIF, BMP, JS, CSS, HTML, HTM, XML,
      SVG, PDF, TXT, DOC, DOCX, XLS, XLSX, PPT, PPTX;
      
      public static FileType parse(String value){
        return valueOf(StringUtils.upperCase(value));
      }
    }
    
    public static class MimeType {
      public static final String CSS = "text/css";
      public static final String JAVASCRIPT = "text/javascript";
      public static final String PLAIN_TEXT = "text/plain";
      public static final String HTML = "text/html";
      public static final String XML = "text/xml";
      public static final String JPEG = "img/jpeg";
      public static final String PNG = "image/png";
      public static final String GIF = "image/gif";
      public static final String BMP = "image/bmp";
      public static final String JSON = "application/json";
      public static final String PDF = "application/pdf";

      public static final String DOC = "application/msword";
      public static final String DOCX = "application/msword";
      
      public static final String XLS = "application/msexcel";      
      public static final String XLSX = "application/msexcel";
      
      public static final String PPT = "application/mspowerpoint";
      public static final String PPTX = "application/mspowerpoint";
    }
    
    protected static final EnumMap<FileType, String> mimeTypes = new EnumMap<FileType, String>(FileType.class);
    
    static
    {
      /*
       * Image types
       */
      mimeTypes.put(FileType.JPG, MimeType.JPEG);
      mimeTypes.put(FileType.JPEG, MimeType.JPEG);
      mimeTypes.put(FileType.PNG, MimeType.PNG);
      mimeTypes.put(FileType.GIF, MimeType.GIF);
      mimeTypes.put(FileType.BMP, MimeType.BMP);

      /*
       * HTML (and related) types
       */
      // Deprecated, should be application/javascript, but IE doesn't like that
      mimeTypes.put(FileType.JS, MimeType.JAVASCRIPT);
      mimeTypes.put(FileType.HTM, MimeType.HTML);
      mimeTypes.put(FileType.HTML, MimeType.HTML);
      mimeTypes.put(FileType.CSS, MimeType.CSS);
      mimeTypes.put(FileType.XML, MimeType.XML);
      mimeTypes.put(FileType.TXT, MimeType.PLAIN_TEXT);
    }
    
    protected String getMimeType(String fileName){
      String[] fileNameSplit = StringUtils.split(fileName, '.');// fileName.split("\\.");
      try{
        return getMimeType(FileType.valueOf(fileNameSplit[fileNameSplit.length - 1].toUpperCase()));
      }
      catch(Exception e){
        logger.warn("Unrecognized extension for file name " + fileName);
        return "";
      }
    }
    
    protected String getMimeType(FileType fileType){
      if(fileType == null) return "";
      String mimeType = mimeTypes.get(fileType);
      return mimeType == null ? "" : mimeType;
    }
  
  
  
  

    /**
     * @param params
     * @param scriptName
     * @param scriptType
     * @param width
     * @param height
     * @return
     * @throws FileNotFoundException 
     */
    private Chart evaluateChart(HashMap<String, Object> params, String scriptName, ScriptType scriptType, Long width, Long height) {
      ScriptFactory factory = ScriptFactory.getInstance();
      factory.setSystemPath(RepositoryAccess.getSystemDir() + "/cgg/libs/");
      
      try {
          Script script = factory.createScript(scriptName, scriptType, width, height);
      
          logger.debug("Script created:" + new Date().getTime());
          Chart chart = script.execute(params);
          logger.debug("Script executed:" + new Date().getTime());
          return chart;
      } catch (FileNotFoundException e) {
          logger.error(e);
          return null;
      }   
    }

    private void getOutput(Chart chart, OutputType outputType, OutputStream out,HttpServletResponse response) {


        switch (outputType) {
            case PDF:
                /*
                if (chart instanceof pt.webdetails.cgg.charts.SVGChart) {
                chart.toPDF(out);
                }
                 */
                break;
            case PNG:
                chart.toPNG(out);
                break;
            case SVG:
                setResponseHeaders(MIME_SVG, null, response);
                if (chart instanceof pt.webdetails.cgg.charts.SVGChart) {
                    ((SVGChart) chart).toSVG(out);
                }
                else {
                  logger.error("Trying to get SVG from non-SVG chart");
                }
                break;
            default:
        }
    }
  
  
  
   protected void setResponseHeaders(final String mimeType, final HttpServletResponse response){
      setResponseHeaders(mimeType, 0, null, response);
    }
    
    protected void setResponseHeaders(final String mimeType, final String attachmentName, final HttpServletResponse response){
      setResponseHeaders(mimeType, 0, attachmentName, response);
    }
    
    protected void setResponseHeaders(final String mimeType, final int cacheDuration, final String attachmentName, final HttpServletResponse response)
    {
      // Make sure we have the correct mime type
      
     

      if (response == null)
      {
        logger.warn("Parameter 'httpresponse' not found!");
        return;
      }

      response.setHeader("Content-Type", mimeType);

      if (attachmentName != null)
      {
        response.setHeader("content-disposition", "attachment; filename=" + attachmentName);
      } // Cache?

      if (cacheDuration > 0)
      {
        response.setHeader("Cache-Control", "max-age=" + cacheDuration);
      }
      else
      {
        response.setHeader("Cache-Control", "max-age=0, no-store");
      }
    }    
    
    
  
  
}
