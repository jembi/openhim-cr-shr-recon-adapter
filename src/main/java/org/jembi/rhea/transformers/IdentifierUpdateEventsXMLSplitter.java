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
 * Splits the identifierUpdateEvent elements in an identifierUpdateEvents XML
 * message into separate items.
 */
public class IdentifierUpdateEventsXMLSplitter extends
		AbstractMessageTransformer {
	private static final String UPDATES_ROOT_ELEMENT = "identifierUpdateEvents";
	private static final String UPDATES_ITEM_ELEMENT = "identifierUpdateEvent";

	@Override
	public Object transformMessage(MuleMessage msg, String outputEncoding)
			throws TransformerException {
		List<RestfulHttpRequest> result = new ArrayList<RestfulHttpRequest>();
		RestfulHttpRequest origReq = ((RestfulHttpRequest) msg.getPayload());

		try {
			String updates = origReq.getBody();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc = dbf.newDocumentBuilder().parse(
					new InputSource(new StringReader(updates)));
			XPath xpath = XPathFactory.newInstance().newXPath();

			NodeList nodes = (NodeList) xpath.evaluate(String.format("/%s/%s",
					UPDATES_ROOT_ELEMENT, UPDATES_ITEM_ELEMENT), doc,
					XPathConstants.NODESET);
			Document output = dbf.newDocumentBuilder().newDocument();
			Boolean postUpdateIdfHasChild = false;
			Boolean preUpdateIdfHasChild = false;

			for (int i = 0; i < nodes.getLength(); i++) {
				
				//Looping through all child nodes of identifierUpdateEvent Node
				Node item = output.importNode(nodes.item(i), true);
				for (int j = 0; j < item.getChildNodes().getLength(); j++) {
				    //Check if there is Node with name postUpdateIdentifiers
					if (item.getChildNodes().item(j).getNodeName().equals("postUpdateIdentifiers")) {
						//If a node with Name postUpdateIdentifiers is found get its child nodes in postUpdateIdfsChildNodes variable
						NodeList postUpdateIdfsChildNodes = item.getChildNodes().item(j).getChildNodes();
						         item.getNodeName();
						//Looping through all child Nodes of the Child of postUpdateIdentifiers
						for (int k = 0; k < postUpdateIdfsChildNodes.getLength(); k++) {
							//Get each child node from the main output file
							Node postUpdateIdfsChildItem = postUpdateIdfsChildNodes.item(k);
							//If a node with name postUpdateIdentifier was found among child of postUpdateIdentifiers Node,check if it has child of its own
							if (postUpdateIdfsChildItem.getNodeName().equals("postUpdateIdentifier")) {
								if (postUpdateIdfsChildItem.hasChildNodes()){
									postUpdateIdfHasChild = true;
									break;
								}
							}	
						}
					}					
					//Check if there is Node with name preUpdateIdentifiers
					if (item.getChildNodes().item(j).getNodeName().equals("preUpdateIdentifiers")) {
						//If a node with Name preUpdateIdentifiers is found get its child nodes in preUpdateIdfsChildNodes variable
						NodeList preUpdateIdfsChildNodes = item.getChildNodes().item(j).getChildNodes();
						//Looping through all childs Nodes of the Child of preUpdateIdentifiers
						for (int m = 0; m < preUpdateIdfsChildNodes.getLength(); m++) {
							//Get each child node from the main output file
							Node preUpdateIdfsChildItem =preUpdateIdfsChildNodes.item(m);
							//If a node with name preUpdateIdentifier was found among child of preUpdateIdentifiers Node,check if it has child of its own
							if (preUpdateIdfsChildItem.getNodeName().equals("preUpdateIdentifier")) {
								if (preUpdateIdfsChildItem.hasChildNodes()){
									preUpdateIdfHasChild = true;
									break;
								}
								     
							}						
						}

					}
				}
				Transformer transformer = TransformerFactory.newInstance()
						.newTransformer();
				StringWriter sw = new StringWriter();
				transformer
						.transform(new DOMSource(item), new StreamResult(sw));
				if (postUpdateIdfHasChild && preUpdateIdfHasChild){
					 result.add(buildRestfulRequest(sw.toString(),origReq.getPath(), origReq.getHttpMethod()));
					 postUpdateIdfHasChild = false;
				     preUpdateIdfHasChild = false;

				
				}
				
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
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}

		return result;
	}

	private RestfulHttpRequest buildRestfulRequest(String payload, String path,
			String method) {
		RestfulHttpRequest restMsg = new RestfulHttpRequest();
		restMsg.setPath(path);
		restMsg.setHttpMethod(method);
		restMsg.setBody(payload);
		return restMsg;
	}
}
