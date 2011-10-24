/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.platform.camel.ihe.hl7v3.iti55;

import groovy.util.slurpersupport.GPathResult;
import org.apache.camel.Exchange;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3Utils;
import org.openehealth.ipf.commons.ihe.hl7v3.iti55.Iti55PortType;
import org.openehealth.ipf.commons.ihe.hl7v3.iti55.Iti55Utils;
import org.openehealth.ipf.commons.ihe.ws.JaxWsClientFactory;
import org.openehealth.ipf.commons.ihe.ws.correlation.AsynchronyCorrelator;
import org.openehealth.ipf.commons.xml.XmlUtils;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsEndpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsProducer;

import javax.xml.ws.BindingProvider;
import java.util.Map;

/**
 * Camel producer for the ITI-55 XCPD transaction
 * with support of the Deferred Response option.
 * @author Dmytro Rud
 */
class Iti55Producer extends AbstractWsProducer {
    private static final String PROCESSING_MODE_PROPERTY = Iti55Producer.class.getName() + ".MODE";

    Iti55Producer(AbstractWsEndpoint endpoint, JaxWsClientFactory clientFactory) {
        super(endpoint, clientFactory, Object.class);
    }


    @Override
    protected void enrichRequestContext(Exchange exchange, WrappedMessageContext requestContext) {
        GPathResult requestXml = requestXml(exchange);
        String processingMode = Iti55Utils.processingMode(requestXml);

        if ("D".equals(processingMode)) {
            if (Iti55Utils.deferredResponseUri(requestXml) == null) {
                throw new RuntimeException("missing deferred response URI in the request");
            }
            requestContext.put(AsynchronyCorrelator.FORCE_CORRELATION, Boolean.TRUE);
        }
        requestContext.put(PROCESSING_MODE_PROPERTY, processingMode);
    }


    @Override
    protected Object callService(Object client, Object request) {
        BindingProvider bindingProvider = (BindingProvider) client;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();

        return ("D".equals(requestContext.get(PROCESSING_MODE_PROPERTY))) ?
            ((Iti55PortType) client).discoverPatientsDeferred(request) :
            ((Iti55PortType) client).discoverPatients(request);
    }


    @Override
    protected String[] getAlternativeRequestKeys(Exchange exchange) {
        GPathResult requestXml = requestXml(exchange);
        return new String[] { Iti55Utils.requestQueryId(requestXml) };
    }


    private static GPathResult requestXml(Exchange exchange) {
        String encoding = exchange.getProperty(Exchange.CHARSET_NAME, String.class);
        String requestString = XmlUtils.toString(exchange.getIn().getBody(), encoding);
        return Hl7v3Utils.slurp(requestString);
    }
}
