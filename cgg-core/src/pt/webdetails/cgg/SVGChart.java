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

  public Document getSvg()
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
    final TranscoderOutput output = new TranscoderOutput(new OutputStreamWriter(out));

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
