/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.webdetails.cgg.cdw;

import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IFileInfo;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.SolutionFileMetaAdapter;
import org.pentaho.platform.engine.core.solution.FileInfo;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author pdpi
 */
public class CdwMetadataProvider extends SolutionFileMetaAdapter {

    private Log logger = LogFactory.getLog(CdwMetadataProvider.class);

    public IFileInfo getFileInfo(ISolutionFile solutionFile, InputStream in) {
        Document doc = null;
        try {

            StringBuilder xmlBuilder = new StringBuilder();
            int charRead;
            while ((charRead = in.read()) != -1) {
                xmlBuilder.appendCodePoint(charRead);
            }
            String xml = xmlBuilder.toString().trim().replaceFirst("^([\\W]+)<","<");
            
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xml)));

            String result = "chart";

            XPath xpath = XPathFactory.newInstance().newXPath();
            String author = (String) xpath.evaluate("/cdw/metadata/author", doc, XPathConstants.STRING);
            String description = (String) xpath.evaluate("/cdw/metadata/description", doc, XPathConstants.STRING);
            String icon = (String) xpath.evaluate("/cdw/metadata/icon", doc, XPathConstants.STRING);
            String title = (String) xpath.evaluate("/cdw/metadata/title", doc, XPathConstants.STRING);

            IFileInfo info = new FileInfo();
            info.setAuthor(author);
            info.setDescription(description);
            info.setDisplayType(result);
            info.setIcon(icon);
            info.setTitle(title);
            return info;

        } catch (Exception e) {
            logger.error(e);
            return null;
        }
    }
}
