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
package org.openehealth.ipf.platform.camel.ihe.mllp.iti10

import org.openehealth.ipf.modules.hl7.message.MessageUtils
import org.apache.camel.spring.SpringRouteBuilder
import static org.openehealth.ipf.platform.camel.core.util.Exchanges.resultMessage
import static org.openehealth.ipf.platform.camel.ihe.mllp.PixPdqCamelValidators.*

/**
 * Camel route for generic unit tests.
 * @author Dmytro Rud
 */
class RouteBuilder extends SpringRouteBuilder {

     void configure() throws Exception {

         from('pix-iti10://0.0.0.0:18106?allowIncompleteAudit=true')
             .onException(Exception.class)
                 .maximumRedeliveries(0)
                 .end()
             .process {
                 resultMessage(it).body = MessageUtils.ack(it.in.body.target)
             }
         
         
         from('pix-iti10://0.0.0.0:18107?audit=false')
             .onException(Exception.class)
                 .maximumRedeliveries(0)
                 .end()
             .process(iti10RequestValidator())
             .process {
                 resultMessage(it).body = MessageUtils.ack(it.in.body.target)
             }
             .process(iti10ResponseValidator())

             
         from('pix-iti10://0.0.0.0:18108')
             .onException(Exception.class)
                 .maximumRedeliveries(0)
                 .end()
             .process {
                 resultMessage(it).body = MessageUtils.ack(it.in.body.target)
             }
     }
}
 
