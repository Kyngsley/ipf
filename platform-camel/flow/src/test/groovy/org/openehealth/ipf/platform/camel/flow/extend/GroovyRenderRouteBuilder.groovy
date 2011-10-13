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
package org.openehealth.ipf.platform.camel.flow.extend

import org.apache.camel.spring.SpringRouteBuilder

/**
 * @author Martin Krasser
 */
class GroovyRenderRouteBuilder extends SpringRouteBuilder {
    
    void configure() {
    
        def dlc = deadLetterChannel('direct:err').maximumRedeliveries(0)        
        
        // --------------------------
        //  Default route
        // --------------------------

        from('direct:render-test')
            .errorHandler(dlc)
            .onException(Exception.class).handled(false).end()
            .initFlow('test-1')
                .application("test")
                .renderer('initRenderer')
                .outType(String.class)
            .validate { body ->
                if (body == 'error') {
                    throw new Exception('message rejected')
                }
            }
            .to('mock:out')
            .ackFlow().renderer('ackRenderer')
        
        // --------------------------
        //  Error route
        // --------------------------
        
        from('direct:err')
            .nakFlow().renderer('nakRenderer')
            .to('mock:err')

    }

}
