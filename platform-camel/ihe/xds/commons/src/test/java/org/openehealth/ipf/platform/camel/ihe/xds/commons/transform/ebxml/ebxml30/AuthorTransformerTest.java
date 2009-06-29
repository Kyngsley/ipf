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
package org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.ebxml30;

import org.junit.Before;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.ebxml30.EbXMLFactory30;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.AuthorTransformer;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.AuthorTransformerTestBase;

/**
 * Tests for {@link AuthorTransformer}.
 * @author Jens Riemschneider
 */
public class AuthorTransformerTest extends AuthorTransformerTestBase {
    @Before
    public void setUp() {
        transformer = new AuthorTransformer(new EbXMLFactory30());
    }
}