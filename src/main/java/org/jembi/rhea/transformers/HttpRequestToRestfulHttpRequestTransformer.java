/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea.transformers;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;

public class HttpRequestToRestfulHttpRequestTransformer extends
		RestfulHttpRequestTransformer {

	@Override
	public Object transformMessage(MuleMessage msg, String enc) throws TransformerException {
		msg.setInvocationProperty("rest.request", msg.getInboundProperty("http.request"));
		msg.setInvocationProperty("rest.request", msg.getInboundProperty("http.method"));
		return super.transformMessage(msg, enc);
	}

}
