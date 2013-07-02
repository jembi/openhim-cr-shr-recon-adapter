package org.jembi.rhea.transformers;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.rhea.RestfulHttpRequest;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/***
 * Splits the identifierUpdateEvent elements in an identifierUpdateEvents XML message into separate items.
 */
public class IdentifierUpdateEventsXMLSplitter extends AbstractMessageTransformer {

	private final Log log = LogFactory.getLog(this.getClass());
	
	private static final String UPDATES_ROOT_ELEMENT = "identifierUpdateEvents";
	private static final String UPDATES_ITEM_ELEMENT = "identifierUpdateEvent";
	
	@Override
	public Object transformMessage(MuleMessage msg, String outputEncoding)
			throws TransformerException {
		RestfulHttpRequest request = (RestfulHttpRequest) msg.getPayload();
		String updates = request.getBody();
		List<String> result = new ArrayList<String>();
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc = dbf.newDocumentBuilder().parse(new InputSource(new StringReader(updates)));
			XPath xpath = XPathFactory.newInstance().newXPath();
			
			NodeList nodes = (NodeList) xpath.evaluate(String.format("/%s/%s", UPDATES_ROOT_ELEMENT, UPDATES_ITEM_ELEMENT), doc, XPathConstants.NODESET);
			Document output = dbf.newDocumentBuilder().newDocument();
			for (int i=0; i<nodes.getLength(); i++) {
				Node root = output.createElement(UPDATES_ROOT_ELEMENT);
				Node item = output.importNode(nodes.item(i), true);
				root.appendChild(item);
				nodes.item(i);
				
				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				StringWriter sw = new StringWriter();
				transformer.transform(new DOMSource(root), new StreamResult(sw));
				result.add(sw.toString());
			}
		} catch (SAXException e) {
			throw new TransformerException(this, e);
		} catch (IOException e) {
			throw new TransformerException(this, e);
		} catch (ParserConfigurationException e) {
			throw new TransformerException(this, e);
		} catch (XPathExpressionException e) {
			throw new TransformerException(this, e);
		} catch (TransformerConfigurationException e) {
			throw new TransformerException(this, e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new TransformerException(this, e);
		} catch (javax.xml.transform.TransformerException e) {
			throw new TransformerException(this, e);
		}
		
		return result;
	}

}
