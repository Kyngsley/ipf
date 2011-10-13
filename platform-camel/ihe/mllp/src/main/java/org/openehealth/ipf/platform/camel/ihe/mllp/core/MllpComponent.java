/*
 * Copyright 2009 the original author or authors.
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
package org.openehealth.ipf.platform.camel.ihe.mllp.core;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.component.hl7.HL7MLLPCodec;
import org.apache.camel.component.mina.MinaComponent;
import org.apache.camel.component.mina.MinaEndpoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.Hl7v2ConfigurationHolder;
import org.openehealth.ipf.platform.camel.ihe.hl7v2.intercept.consumer.ConsumerAdaptingInterceptor;
import org.openehealth.ipf.platform.camel.ihe.mllp.core.intercept.MllpCustomInterceptor;

import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.util.*;


/**
 * Generic Camel component for IHE PIX/PDQ transactions.
 * 
 * @author Dmytro Rud
 */
public abstract class MllpComponent extends MinaComponent implements Hl7v2ConfigurationHolder {
    private static final transient Log LOG = LogFactory.getLog(MllpComponent.class);
    
    public static final String ACK_TYPE_CODE_HEADER = ConsumerAdaptingInterceptor.ACK_TYPE_CODE_HEADER;
    
    private static final String DEFAULT_HL7_CODEC_FACTORY_BEAN_NAME = "#hl7codec";

    /**
     * Default constructor.
     */
    protected MllpComponent() {
        super();
    }

    /**
     * Camel context-based constructor.
     * 
     * @param camelContext
     */
    protected MllpComponent(CamelContext camelContext) {
        super(camelContext);
    }


    /**
     * Creates and configures the endpoint.
     */
    @Override
    protected Endpoint createEndpoint(
            String uri,
            String remaining, 
            Map<String, Object> parameters) throws Exception
    {
        // replace URL parts
        int pos = uri.indexOf(":");
        uri = new StringBuilder(uri).replace(0, pos, "mina:tcp").toString();
        remaining = "tcp://" + remaining;

        // extract & exclude parameters which cannot be handled by camel-mina
        boolean audit = getAndRemoveParameter(parameters, "audit", boolean.class, true);
        boolean allowIncompleteAudit =
                getAndRemoveParameter(parameters, "allowIncompleteAudit", boolean.class, false);

        boolean secure = getAndRemoveParameter(parameters, "secure", boolean.class, false);
        MllpClientAuthType clientAuthType = getAndRemoveParameter(parameters, "clientAuth",
                MllpClientAuthType.class, MllpClientAuthType.NONE);
        String sslProtocolsString = getAndRemoveParameter(parameters, "sslProtocols", String.class, null);
        String sslCiphersString = getAndRemoveParameter(parameters, "sslCiphers", String.class, null);
        
        boolean supportInteractiveContinuation = getAndRemoveParameter(
                parameters, "supportInteractiveContinuation", boolean.class, false);
        int interactiveContinuationDefaultThreshold = getAndRemoveParameter(
                parameters, "interactiveContinuationDefaultThreshold", int.class, -1);      // >= 1 data record
        
        boolean supportUnsolicitedFragmentation = getAndRemoveParameter(
                parameters, "supportUnsolicitedFragmentation", boolean.class, false);
        int unsolicitedFragmentationThreshold = getAndRemoveParameter(
                parameters, "unsolicitedFragmentationThreshold", int.class, -1);            // >= 3 segments
        
        boolean supportSegmentFragmentation = getAndRemoveParameter(
                parameters, "supportSegmentFragmentation", boolean.class, false);
        int segmentFragmentationThreshold = getAndRemoveParameter(
                parameters, "segmentFragmentationThreshold", int.class, -1);                // >= 5 characters
        
        InteractiveContinuationStorage interactiveContinuationStorage =
                resolveAndRemoveReferenceParameter(
                        parameters,
                        "interactiveContinuationStorage",
                        InteractiveContinuationStorage.class);

        UnsolicitedFragmentationStorage unsolicitedFragmentationStorage = 
                resolveAndRemoveReferenceParameter(
                        parameters,
                        "unsolicitedFragmentationStorage",
                        UnsolicitedFragmentationStorage.class);

        boolean autoCancel = getAndRemoveParameter(parameters, "autoCancel", boolean.class, false);

        // explicitly overwrite some standard camel-mina parameters
        if (parameters == Collections.EMPTY_MAP) {
            parameters = new HashMap<String, Object>();
        }
        parameters.put("sync", true);
        parameters.put("lazySessionCreation", true);
        parameters.put("transferExchange", false);
        if( ! parameters.containsKey("codec")) {
            parameters.put("codec", DEFAULT_HL7_CODEC_FACTORY_BEAN_NAME);
        }

        // adopt character set configured for the HL7 codec
        ProtocolCodecFactory codecFactory = getCamelContext().getRegistry().lookup(
                    extractBeanName((String) parameters.get("codec")), 
                    ProtocolCodecFactory.class);
        Charset charset = null;
        try {
            charset = ((HL7MLLPCodec) codecFactory).getCharset();
        } catch(ClassCastException cce) {
            LOG.error("Unsupported HL7 codec factory type " + codecFactory.getClass().getName());
        }
        if(charset == null) {
            charset = Charset.defaultCharset();
        }
        parameters.put("encoding", charset.name());
        
        // construct the endpoint
        Endpoint endpoint = super.createEndpoint(uri, remaining, parameters);
        MinaEndpoint minaEndpoint = (MinaEndpoint) endpoint;

        SSLContext sslContext = secure ? resolveAndRemoveReferenceParameter(
                parameters, 
                "sslContext", 
                SSLContext.class,
                SSLContext.getDefault()) : null;

        List<MllpCustomInterceptor> customInterceptors = resolveAndRemoveReferenceListParameter(
                parameters, "interceptors", MllpCustomInterceptor.class);

        String[] sslProtocols = sslProtocolsString != null ? sslProtocolsString.split(",") : null;
        String[] sslCiphers = sslCiphersString != null ? sslCiphersString.split(",") : null;

        // wrap and return
        return new MllpEndpoint(
                this,
                minaEndpoint,
                audit,
                allowIncompleteAudit,
                sslContext,
                clientAuthType,
                customInterceptors,
                sslProtocols,
                sslCiphers,
                supportInteractiveContinuation,
                supportUnsolicitedFragmentation,
                supportSegmentFragmentation,
                interactiveContinuationDefaultThreshold,
                unsolicitedFragmentationThreshold,
                segmentFragmentationThreshold,
                interactiveContinuationStorage,
                unsolicitedFragmentationStorage,
                autoCancel);
    }

    private static String extractBeanName(String originalBeanName) {
        return originalBeanName.startsWith("#") ? originalBeanName.substring(1) : originalBeanName;
    }


    // ----- abstract methods -----

    /**
     * Returns server-side ATNA audit strategy. 
     */
    public abstract MllpAuditStrategy getServerAuditStrategy();

    /**
     * Returns client-side ATNA audit strategy. 
     */
    public abstract MllpAuditStrategy getClientAuditStrategy();

}
