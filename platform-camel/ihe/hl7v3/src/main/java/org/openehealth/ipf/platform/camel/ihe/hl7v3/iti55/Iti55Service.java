<<<<<<< HEAD
/*
 * Copyright 2010 the original author or authors.
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

import org.apache.camel.Exchange;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3Exception;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3NakFactory;
import org.openehealth.ipf.commons.ihe.hl7v3.iti55.Iti55PortType;
import org.openehealth.ipf.commons.xml.XmlUtils;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;

/**
 * Service implementation for the IHE ITI-55 transaction (XCPD).
 * @author Dmytro Rud
 */
public class Iti55Service extends DefaultItiWebService implements Iti55PortType {

    @Override
    public Object respondingGatewayPRPAIN201305UV02(Object request) {
        try {
            Exchange result = process(request);
            if(result.getException() != null) {
                throw result.getException();
            }
            return prepareBody(result);
        } catch (Exception e) {
            Hl7v3Exception hl7v3Exception = new Hl7v3Exception(e, null);
            hl7v3Exception.setDetectedIssueManagementCode("InternalError");
            hl7v3Exception.setDetectedIssueManagementCodeSystem("1.3.6.1.4.1.19376.1.2.27.3");
            return Hl7v3NakFactory.createNak(XmlUtils.toString(request, null), hl7v3Exception, "PRPA_IN201306UV02", true);
        }
    }

}
=======
/*
 * Copyright 2010 the original author or authors.
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
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.spi.ExecutorServiceStrategy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.context.WebServiceContextImpl;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AddressingPropertiesImpl;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3Exception;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3Utils;
import org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3WsTransactionConfiguration;
import org.openehealth.ipf.commons.ihe.hl7v3.iti55.Iti55PortType;
import org.openehealth.ipf.commons.ihe.hl7v3.iti55.Iti55Utils;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.AbstractAuditInterceptor;
import org.openehealth.ipf.commons.ihe.ws.cxf.audit.WsAuditDataset;
import org.openehealth.ipf.platform.camel.ihe.hl7v3.AbstractHl7v3WebService;
import org.openehealth.ipf.platform.camel.ihe.hl7v3.Hl7v3Endpoint;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWsEndpoint;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static org.apache.cxf.ws.addressing.JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_INBOUND;
import static org.apache.cxf.ws.addressing.JAXWSAConstants.SERVER_ADDRESSING_PROPERTIES_OUTBOUND;
import static org.openehealth.ipf.commons.ihe.hl7v3.Hl7v3NakFactory.response;
import static org.openehealth.ipf.platform.camel.ihe.hl7v3.iti55.deferredresponse.Iti55DeferredResponseComponent.THREAD_POOL_NAME;

/**
 * Service implementation for the Responding Gateway actor
 * of the IHE ITI-55 transaction (XCPD)
 * with support for the Deferred Response option.
 *
 * @author Dmytro Rud
 */
public class Iti55Service extends AbstractHl7v3WebService implements Iti55PortType {
    private static final transient Log LOG = LogFactory.getLog(Iti55Service.class);

    private final ProducerTemplate producerTemplate;
    private final ExecutorService executorService;
    private final Hl7v3Endpoint<Hl7v3WsTransactionConfiguration> endpoint;
    private final CamelContext camelContext;


    /**
     * Constructor.
     * @param endpoint
     *      Camel endpoint instance this Web Service corresponds to.
     */
    Iti55Service(Hl7v3Endpoint<Hl7v3WsTransactionConfiguration> endpoint) {
        super(endpoint.getComponent().getWsTransactionConfiguration());
        this.endpoint = endpoint;
        this.camelContext = endpoint.getCamelContext();
        this.producerTemplate = this.camelContext.createProducerTemplate();

        ExecutorServiceStrategy executorServiceStrategy = this.camelContext.getExecutorServiceStrategy();
        ExecutorService executorService0 = executorServiceStrategy.lookup(this, THREAD_POOL_NAME, THREAD_POOL_NAME);
        this.executorService = (executorService0 != null)
                ? executorService0
                : executorServiceStrategy.newThreadPool(this, THREAD_POOL_NAME, 10, 100);
    }


    @Override
    public Object discoverPatients(Object requestString) {
        return doProcess(requestString);
    }

    @Override
    public Object discoverPatientsDeferred(Object requestString) {
        return doProcess(requestString);
    }


