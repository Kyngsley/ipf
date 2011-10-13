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
package org.openehealth.ipf.platform.camel.lbs.cxf.builder

import javax.activation.DataHandler
import javax.xml.ws.Holder
import org.apache.camel.Exchange
import org.apache.camel.component.cxf.common.message.CxfConstants
import org.apache.camel.spring.SpringRouteBuilder
import org.apache.cxf.message.MessageContentsList
import org.openehealth.ipf.commons.lbs.resource.ResourceFactory
import org.openehealth.ipf.platform.camel.lbs.cxf.process.AbstractLbsCxfTest

/**
 * @author Jens Riemschneider
 * @author Martin Krasser
 */
class LbsCxfRouteBuilderGroovy extends SpringRouteBuilder {
    
    void configure() {
        
        errorHandler(noErrorHandler())
        
        from('cxf:bean:soapEndpointNoExtract') 
            .to('bean:serviceBean?method=processSOAP')
        
        from('cxf:bean:soapEndpointExtract?dataFormat=POJO')
            .store().with('resourceHandlers')
            .to('bean:serviceBean?method=processSOAP')
            .store().with('resourceHandlers')
            .process(new AbstractLbsCxfTest.CheckOutputDataSource())
        
        from('cxf:bean:soapEndpointExtractRouter?dataFormat=POJO')
            .store().with('resourceHandlers')
            .to('cxf:bean:soapEndpointExtract?dataFormat=POJO&headerFilterStrategy=#routerHeaderFilterStrategy')
            .store().with('resourceHandlers')
            .process(new AbstractLbsCxfTest.CheckOutputDataSource())

        from('cxf:bean:soapEndpointExtractRouterRealServer?dataFormat=POJO')
            .store().with('resourceHandlers')
            .to('cxf:bean:soapEndpointRealServer?dataFormat=POJO&headerFilterStrategy=#routerHeaderFilterStrategy')
            .store().with('resourceHandlers')
            .process(new AbstractLbsCxfTest.CheckOutputDataSource())
        
        from('direct:cxflbs')
            .store().with('resourceHandlers')
            .to('mock:mock')
            .store().with('resourceHandlers')
            .process(new AbstractLbsCxfTest.CheckOutputDataSource())

        from('cxf:bean:soapEndpointExtractSwA?dataFormat=POJO')
            .store().with('resourceHandlers')
            .to("bean:serviceBean?method=processSOAP")
            .store().with('resourceHandlers')
            .process(new AbstractLbsCxfTest.CheckOutputDataSource())
            
        from('cxf:bean:soapEndpointExample1')
            // Store the binaries of the operation paremeters
            .store().with('resourceHandlers')
            // Custom processing to find a token in a binary
            .process { Exchange exchange ->
                // Operation parameters are contained in a list
                def params = exchange.in.getBody(List.class)
                // In this example, the parameter with index 1 contains a binary
                def inputStream = params.get(1).value.inputStream
                def reader = new BufferedReader(new InputStreamReader(inputStream))
                try {
                    def line = reader.readLine()
                    // Look for the token
                    while (line != null && !line.contains('blu')) {
                        line = reader.readLine()
                    }
                    // If found set the header
                    if (line != null) {
                        exchange.in.setHeader('tokenfound', 'yes')
                    }
                }
                finally {
                    reader.close()
                }
            }
            .to('bean:serviceBean?method=processSOAP')

        from('direct:start')
            // Custom processor to manually create a SOAP call
            .process { Exchange exchange ->
                // The resource factory can be used to create resources manually
                def resourceFactory = lookup('resourceFactory', ResourceFactory.class) 
                def inputStream1 = new ByteArrayInputStream('hello world'.bytes)
            
                // Using the unit of work from the original exchange we can ensure that the
                // resource is removed once the message has been processed by the route
                def resource1 = resourceFactory.createResource(exchange.unitOfWork.id, 'text/plain', null, null, inputStream1)
                def inputStream2 = new ByteArrayInputStream('this is me'.bytes)
                def resource2 = resourceFactory.createResource(exchange.unitOfWork.id, 'text/plain', null, null, inputStream2)
                
                // The list of parameters for the operation call
                def params = new MessageContentsList()                
                params.set(0, new Holder<String>('Hello world'))
                params.set(1, new Holder<DataHandler>(new DataHandler(resource1)))
                params.set(2, new DataHandler(resource2))
                
                // postMe is the operation being called
                exchange.in.setHeader(CxfConstants.OPERATION_NAME, "postMe")
                exchange.in.body = params
            }
            .to('cxf:bean:soapEndpointExtract')
    }    
}
