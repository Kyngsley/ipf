<<<<<<< HEAD
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
package org.openehealth.ipf.platform.camel.ihe.xds.iti41;

import org.apache.camel.Exchange;
import org.openehealth.ipf.commons.ihe.xds.iti41.Iti41PortType;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.ProvideAndRegisterDocumentSetRequestType;
import org.openehealth.ipf.commons.ihe.xds.core.responses.ErrorCode;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryResponseType;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.ws.DefaultItiWebService;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;

/**
 * Service implementation for the IHE ITI-41 transaction (Provide and Register Document Set).
 * <p>
 * This implementation delegates to a Camel consumer by creating an exchange.
 *
 * @author Jens Riemschneider
 */
public class Iti41Service extends DefaultItiWebService implements Iti41PortType {
    @Override
    public RegistryResponseType documentRepositoryProvideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType body) {
        Exchange result = process(body);
        if (result.getException() != null) {
            Response errorResponse = new Response(result.getException(), ErrorCode.REPOSITORY_METADATA_ERROR, ErrorCode.REPOSITORY_ERROR, null);
            return EbXML30Converters.convert(errorResponse);
        }
        
        return Exchanges.resultMessage(result).getBody(RegistryResponseType.class);            
    }
=======
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
package org.openehealth.ipf.platform.camel.ihe.xds.iti41;

import org.apache.camel.Exchange;
import org.openehealth.ipf.commons.ihe.xds.iti41.Iti41PortType;
import org.openehealth.ipf.commons.ihe.xds.core.ebxml.ebxml30.ProvideAndRegisterDocumentSetRequestType;
import org.openehealth.ipf.commons.ihe.xds.core.responses.ErrorCode;
import org.openehealth.ipf.commons.ihe.xds.core.responses.Response;
import org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs30.rs.RegistryResponseType;
import org.openehealth.ipf.platform.camel.core.util.Exchanges;
import org.openehealth.ipf.platform.camel.ihe.ws.AbstractWebService;
import org.openehealth.ipf.platform.camel.ihe.xds.core.converters.EbXML30Converters;

/**
 * Service implementation for the IHE ITI-41 transaction (Provide and Register Document Set).
 * <p>
 * This implementation delegates to a Camel consumer by creating an exchange.
 *
 * @author Jens Riemschneider
 */
public class Iti41Service extends AbstractWebService implements Iti41PortType {
    @Override
    public RegistryResponseType documentRepositoryProvideAndRegisterDocumentSetB(ProvideAndRegisterDocumentSetRequestType body) {
        Exchange result = process(body);
        if (result.getException() != null) {
            Response errorResponse = new Response(result.getException(), ErrorCode.REPOSITORY_METADATA_ERROR, ErrorCode.REPOSITORY_ERROR, null);
            return EbXML30Converters.convert(errorResponse);
        }
        
        return Exchanges.resultMessage(result).getBody(RegistryResponseType.class);            
    }
>>>>>>> bcfe41f1c3755f92441ca14bf105974f7a258fd8
}