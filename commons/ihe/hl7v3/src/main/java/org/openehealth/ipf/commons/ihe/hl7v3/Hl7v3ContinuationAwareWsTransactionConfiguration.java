/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openehealth.ipf.commons.ihe.hl7v3;

import org.apache.commons.lang3.Validate;
import org.openehealth.ipf.commons.ihe.core.InteractionId;

import javax.xml.namespace.QName;

/**
 * @author Dmytro Rud
 */
public class Hl7v3ContinuationAwareWsTransactionConfiguration extends Hl7v3WsTransactionConfiguration {

    private final String mainRequestRootElementName;
    private final String mainResponseRootElementName;


    public Hl7v3ContinuationAwareWsTransactionConfiguration(
            InteractionId interactionId,
            QName serviceName,
            Class<?> sei,
            QName bindingName,
            boolean mtom,
            String wsdlLocation,
            String nakRootElementName,
            boolean nakNeedControlActProcess,
            boolean auditRequestPayload,
            boolean supportAsynchrony,
            String mainRequestRootElementName,
            String mainResponseRootElementName)
    {
        super(interactionId, serviceName, sei, bindingName, mtom, wsdlLocation,
                nakRootElementName, nakNeedControlActProcess,
                auditRequestPayload, supportAsynchrony);

        Validate.notEmpty(mainRequestRootElementName);
        Validate.notEmpty(mainResponseRootElementName);
        this.mainRequestRootElementName = mainRequestRootElementName;
        this.mainResponseRootElementName = mainResponseRootElementName;
    }


    /**
     * @return root XML element name for request messages
     * which correspond to the "main" operation of the transaction,
     * e.g. "PRPA_IN201305UV02" for PDQv3.
     */
    public String getMainRequestRootElementName() {
        return mainRequestRootElementName;
    }

    /**
     * @return root XML element name for response messages
     * which correspond to the "main" operation of the transaction,
     * e.g. "PRPA_IN201306UV02" for PDQv3.
     */
    public String getMainResponseRootElementName() {
        return mainResponseRootElementName;
    }

}