    @Override
    protected Object doProcess(Object request) {
        final String requestString = (String) request;
        final GPathResult requestXml = Hl7v3Utils.slurp(requestString);
        final String processingMode = Iti55Utils.processingMode(requestXml);

        // process regular requests in a synchronous route
        if ("I".equals(processingMode)) {
            Object response = doProcess0(requestString, requestXml);
            configureWsaAction(Iti55PortType.REGULAR_REQUEST_OUTPUT_ACTION);
            return response;
        }

        else if ("D".equals(processingMode)) {
            // check whether deferred response URI is specified
            final String deferredResponseUri = Iti55Utils.normalizedDeferredResponseUri(requestXml);
            if (deferredResponseUri == null) {
                Hl7v3Exception hl7v3Exception = new Hl7v3Exception();
                hl7v3Exception.setMessage("Deferred response URI is missing or not HTTP(S)");
                hl7v3Exception.setTypeCode("AE");
                hl7v3Exception.setAcknowledgementDetailCode("SYN105");
                hl7v3Exception.setQueryResponseCode("AE");
                return createNak(requestXml, hl7v3Exception);
            }

            // determine original request message ID
            final WrappedMessageContext messageContext = (WrappedMessageContext) new WebServiceContextImpl().getMessageContext();
            AddressingProperties apropos = (AddressingProperties) messageContext.get(SERVER_ADDRESSING_PROPERTIES_INBOUND);
            final String requestMessageId = ((apropos != null) && (apropos.getMessageID() != null)) ?
                    apropos.getMessageID().getValue() : null;
            if (requestMessageId == null) {
                LOG.warn("Cannot determine WS-Addressing ID of the request message");
            }

            final WsAuditDataset auditDataset = (WsAuditDataset) messageContext.getWrappedMessage()
                    .getContextualProperty(AbstractAuditInterceptor.CONTEXT_KEY);

            // in a separate thread: run the route, send its result synchronously
            // to the deferred response URI, ignore all errors and ACKs
            Callable<?> command = new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    // Message context is a thread local object, so we need to propagate in into
                    // this new thread.  Note that the producer (see producerTemplate below) will
                    // get its own message context, precisely spoken a freshly created one.
                    WebServiceContextImpl.setMessageContext(messageContext);

                    // run the route
                    Object result = doProcess0(requestString, requestXml);

                    // prepare and send deferred response.
                    // NB: Camel message headers will be used in Iti55DeferredResponseProducer
                    Exchange exchange = new DefaultExchange(camelContext);
                    exchange.getIn().setBody(result);
                    exchange.getIn().setHeader("iti55.deferred.requestMessageId", requestMessageId);
                    exchange.getIn().setHeader("iti55.deferred.auditDataset", auditDataset);

                    AbstractWsEndpoint targetEndpoint = (AbstractWsEndpoint) camelContext.getEndpoint(deferredResponseUri);
                    targetEndpoint.setAudit(endpoint.isAudit());
                    targetEndpoint.setAllowIncompleteAudit(endpoint.isAllowIncompleteAudit());

                    exchange = producerTemplate.send(targetEndpoint, exchange);
                    if (exchange.isFailed()) {
                        LOG.error("Sending deferred response failed", exchange.getException());
                    }
                    return null;
                }
            };

            Future<?> future = executorService.submit(command);

            // return an immediate MCCI ACK
            configureWsaAction(Iti55PortType.DEFERRED_REQUEST_OUTPUT_ACTION);
            return response(requestXml, null, "MCCI_IN000002UV01", false, false);
        }

        else {
            Hl7v3Exception hl7v3Exception = new Hl7v3Exception();
            hl7v3Exception.setMessage(String.format("Unsupported processing mode '%s'", processingMode));
            hl7v3Exception.setTypeCode("AE");
            hl7v3Exception.setAcknowledgementDetailCode("NS250");
            hl7v3Exception.setQueryResponseCode("AE");
            return createNak(requestXml, hl7v3Exception);
        }
    }


    public Object doProcess0(String requestString, GPathResult requestXml) {
        Exchange result = process(requestString);
        return (result.getException() != null) ?
            nak(result.getException(), requestXml) :
            prepareBody(result);
    }


    /**
     * Generates an XCPD-specific NAK from the given exception.
     * @param exception
     *      occurred exception.
     * @param requestXml
     *      original request as GPath object.
     * @return
     *      NAK as XML string.
     */
    private String nak(Exception exception, GPathResult requestXml) {
        Hl7v3Exception hl7v3Exception;
        if (exception instanceof Hl7v3Exception) {
            hl7v3Exception = (Hl7v3Exception) exception;
        } else {
            hl7v3Exception = new Hl7v3Exception();
            hl7v3Exception.setCause(exception);
            hl7v3Exception.setDetectedIssueManagementCode("InternalError");
            hl7v3Exception.setDetectedIssueManagementCodeSystem("1.3.6.1.4.1.19376.1.2.27.3");
        }
        return createNak(requestXml, hl7v3Exception);
    }


    /**
     * Configures outbound WS-Addressing header "Action".
     * @param action
     *      WS-Addressing action.
     */
    private static void configureWsaAction(String action) {
        WrappedMessageContext messageContext = (WrappedMessageContext) new WebServiceContextImpl().getMessageContext();
        Message outMessage = messageContext.getWrappedMessage().getExchange().getOutMessage();
        AddressingProperties apropos = (AddressingProperties) outMessage.get(SERVER_ADDRESSING_PROPERTIES_OUTBOUND);
        if (apropos == null) {
            apropos = new AddressingPropertiesImpl();
            outMessage.put(SERVER_ADDRESSING_PROPERTIES_OUTBOUND, apropos);
        }

        AttributedURIType actionHolder = new AttributedURIType();
        actionHolder.setValue(action);
        apropos.setAction(actionHolder);
    }

}
>>>>>>> bcfe41f1c3755f92441ca14bf105974f7a258fd8
