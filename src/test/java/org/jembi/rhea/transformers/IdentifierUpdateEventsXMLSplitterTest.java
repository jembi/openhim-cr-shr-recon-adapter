package org.jembi.rhea.transformers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;

import org.jembi.Util;
import org.jembi.rhea.RestfulHttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;

public class IdentifierUpdateEventsXMLSplitterTest {
	
	private MuleMessage msgMock;

	@Before
	public void setUp() throws Exception {
		msgMock = mock(MuleMessage.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	/***
	 * Transformer should split the identifierUpdateEvent nodes into seperate xml documents
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testTransformMessageMuleMessageString() throws Exception {
		when(msgMock.getPayloadAsString()).thenReturn(Util.getResourceAsString("OpenEMPIIdentifierUpdateEvents.xml"));
		String expectedResult = Util.getResourceAsString("OpenEMPIIdentifierUpdateEvents_Split.xml");
		
		IdentifierUpdateEventsXMLSplitter splitter = new IdentifierUpdateEventsXMLSplitter();
		Object result = splitter.transformMessage(msgMock, null);
		assertNotNull(result);
		assertTrue(result instanceof List);
		
		StringBuilder cat = new StringBuilder();
		for (String msg : (List<String>)result) {
			cat.append(msg);
		}
		assertEquals(Util.trimXML(expectedResult), Util.trimXML(cat.toString()));
	}

	/***
	 * Transformer should return an empty list when there are no events in the message
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testTransformMessageMuleMessageString_NoEvents() throws Exception {
		when(msgMock.getPayloadAsString()).thenReturn("<?xml version=\"1.0\" encoding=\"UTF-8\"?><identifierUpdateEvents></identifierUpdateEvents>");
		
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
		when(msgMock.getPayloadAsString()).thenReturn("Not a valid XML document!");
		
		IdentifierUpdateEventsXMLSplitter splitter = new IdentifierUpdateEventsXMLSplitter();
		try {
			splitter.transformMessage(msgMock, null);
			fail("Failed to throw exception for invalid document");
		} catch (TransformerException ex) {
			//expected
		}
	}
}
