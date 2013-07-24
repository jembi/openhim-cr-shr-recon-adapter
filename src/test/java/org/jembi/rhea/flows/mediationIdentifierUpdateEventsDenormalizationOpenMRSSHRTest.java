package org.jembi.rhea.flows;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jembi.Util;
import org.jembi.rhea.RestfulHttpRequest;
import org.jembi.rhea.RestfulHttpResponse;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.junit4.FunctionalTestCase;

import com.github.tomakehurst.wiremock.junit.WireMockRule;


public class mediationIdentifierUpdateEventsDenormalizationOpenMRSSHRTest extends FunctionalTestCase {
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(8003);

	
	@Override
	protected String getConfigResources() {
		return "src/main/app/identifierupdateevents-denormalization-openmrsshr.xml";
	}
	
	@Override
	protected void doSetUp() throws Exception {
		Logger.getRootLogger().setLevel(Level.INFO);
		super.doSetUp();
	}

	@Override
	protected void doTearDown() throws Exception {
		Logger.getRootLogger().setLevel(Level.WARN);
		super.doTearDown();
	}
	
	
	private void setupWebserviceStub(int httpStatus) {
		stubFor(put(urlEqualTo("/openmrs/ws/rest/RHEA/patient/identifier"))
		    	.willReturn(aResponse()
		    		.withStatus(httpStatus)));
	}

	@Test
	public void identifierUpdateEventsDenormalization() throws IOException, MuleException{
		runTest(200);
	}
	
	@Test
	public void identifierUpdateEventsDenormalization_NotFound() throws IOException, MuleException{
		runTest(404);
	}
	
	private void runTest(int status) throws IOException, MuleException {
		setupWebserviceStub(status);
		
		Map<String, Object> properties = null;
		RestfulHttpRequest req = new RestfulHttpRequest();
		req.setHttpMethod(RestfulHttpRequest.HTTP_PUT);
		req.setPath("openmrs/ws/rest/v1/patient/identifier");
		req.setBody(Util.getResourceAsString("OpenEMPIIdentifierUpdateEvent.xml"));
		
		MuleClient client = new MuleClient(muleContext);
		MuleMessage result = client.send("vm://identifierUpdateEventsDenormalizationQueue", (Object)req, properties);
		
		verify(putRequestedFor(urlEqualTo("/openmrs/ws/rest/RHEA/patient/identifier")));
		Assert.assertTrue(result.getPayload() instanceof RestfulHttpResponse);
		RestfulHttpResponse resp = (RestfulHttpResponse) result.getPayload();
	    assertEquals(status, resp.getHttpStatus());
	}
}
