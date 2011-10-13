/*
 * Copyright 2008 the original author or authors.
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
package org.openehealth.ipf.platform.camel.core.adapter;

import static junit.framework.Assert.assertEquals;

import java.io.IOException;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openehealth.ipf.commons.core.modules.api.ValidationException;
import org.openehealth.ipf.platform.camel.core.AbstractRouteTest;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Martin Krasser
 * @author Christian Ohr
 */
public class ValidatorRouteTest extends AbstractRouteTest {

    @EndpointInject(uri = "mock:error")
    protected MockEndpoint error;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    @After
    public void tearDown() throws Exception {
        error.reset();
        super.tearDown();
    }

    @Test
    public void testValidator1() throws InterruptedException {
        String result = (String) producerTemplate.sendBody(
                "direct:validator-test", ExchangePattern.InOut, "correct");
        assertEquals("correct", result);
    }

    @Test
    public void testValidator2() throws InterruptedException {
        error.expectedMessageCount(1);
        Exchange exchange = producerTemplate.send("direct:validator-test",
                ExchangePattern.InOut, new Processor() {
                    public void process(Exchange exchange) {
                        exchange.getIn().setBody("incorrect");
                    }
                });
        assertEquals(ValidationException.class, exchange.getException()
                .getClass());
        error.assertIsSatisfied(2000);
    }

    @Test
    public void testValidator3() throws InterruptedException, IOException {
        final String xml = IOUtils.toString(new ClassPathResource("xsd/test.xml")
                .getInputStream());
        String response = (String)producerTemplate.sendBody(
        		"direct:validator-xml-test", ExchangePattern.InOut, xml);
        assertEquals("passed", response);
     }

    @Test
    public void testValidator4() throws InterruptedException, IOException {
        final String xml = IOUtils.toString(new ClassPathResource(
                "xsd/invalidtest.xml").getInputStream());
        error.expectedMessageCount(1);
        Exchange exchange = producerTemplate.send("direct:validator-xml-test",
                ExchangePattern.InOut, new Processor() {
                    public void process(Exchange exchange) {
                        exchange.getIn().setBody(xml);
                    }
                });
        assertEquals(ValidationException.class, exchange.getException()
                .getClass());
        ValidationException e = (ValidationException) exchange.getException();
        assertEquals(5, e.getCauses().length);
        error.assertIsSatisfied(2000);
    }

    @Test
    public void testValidator5() throws InterruptedException, IOException {
        final String xml = IOUtils.toString(new ClassPathResource("schematron/schematron-test.xml")
                .getInputStream());
        String response = (String)producerTemplate.sendBody(
        		"direct:validator-schematron-test", ExchangePattern.InOut, xml);
        assertEquals("passed", response);
     }

    @Test
    public void testValidator6() throws InterruptedException, IOException {
        final String xml = IOUtils.toString(new ClassPathResource(
                "schematron/schematron-test-fail.xml").getInputStream());
        error.expectedMessageCount(1);
        Exchange exchange = producerTemplate.send("direct:validator-schematron-test",
                ExchangePattern.InOut, new Processor() {
                    public void process(Exchange exchange) {
                        exchange.getIn().setBody(xml);
                    }
                });
        assertEquals(ValidationException.class, exchange.getException()
                .getClass());
        ValidationException e = (ValidationException) exchange.getException();
        assertEquals(3, e.getCauses().length);
        error.assertIsSatisfied(2000);

    }    
}
