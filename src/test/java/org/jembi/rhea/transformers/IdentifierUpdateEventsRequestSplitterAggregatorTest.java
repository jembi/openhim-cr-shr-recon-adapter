package org.jembi.rhea.transformers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jembi.rhea.RestfulHttpRequest;
import org.jembi.rhea.RestfulHttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.api.transport.PropertyScope;

public class IdentifierUpdateEventsRequestSplitterAggregatorTest {

	private MuleMessage msgMock;
	private MuleContext ctxMock;
	private LocalMuleClient clientMock;
	
	@Before
	public void setUp() throws Exception {
		msgMock = mock(MuleMessage.class);
		ctxMock = mock(MuleContext.class);
		clientMock = mock(LocalMuleClient.class);
		
		when(msgMock.getProperty("uuid", PropertyScope.SESSION)).thenReturn("SESSION-UUID");
		when(ctxMock.getClient()).thenReturn(clientMock);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@SuppressWarnings({ "unchecked" })
	private List<RestfulHttpRequest> buildMockRequests(int numRequests, int status) throws MuleException {
		List<RestfulHttpRequest> requests = new ArrayList<RestfulHttpRequest>();
		
		for (int i=0; i<numRequests; i++) {
			RestfulHttpRequest req = mock(RestfulHttpRequest.class);
			requests.add(req);
			
			MuleMessage msgResponseMock = mock(MuleMessage.class);
			RestfulHttpResponse responseMock = mock(RestfulHttpResponse.class);
			when(msgResponseMock.getPayload()).thenReturn(responseMock);
			when(responseMock.getHttpStatus()).thenReturn(status);
			when(responseMock.getUuid()).thenReturn(Integer.toString(i));
			when(req.getUuid()).thenReturn(Integer.toString(i));
			when(
				clientMock.send(eq("vm://_identifierUpdateEventsOrchestration_IndividualRequest_queue"),
				argThat(new ResponseMatcher(Integer.toString(i))),
				anyMap())
			).thenReturn(msgResponseMock);
		}
		
		return requests;
	}

	@Test
	public void testTransformMessageMuleMessageString() throws MuleException {
		IdentifierUpdateEventsRequestSplitterAggregator transformer = new IdentifierUpdateEventsRequestSplitterAggregator();
		transformer.setMuleContext(ctxMock);
		List<RestfulHttpRequest> requests = buildMockRequests(3, 200);
		when(msgMock.getPayload()).thenReturn(requests);
		
		Object result = transformer.transformMessage(msgMock, null);
		assertNotNull(result);
		assertTrue(result instanceof RestfulHttpResponse);
		assertEquals(((RestfulHttpResponse)result).getHttpStatus(), 200);
		assertEquals(((RestfulHttpResponse)result).getUuid(), "SESSION-UUID");
		assertEquals(((RestfulHttpResponse)result).getBody(), "Individual Request UUIDs:\n0\n1\n2\n");
	}

	@Test
	public void testTransformMessageMuleMessageString_NoRequests() throws MuleException {
		IdentifierUpdateEventsRequestSplitterAggregator transformer = new IdentifierUpdateEventsRequestSplitterAggregator();
		transformer.setMuleContext(ctxMock);
		List<RestfulHttpRequest> requests = Collections.emptyList();
		when(msgMock.getPayload()).thenReturn(requests);
		
		Object result = transformer.transformMessage(msgMock, null);
		assertNotNull(result);
		assertTrue(result instanceof RestfulHttpResponse);
		assertEquals(((RestfulHttpResponse)result).getHttpStatus(), 200);
		assertEquals(((RestfulHttpResponse)result).getUuid(), "SESSION-UUID");
		assertEquals(((RestfulHttpResponse)result).getBody(), "Individual Request UUIDs:\n");
	}

	@Test
	public void testTransformMessageMuleMessageString_Errors() throws MuleException {
		IdentifierUpdateEventsRequestSplitterAggregator transformer = new IdentifierUpdateEventsRequestSplitterAggregator();
		transformer.setMuleContext(ctxMock);
		List<RestfulHttpRequest> requests = buildMockRequests(3, 404);
		when(msgMock.getPayload()).thenReturn(requests);
		
		Object result = transformer.transformMessage(msgMock, null);
		assertNotNull(result);
		assertTrue(result instanceof RestfulHttpResponse);
		assertEquals(((RestfulHttpResponse)result).getHttpStatus(), 500);
		assertEquals(((RestfulHttpResponse)result).getUuid(), "SESSION-UUID");
		assertEquals(((RestfulHttpResponse)result).getBody(), "Individual Request UUIDs:\n0\n1\n2\n");
	}

	
	private static class ResponseMatcher extends ArgumentMatcher<RestfulHttpRequest> {
		String uuid;
		
		private ResponseMatcher(String uuid) {
			this.uuid = uuid;
		}
		
		@Override
		public boolean matches(Object arg0) {
			return uuid.equals(((RestfulHttpRequest)arg0).getUuid());
		}
		
	}
}
