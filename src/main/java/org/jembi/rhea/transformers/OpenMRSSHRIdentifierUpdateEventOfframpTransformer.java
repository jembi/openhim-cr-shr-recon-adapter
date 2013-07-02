package org.jembi.rhea.transformers;

import org.jembi.rhea.RestfulHttpRequest;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

public class OpenMRSSHRIdentifierUpdateEventOfframpTransformer extends AbstractMessageTransformer {
	
	private static final String PATH = "openmrs/ws/rest/RHEA/patient/identifier";

	@Override
	public Object transformMessage(MuleMessage msg, String outputEncoding)
			throws TransformerException {
		RestfulHttpRequest request = (RestfulHttpRequest) msg.getPayload();
		request.setPath(PATH);
		return msg;
	}

}
