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
package org.openehealth.ipf.platform.camel.flow.aspect;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.processor.MulticastProcessor;
import org.apache.camel.processor.aggregate.UseLatestAggregationStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openehealth.ipf.commons.flow.ManagedMessage;
import org.openehealth.ipf.commons.flow.history.SplitHistory;
import org.openehealth.ipf.platform.camel.flow.PlatformMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


/**
 * @author Martin Krasser
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/context-weaver.xml" })
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
public class MulticastCopyAspectTest {

    private Processor multicast;

    private List<ManagedMessage> messages;
    
    @Before
    public void setUp() throws Exception {
        Processor processor = new TestProcessor();
        messages = new ArrayList<ManagedMessage>();
        multicast = new MulticastProcessor(Collections.nCopies(5, processor),
                new UseLatestAggregationStrategy());
    }

    @After
    public void tearDown() throws Exception {
        messages.clear();
    }

    @Test
    public void testMulticast() throws Exception {
        PlatformMessage message = createMessage();
        SplitHistory original = message.getSplitHistory();
        multicast.process(message.getExchange());
        assertEquals(original, message.getSplitHistory());
        for (int i = 0; i < messages.size(); i++) {
            SplitHistory expected = SplitHistory.parse("[(0/1),(" + i + "/5)]");
            assertEquals(expected, messages.get(i).getSplitHistory());
        }
    }
    
    private PlatformMessage createMessage() {
        return createMessage(new DefaultExchange(new DefaultCamelContext()));
    }
    
    private PlatformMessage createMessage(Exchange exchange) {
        return new PlatformMessage(exchange);
    }
    
    private class TestProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            messages.add(new PlatformMessage(exchange));
        }
    }
    
}