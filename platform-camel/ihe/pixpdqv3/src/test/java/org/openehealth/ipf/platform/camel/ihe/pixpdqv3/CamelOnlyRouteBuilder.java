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
package org.openehealth.ipf.platform.camel.ihe.pixpdqv3;

import ca.uhn.hl7v2.parser.Parser;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.lang.Validate;
import org.openehealth.ipf.commons.ihe.pixpdqv3.translation.PdqRequest3to2Translator;
import org.openehealth.ipf.commons.ihe.pixpdqv3.translation.PdqResponse2to3Translator;
import org.openehealth.ipf.modules.hl7dsl.MessageAdapter;
import org.openehealth.ipf.modules.hl7dsl.MessageAdapters;
import org.openehealth.ipf.platform.camel.ihe.pixpdq.iti21.Iti21Component;

import static org.openehealth.ipf.platform.camel.ihe.pixpdq.PixPdqCamelValidators.iti21RequestValidator;
import static org.openehealth.ipf.platform.camel.ihe.pixpdq.PixPdqCamelValidators.iti21ResponseValidator;
import static org.openehealth.ipf.platform.camel.ihe.pixpdqv3.PixPdqV3CamelTranslators.translatorHL7v2toHL7v3;
import static org.openehealth.ipf.platform.camel.ihe.pixpdqv3.PixPdqV3CamelTranslators.translatorHL7v3toHL7v2;
import static org.openehealth.ipf.platform.camel.ihe.pixpdqv3.PixPdqV3CamelValidators.iti47RequestValidator;
import static org.openehealth.ipf.platform.camel.ihe.pixpdqv3.PixPdqV3CamelValidators.iti47ResponseValidator;


/**
 * Test for Camel-only IHE transaction support.
 * @author Dmytro Rud
 */
public class CamelOnlyRouteBuilder extends SpringRouteBuilder {

    private static final Parser PARSER = Iti21Component.CONFIGURATION.getParser();
    private static final PdqRequest3to2Translator REQUEST_TRANSLATOR = new PdqRequest3to2Translator();
    private static final PdqResponse2to3Translator RESPONSE_TRANSLATOR = new PdqResponse2to3Translator();


    @Override
    public void configure() throws Exception {
        from("pdqv3-iti47:iti47Service")
            .onException(Exception.class)
                .maximumRedeliveries(0)
                .end()
            .process(iti47RequestValidator())
            .setHeader("myHeader", constant("content-1"))
            .process(translatorHL7v3toHL7v2(REQUEST_TRANSLATOR))
            .process(typeAndHeaderChecker(MessageAdapter.class, "content-1"))
            .process(iti21RequestValidator())
            .setBody(constant(MessageAdapters.make(PARSER, TestIti47CamelOnly.getResponseMessage())))
            .process(iti21ResponseValidator())
            .setHeader("myHeader", constant("content-2"))
            .process(translatorHL7v2toHL7v3(RESPONSE_TRANSLATOR))
            .process(typeAndHeaderChecker(String.class, "content-2"))
            .process(iti47ResponseValidator());
    }
    
    
    private static Processor typeAndHeaderChecker(
            final Class<?> expectedClass,
            final String expectedHeaderContent)
    {
        Validate.notNull(expectedClass);
        return new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                Class<?> actualClass = exchange.getIn().getBody().getClass();
                if (! expectedClass.equals(actualClass)) {
                    throw new RuntimeException("Wrong body class: expected " + 
                            expectedClass + ", got " + actualClass);
                }

                if (expectedHeaderContent != null) {
                    String actualHeaderContent = exchange.getIn().getHeader("myHeader", String.class);
                    if (! expectedHeaderContent.equals(actualHeaderContent)) {
                        throw new RuntimeException("wrong headers");
                    }
                }
            }
        };
    }
    
}
