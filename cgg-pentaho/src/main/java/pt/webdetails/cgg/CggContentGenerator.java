/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package pt.webdetails.cgg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IParameterProvider;
import pt.webdetails.cpf.SimpleContentGenerator;
import pt.webdetails.cpf.annotations.AccessLevel;
import pt.webdetails.cpf.annotations.Exposed;
import pt.webdetails.cpf.repository.RepositoryAccess;

/**
 *
 * @author pdpi
 */
public class CggContentGenerator extends SimpleContentGenerator {

    private static final long serialVersionUID = 1L;
    
    private static final Log logger = LogFactory.getLog(CggContentGenerator.class);

    private class InternalSetResponseHeaderDelegate implements SetResponseHeaderDelegate
    {
      private String attachmentName;
      private String outputTypeParam;

      private InternalSetResponseHeaderDelegate(final String attachmentName,
                                                final String outputTypeParam)
      {
        this.attachmentName = attachmentName;
        this.outputTypeParam = outputTypeParam;
      }

      public void setResponseHeader(final String mimeType)
      {
        if (attachmentName != null) {
          String fileName = attachmentName;
          if (attachmentName.indexOf(".") <= 0)
          {
            fileName = fileName + "." + outputTypeParam;
          }
          setResponseHeaders(getMimeType(fileName), fileName);
        }
        else{
            // Just set mime types
            setResponseHeaders(mimeType);
        }
      }
    }

    @Override
    public Log getLogger() {//TODO:?
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String getPluginName() {
      return "cgg";
    }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void Draw(final OutputStream out) {
        draw(out);
    }


    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void draw(final OutputStream out) {

      final HttpServletResponse response = (HttpServletResponse) parameterProviders.get("path").getParameter("httpresponse");
      try {

            IParameterProvider requestParams = getRequestParameters();
            String scriptName = requestParams.getStringParameter("script", "");
            String scriptTypeParam = requestParams.getStringParameter("type", "svg");
            String outputTypeParam = requestParams.getStringParameter("outputType", "png");

            final String attachmentName = requestParams.getStringParameter("attachmentName", null);
            InternalSetResponseHeaderDelegate delegate = new InternalSetResponseHeaderDelegate(attachmentName, outputTypeParam);

            Long width = requestParams.getLongParameter("width", 0L);
            Long height = requestParams.getLongParameter("height", 0L);
            logger.debug("Starting:" + new Date().getTime());
            
            final URL context = new File(RepositoryAccess.getSolutionPath(scriptName)).getParentFile().toURI().toURL();
            final WebCgg cgg = new WebCgg(context, response, out, delegate);
            cgg.draw(RepositoryAccess.getSolutionPath(scriptName), scriptTypeParam, outputTypeParam,
                width.intValue(), height.intValue(), buildParameterMap(requestParams));
        }
        catch (FileNotFoundException fe) {
            logger.error(fe);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        catch (Exception e) {
            logger.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

  private Map<String, Object> buildParameterMap(final IParameterProvider requestParams)
  {
    HashMap<String, Object> params = new HashMap<String, Object>();
    Iterator inputParams = requestParams.getParameterNames();
    while (inputParams.hasNext())
    {
      String paramName = inputParams.next().toString();
      if (paramName.startsWith("param"))
      {
        String pName = paramName.substring(5);
        Object[] p = requestParams.getArrayParameter(paramName, null);
        if (p.length == 1)
        { // not *really* an array, is it?
          params.put(pName, p[0]);
        }
        else
        {
          params.put(pName, p);
        }
      }
    }
    return params;
  }

    @Exposed(accessLevel = AccessLevel.PUBLIC)
    public void refresh(OutputStream out) {
    }

   
}
