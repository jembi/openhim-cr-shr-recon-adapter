package org.jembi.rhea.transformers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.Util;
import org.jembi.rhea.RestfulHttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class IdentifierUpdateEventsXMLSplitterTest {
	
	private MuleMessage msgMock;
	private RestfulHttpRequest reqMock;

	@Before
	public void setUp() throws Exception {
		msgMock = mock(MuleMessage.class);
		reqMock = mock(RestfulHttpRequest.class);
		when(reqMock.getPath()).thenReturn("testPath");
		when(reqMock.getHttpMethod()).thenReturn("PUT");
		when(msgMock.getPayload()).thenReturn(reqMock);
	}

	@After
	public void tearDown() throws Exception {
	}

	/***
	 * Transformer should split the identifierUpdateEvent nodes into separate xml documents
	 */
	@SuppressWarnings({ "rawtypes" })
	@Test
	public void testTransformMessageMuleMessageString() throws Exception {
		when(reqMock.getBody()).thenReturn(Util.getResourceAsString("OpenEMPIIdentifierUpdateEvents.xml"));
		String expectedResult = Util.getResourceAsString("OpenEMPIIdentifierUpdateEvents_Split.xml");
		IdentifierUpdateEventsXMLSplitter splitter = new IdentifierUpdateEventsXMLSplitter();
		Object result = splitter.transformMessage(msgMock, null);
		assertNotNull(result);
		assertTrue(result instanceof List);
		
		StringBuilder cat = new StringBuilder();
		for (Object msg : (List)result) {
			assertTrue(msg instanceof RestfulHttpRequest);
			cat.append(((RestfulHttpRequest)msg).getBody() + "\n");
		}
		assertEquals(Util.trimXML(expectedResult), Util.trimXML(cat.toString()));
	}

	/***
	 * Transformer should return an empty list when there are no events in the message
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testTransformMessageMuleMessageString_NoEvents() throws Exception {
		when(reqMock.getBody()).thenReturn("<?xml version=\"1.0\" encoding=\"UTF-8\"?><identifierUpdateEvents></identifierUpdateEvents>");
		
		IdentifierUpdateEventsXMLSplitter splitter = new IdentifierUpdateEventsXMLSplitter();
		Object result = splitter.transformMessage(msgMock, null);
		assertNotNull(result);
		assertTrue(result instanceof List);
		assertTrue(((List)result).isEmpty());
	}

	/***
	 * Transformer should throw an exception if the message is not a valid xml document
	 */
	@Test
	public void testTransformMessageMuleMessageString_Invalid() throws Exception {
		when(reqMock.getBody()).thenReturn("Not a valid XML document!");
		
		IdentifierUpdateEventsXMLSplitter splitter = new IdentifierUpdateEventsXMLSplitter();
		try {
			splitter.transformMessage(msgMock, null);
			fail("Failed to throw exception for invalid document");
		} catch (TransformerException ex) {
			//expected
		}
	}
	
	
	
}
