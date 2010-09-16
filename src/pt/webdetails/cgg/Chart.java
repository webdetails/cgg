/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;
import org.xml.sax.InputSource;

/**
 *
 * @author pdpi
 */
public class Chart {

    private Document svg;
    private String svgSource;

    public Chart(String svgSource) {
        this.svgSource = svgSource;
    }

    public void toPNG(OutputStream out) {
        PNGTranscoder t = new PNGTranscoder();
        TranscoderInput input = new TranscoderInput(new StringReader(svgSource));
        TranscoderOutput output = new TranscoderOutput(out);
        try {
            t.transcode(input, output);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Chart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TranscoderException ex) {
            Logger.getLogger(Chart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
