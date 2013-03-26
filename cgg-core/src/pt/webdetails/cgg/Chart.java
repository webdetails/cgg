package pt.webdetails.cgg;

import java.io.IOException;
import java.io.OutputStream;

public interface Chart
{
  public void renderAsPng(OutputStream out) throws IOException;
  public void renderAsSVG(OutputStream out) throws IOException;
}
