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
package org.openehealth.ipf.platform.camel.ihe.mllp.core.extend;

import org.apache.camel.Processor;
import org.openehealth.ipf.platform.camel.core.model.ValidatorAdapterDefinition;
import org.openehealth.ipf.platform.camel.core.process.ProcessorBasedExchangeValidator
import org.openehealth.ipf.platform.camel.ihe.mllp.PixPdqCamelValidators;

/**
 * PIX/PDQ DSL extensions for usage in a {@link org.apache.camel.builder.RouteBuilder}
 * using the {@code use} keyword.
 *
 * @DSL
 *
 * @deprecated Please use standard Camel <code>.process()</code> DSL
 * element with one of processors from
 * {@link org.openehealth.ipf.platform.camel.ihe.mllp.PixPdqCamelValidators}
 * as argument.
 *
 * @author Jens Riemschneider
 */
@Deprecated
class MllpExtension {
     /**
      * Validates an ITI-8 request
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti8Request(ValidatorAdapterDefinition self) {
         return definition(self, 8, true);
     }
     
     /**
      * Validates an ITI-8 response
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti8Response(ValidatorAdapterDefinition self) {
         return definition(self, 8, false);
     }
     
     /**
      * Validates an ITI-9 request
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti9Request(ValidatorAdapterDefinition self) {
         return definition(self, 9, true);
     }
     
     /**
      * Validates an ITI-9 response
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti9Response(ValidatorAdapterDefinition self) {
         return definition(self, 9, false);
     }
     
     /**
      * Validates an ITI-10 request
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti10Request(ValidatorAdapterDefinition self) {
         return definition(self, 10, true);
     }
     
     /**
      * Validates an ITI-10 response
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti10Response(ValidatorAdapterDefinition self) {
         return definition(self, 10, false);
     }
     
     /**
      * Validates an ITI-21 request
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti21Request(ValidatorAdapterDefinition self) {
         return definition(self, 21, true);
     }

     /**
      * Validates an ITI-21 response
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti21Response(ValidatorAdapterDefinition self) {
         return definition(self, 21, false);
     }

     /**
      * Validates an ITI-22 request
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti22Request(ValidatorAdapterDefinition self) {
         return definition(self, 22, true);
     }
     
     /**
      * Validates an ITI-22 response
      * @DSLDoc http://repo.openehealth.org/confluence/display/ipf2/IHE+support#IHEsupport-validationpixpdq
      */
     public static ValidatorAdapterDefinition iti22Response(ValidatorAdapterDefinition self) {
         return definition(self, 22, false);
     }
     
     
     private static ValidatorAdapterDefinition definition(
             ValidatorAdapterDefinition self, 
             int transaction,
             boolean request) 
     {
         String what = request ? 'Request' : 'Response';
         Processor validatingProcessor = PixPdqCamelValidators."iti${transaction}${what}Validator"()
         return ProcessorBasedExchangeValidator.definition(self, validatingProcessor);
     }
}
