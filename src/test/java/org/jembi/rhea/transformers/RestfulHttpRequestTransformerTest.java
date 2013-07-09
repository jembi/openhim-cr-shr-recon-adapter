package org.jembi.rhea.transformers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jembi.rhea.RestfulHttpRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;


public class RestfulHttpRequestTransformerTest {

	protected MuleMessage mockMsg;
	
	@Before
	public void setUp() throws Exception {
		mockMsg = mock(MuleMessage.class);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTransformMessageMuleMessageString() throws Exception {
		setProp("request", "testRequest");
		setProp("method", "GET");
		when(mockMsg.getPayloadAsString()).thenReturn("testPayload");
		
		Object result = getTransformer().transformMessage(mockMsg, null);
		assertNotNull(result);
		assertTrue(result instanceof RestfulHttpRequest);
		
		RestfulHttpRequest req = (RestfulHttpRequest)result;
		assertEquals(req.getPath(), "testRequest");
		assertEquals(req.getHttpMethod(), "GET");
		assertEquals(req.getBody(), "testPayload");
	}
	
	@Test
	public void testTransformMessageMuleMessageString_Nulls() throws Exception {
		setProp("request", null);
		try {
			getTransformer().transformMessage(mockMsg, null);
			fail();
		} catch (TransformerException ex) {
			//expected
		}
		
		setProp("request", "testRequest");
		setProp("method", null);
		try {
			getTransformer().transformMessage(mockMsg, null);
			fail();
		} catch (TransformerException ex) {
			//expected
		}
		
		setProp("method", "GET");
		when(mockMsg.getPayloadAsString()).thenReturn(null);
		
		Object result = getTransformer().transformMessage(mockMsg, null);
		assertNotNull(result);
		assertTrue(result instanceof RestfulHttpRequest);
		
		RestfulHttpRequest req = (RestfulHttpRequest)result;
		assertNull(req.getBody());
	}
	
	@Test
	public void testTransformMessageMuleMessageString_BodyURLEqual() throws Exception {
		setProp("request", "testRequest");
		setProp("method", "GET");
		when(mockMsg.getPayloadAsString()).thenReturn("testRequest");
		
		Object result = getTransformer().transformMessage(mockMsg, null);
		assertNotNull(result);
		assertTrue(result instanceof RestfulHttpRequest);
		
		RestfulHttpRequest req = (RestfulHttpRequest)result;
		assertEquals(req.getPath(), "testRequest");
		assertEquals(req.getHttpMethod(), "GET");
		assertTrue(!"testRequest".equals(req.getBody()));
	}

	protected void setProp(String key, String value) {
		when(mockMsg.getInvocationProperty("rest." + key)).thenReturn(value);
	}

	protected AbstractMessageTransformer getTransformer() {
		return new RestfulHttpRequestTransformer();
	}
}
