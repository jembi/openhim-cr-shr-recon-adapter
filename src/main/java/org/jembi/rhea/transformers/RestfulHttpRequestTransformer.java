/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea.transformers;

import org.jembi.rhea.RestfulHttpRequest;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

/***
 * Transforms the current message into a RestfulHttpRequest
 * 
 * Expects the invocation properties 'rest.request' and 'rest.method'
 */
public class RestfulHttpRequestTransformer extends
		AbstractMessageTransformer {

	@Override
	public Object transformMessage(MuleMessage msg, String enc) throws TransformerException {
		
		RestfulHttpRequest restMsg = new RestfulHttpRequest();
		
		Object url = msg.getInvocationProperty("rest.request");
		if (url!=null && url instanceof String)
			restMsg.setPath((String)url);
		else
			throw new TransformerException(this, new InvocationPropertyNotSetException());
		
		Object httpMethod = msg.getInvocationProperty("rest.method");
		if (httpMethod!=null && httpMethod instanceof String)
			restMsg.setHttpMethod((String)httpMethod);
		else
			throw new TransformerException(this, new InvocationPropertyNotSetException());
		
		try {
			String body = msg.getPayloadAsString();
			if (body != url) {
				restMsg.setBody(body);
			}
		} catch (Exception e) {
			throw new TransformerException(this, e);
		}
		
		return restMsg;
	}

	public static class InvocationPropertyNotSetException extends Exception {
		private static final long serialVersionUID = -8518334981694624366L;
	}
}
