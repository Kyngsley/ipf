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
package org.openehealth.ipf.platform.camel.lbs.http.builder;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.openehealth.ipf.commons.lbs.resource.ResourceDataSource;
import org.openehealth.ipf.platform.camel.lbs.core.process.ResourceHandler;

import java.util.ArrayList;
import java.util.List;

import static org.openehealth.ipf.platform.camel.lbs.core.builder.RouteHelper.fetch;
import static org.openehealth.ipf.platform.camel.lbs.core.builder.RouteHelper.store;

/**
 * @author Jens Riemschneider
 */
public class LbsHttpRouteBuilderJava extends SpringRouteBuilder {

    @Override
    public void configure() throws Exception {
        List<?> list = lookup("resourceHandlers", List.class);
        List<ResourceHandler> handlers = new ArrayList<ResourceHandler>();
        for (Object handler : list) {
            handlers.add((ResourceHandler) handler);
        }

        errorHandler(noErrorHandler());
        
        from("jetty:http://localhost:9452/lbstest_no_extract")
            .to("mock:mock");
        
        from("jetty:http://localhost:9452/lbstest_extract")
            .process(store().with(handlers))
            .to("mock:mock");
        
        from("jetty:http://localhost:9452/lbstest_ping")
            .process(store().with(handlers))
            .process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    ResourceDataSource dataSource = 
                        exchange.getIn().getBody(ResourceDataSource.class);
                    exchange.getOut().setBody(dataSource);
                }
            })
            .to("mock:mock");
        
        // Note: This is not available via java, only groovy. Simply make the test work
        from("jetty:http://localhost:9452/lbstest_extract_factory_via_bean")
            .process(store().with(handlers))
            .to("mock:mock");
        
        from("jetty:http://localhost:9452/lbstest_extract_router")
            .process(store().with(handlers))
            .setHeader("tag", constant("I was here"))
            .process(fetch().with(handlers))
            .removeHeader(Exchange.HTTP_PATH)
            .removeHeader(Exchange.HTTP_URI)
            .to("http://localhost:9452/lbstest_receiver");      

        from("direct:lbstest_send_only")
            .process(fetch().with(handlers))
            .to("http://localhost:9452/lbstest_receiver");
        
        
        from("direct:lbstest_non_http")
            .process(store().with(handlers))
            .to("mock:mock");

        from("jetty:http://localhost:9452/lbstest_receiver")
            .process(store().with(handlers))
            .to("mock:mock");
    }
}
