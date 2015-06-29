/*!
* Copyright 2002 - 2013 Webdetails, a Pentaho company.  All rights reserved.
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

package pt.webdetails.cgg;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.w3c.dom.Document;
import pt.webdetails.cpf.utils.CharsetHelper;

/**
 * @author pdpi
 */
public class SVGChart implements Chart
{
  private Document svg;

  public SVGChart(final Document doc)
  {
    if (doc == null)
    {
      throw new NullPointerException();
    }
    this.svg = doc;
  }

  public Document getRawObject()
  {
    return svg;
  }

  public void renderAsPng(final OutputStream out) throws IOException
  {
    final PNGTranscoder t = new PNGTranscoder();
    final TranscoderInput input = new TranscoderInput(svg);
    final TranscoderOutput output = new TranscoderOutput(out);
    try
    {
      t.transcode(input, output);
      out.flush();
    }
    catch (TranscoderException ex)
    {
      throw new IOException("Failed to transcode image", ex);
    }
  }

  public void renderAsSVG(final OutputStream out) throws IOException
  {
    final SVGTranscoder t = new SVGTranscoder();
    final TranscoderInput input = new TranscoderInput(svg);
    final TranscoderOutput output = new TranscoderOutput( new OutputStreamWriter( out, CharsetHelper.getEncoding() ) );

    try
    {
      t.transcode(input, output);
      out.flush();
    }
    catch (TranscoderException ex)
    {
      throw new IOException("Failed to transcode image", ex);
    }
  }
}
