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
package org.openehealth.ipf.modules.hl7.parser

import ca.uhn.hl7v2.model.*
import ca.uhn.hl7v2.parser.*
import ca.uhn.hl7v2.validation.ValidationContext
import ca.uhn.hl7v2.HL7Exception

import org.openehealth.ipf.modules.hl7.validation.support.DefaultTypeRulesValidationContext

import java.lang.reflect.Constructor;

/**
 * PipeParser that also allows for using the CustomModelClassFactory in
 * order to support changes or extensions of the default HL7 model.
 * As of HAPI v0.6, there's a {@link PipeParser(ModelClassFactory)} constructor
 * so we can omit our local copy.
 * 
 * @author Christian Ohr
 * @author Marek V�cl�vik
 *
 */
public class PipeParser extends ca.uhn.hl7v2.parser.PipeParser {
	
	PipeParser() {
		super(new CustomModelClassFactory())
		setValidationContext(new DefaultTypeRulesValidationContext())
	}
	
	PipeParser(ModelClassFactory factory) {
		super(factory)
		setValidationContext(new DefaultTypeRulesValidationContext())
	}

	PipeParser(ValidationContext context) {
	    super(new CustomModelClassFactory())
		setValidationContext(context)
	}
	
	PipeParser(ModelClassFactory factory, ValidationContext context) {
		super(factory)
		setValidationContext(context)
	}
	
}
