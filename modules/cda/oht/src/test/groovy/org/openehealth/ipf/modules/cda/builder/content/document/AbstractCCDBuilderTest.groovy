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
package org.openehealth.ipf.modules.cda.builder.content.document

import org.junit.Before
import org.openehealth.ipf.modules.ccd.builder.CCDModelExtension
import org.openehealth.ipf.modules.cda.AbstractCDAR2Test
import org.openehealth.ipf.modules.cda.CDAR2Renderer

/**
 * Abstract CCD Document Builder Test environment
 * @author Stefan Ivanov
 * @author Christian Ohr
 */
public abstract class AbstractCCDBuilderTest extends AbstractCDAR2Test{
    static def builder
    static def renderer
    
    @Before
    public void init() throws Exception {
        if (!builder)
            builder = new CCDBuilder()
        new CCDModelExtension(builder).register(registered)
        renderer = new CDAR2Renderer()
    }
}
