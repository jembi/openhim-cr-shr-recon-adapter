package org.jembi.rhea.transformers;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.rhea.RestfulHttpRequest;
import org.jembi.rhea.RestfulHttpResponse;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.client.LocalMuleClient;
import org.mule.api.transformer.TransformerException;
import org.mule.api.transport.PropertyScope;
import org.mule.transformer.AbstractMessageTransformer;


/***
 * Orchestrates the individual messages and collects the responses, creating a single RestfulHTTPResponse.
 * 
 * The response HTTP status is set to 200 if all the requests were fine, and 500 if any one of the requests had issues.
 * The response body is set to a list of UUIDs for the individual request messages.
 */
public class IdentifierUpdateEventsRequestSplitterAggregator extends AbstractMessageTransformer {

	private final Log log = LogFactory.getLog(this.getClass());
	
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public Object transformMessage(MuleMessage msg, String outputEncoding)
			throws TransformerException {
		RestfulHttpResponse result = new RestfulHttpResponse();
		
		try {
			LocalMuleClient client = muleContext.getClient();

			List<RestfulHttpRequest> requests = ((List<RestfulHttpRequest>)msg.getPayload());
			StringBuilder listOfRequests = new StringBuilder();
			
			result.setUuid((String)msg.getProperty("uuid", PropertyScope.SESSION));
			result.setHttpStatus(200); //by default assume that everything's ok
			listOfRequests.append("Individual Request UUIDs:\n");
			
			for (RestfulHttpRequest request : requests) {
				MuleMessage vmResponse = client.send("vm://_identifierUpdateEventsOrchestration_IndividualRequest_queue", request, null);
				RestfulHttpResponse response = (RestfulHttpResponse)vmResponse.getPayload();
				
				if (response.getHttpStatus()<200 || response.getHttpStatus()>=300)
					result.setHttpStatus(500);
				listOfRequests.append(response.getUuid() + "\n");
			}
			result.setBody(listOfRequests.toString());
		} catch (MuleException e) {
			throw new TransformerException(this, e);
		}
		
		return result;
	}
}
