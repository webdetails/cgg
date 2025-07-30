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

package pt.webdetails.cgg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pt.webdetails.cgg.output.PngOutputHandler;
import pt.webdetails.cgg.output.SVGOutputHandler;
import pt.webdetails.cpf.utils.CharsetHelper;

import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

/**
 * @author pdpi
 */
public class SVGChart implements Chart {
  private Document svg;
  private boolean isMultiPage;

  public SVGChart( final Document doc, final boolean isMultiPage ) {
    if ( doc == null ) {
      throw new NullPointerException();
    }
    this.svg = doc;
    this.isMultiPage = isMultiPage;
  }

  public Document getRawObject() {
    return svg;
  }

  public void renderAsPng( final OutputStream out ) throws IOException {
    if ( this.isMultiPage ) {
      renderPages( out, false );
    } else {
      renderPageAsPng( out, svg );
    }
  }

  public void renderAsSVG( final OutputStream out ) throws IOException {
    if ( this.isMultiPage ) {
      renderPages( out, true );
    } else {
      renderPageAsSVG( out, svg );
    }
  }

  private void renderPages( final OutputStream out, boolean isAsSvg ) throws IOException {
    // Each child element of svg's document element represents a page.
    // Remove all but one of those children and render each page.
    Element rootElem = svg.getDocumentElement();
    NodeList pageNodeList = rootElem.getChildNodes();
    int pageCount = pageNodeList.getLength();

    // Backup page nodes in a node array.
    Node[] pageNodes = new Node[pageCount];
    for ( int pageIndex = 0; pageIndex < pageCount; pageIndex++ ) {
      pageNodes[pageIndex] = pageNodeList.item( pageIndex );
    }

    // Remove all page nodes.
    while ( rootElem.getLastChild() != null ) {
      rootElem.removeChild( rootElem.getLastChild() );
    }

    // Create a "mixed" multipart.
    MimeMultipart multiPart = new MimeMultipart();

    try {
      // Now add one child node in turn.
      for ( int pageIndex = 0; pageIndex < pageCount; pageIndex++ ) {
        rootElem.appendChild( pageNodes[pageIndex] );

        ByteArrayOutputStream pageOutputStream = new ByteArrayOutputStream();
        if ( isAsSvg ) {
          renderPageAsSVG( pageOutputStream, svg );
        } else {
          renderPageAsPng( pageOutputStream, svg );
        }

        MimeBodyPart bodyPart = new MimeBodyPart();

        String mime_type = isAsSvg ? SVGOutputHandler.MIME_SVG : PngOutputHandler.MIME_PNG;

        bodyPart.setDataHandler( new DataHandler( new ByteArrayDataSource( pageOutputStream.toByteArray(), mime_type ) ) );
        bodyPart.setHeader( "Content-Type", mime_type );
        bodyPart.setFileName( "export_" + pageIndex + ( isAsSvg ? ".svg" : ".png" ) );

        multiPart.addBodyPart( bodyPart );

        rootElem.removeChild( pageNodes[pageIndex] );
      }

      multiPart.writeTo( out );

    } catch ( MessagingException ex ) {
      throw new IOException( "Cannot render page", ex );
    }
  }

  private void renderPageAsPng( final OutputStream out, final Document pageSvg ) throws IOException {
    final PNGTranscoder t = new PNGTranscoder();
    final TranscoderInput input = new TranscoderInput( pageSvg );
    final TranscoderOutput output = new TranscoderOutput( out );
    try {
      t.transcode( input, output );
      out.flush();
    } catch ( TranscoderException ex ) {
      throw new IOException( "Failed to transcode image", ex );
    }
  }

  private void renderPageAsSVG( final OutputStream out, final Document pageSvg ) throws IOException {
    final SVGTranscoder t = new SVGTranscoder();
    final TranscoderInput input = new TranscoderInput( pageSvg );
    final TranscoderOutput output = new TranscoderOutput( new OutputStreamWriter( out, CharsetHelper.getEncoding() ) );

    try {
      t.transcode( input, output );
      out.flush();
    } catch ( TranscoderException ex ) {
      throw new IOException( "Failed to transcode image", ex );
    }
  }
}
