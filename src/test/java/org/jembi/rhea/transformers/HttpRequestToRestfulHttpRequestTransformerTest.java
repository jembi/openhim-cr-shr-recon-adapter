package org.jembi.rhea.transformers;

import static org.mockito.Mockito.*;

import org.mule.transformer.AbstractMessageTransformer;

public class HttpRequestToRestfulHttpRequestTransformerTest extends RestfulHttpRequestTransformerTest {

	protected void setProp(String key, String value) {
		when(mockMsg.getInboundProperty("http." + key)).thenReturn(value);
		when(mockMsg.getInvocationProperty("rest." + key)).thenReturn(value);
	}
	
	protected AbstractMessageTransformer getTransformer() {
		return new HttpRequestToRestfulHttpRequestTransformer();
	}
}
