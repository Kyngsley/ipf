/*
 * Copyright 2011 the original author or authors.
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
package org.openehealth.ipf.commons.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xquery.XQException;

import net.sf.saxon.FeatureKeys;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

public class XqjTransmogrifierTest {

    private XqjTransmogrifier<String> transformer;

    @BeforeClass
    public static void setUpClass() {
        XMLUnit.setCompareUnmatched(true);
        XMLUnit.setIgnoreAttributeOrder(true);
        XMLUnit.setIgnoreComments(true);
        XMLUnit.setIgnoreWhitespace(true);
    }

    @Before
    public void setUp() throws Exception {
        transformer = new XqjTransmogrifier<String>(String.class);
    }

    @Test
    public void zapSimple() throws IOException, SAXException {
        String zapResult = transformer.zap(source("xquery/string.xml"), "xquery/string-q1.xq");
        assertTrue(XMLUnit.compareXML(result("xquery/string-q1.xml"), zapResult).similar());
    }

    @Test
    public void zapStringParameter() throws IOException, SAXException {
        Map<String, Object> dynamicParams = new HashMap<String, Object>();
        dynamicParams.put("language", "English");
        Object[] params = new Object[] { "xquery/string-q2.xq", dynamicParams };
        String zapResult1 = transformer.zap(source("xquery/string.xml"), params);
        assertTrue(XMLUnit.compareXML(result("xquery/string-q2.xml"), zapResult1).similar());
        dynamicParams.put("language", "German");
        String zapResult2 = transformer.zap(source("xquery/string.xml"), params);
        assertFalse(XMLUnit.compareXML(zapResult1, zapResult2).similar());
    }

    @Test(expected = RuntimeException.class)
    public void zapMissingParameter() throws IOException, SAXException {
        transformer.zap(source("xquery/string.xml"), "xquery/string-q2.xq");
    }

    @Test
    public void zapLocalFunction() throws IOException, SAXException {
        Map<String, Object> dynamicEvalParams = new HashMap<String, Object>();
        dynamicEvalParams.put("language", "Bulgarian");
        dynamicEvalParams.put("author_name", "John");
        Object[] params = new Object[] { "xquery/string-q3.xq", dynamicEvalParams };
        String zapResult = transformer.zap(source("xquery/string.xml"), params);
        assertTrue(XMLUnit.compareXML(result("xquery/string-q3.xml"), zapResult).similar());
    }

    @Test
    public void zapMainFunction() throws IOException, SAXException {
        String zapResult = transformer.zap(source("xquery/string.xml"), "xquery/string-q4.xq");
        assertTrue(XMLUnit.compareXML(result("xquery/string.xml"), zapResult).similar());
    }

    @Test
    public void zapClasspathResolver() throws IOException, SAXException {
        String zapResult = transformer.zap(source("xquery/string.xml"), "xquery/string-q5.xq");
        assertTrue(XMLUnit.compareXML(result("xquery/string.xml"), zapResult).similar());
    }

    @Test
    public void zapParametrisedConstructor() throws IOException, SAXException, XQException {
        Map<String, Object> configParams = new HashMap<String, Object>();
        configParams.put(FeatureKeys.PRE_EVALUATE_DOC_FUNCTION, Boolean.TRUE);
        XqjTransmogrifier<String> localTransformer = new XqjTransmogrifier<String>(String.class, configParams);
        String zapResult = localTransformer.zap(source("xquery/string.xml"), new Object[] { "xquery/string-q5.xq" });
        assertTrue(XMLUnit.compareXML(result("xquery/string.xml"), zapResult).similar());
    }

    @Test
    public void zapParametrisedConstructorNoParams() throws IOException, SAXException, XQException {
        XqjTransmogrifier<String> localTransformer = new XqjTransmogrifier<String>(String.class, null);
        String zapResult = localTransformer.zap(source("xquery/string.xml"), new Object[] { "xquery/string-q5.xq" });
        assertTrue(XMLUnit.compareXML(result("xquery/string.xml"), zapResult).similar());
    }

    @Test
    public void zapWithNamespaces() throws IOException, SAXException {
        String zapResult = transformer.zap(source("xquery/ns.xml"), "xquery/ns-q1.xq");
        assertTrue(XMLUnit.compareXML(result("xquery/ns-q1.xml"), zapResult).similar());
    }

    @Test
    public void zapRCase() throws IOException, SAXException {
        String zapResult = transformer.zap(new StreamSource(new StringReader("<empty/>")), "xquery/r-q1.xq");
        assertTrue(XMLUnit.compareXML(result("xquery/r-q1.xml"), zapResult).similar());
    }

    private Source source(String path) throws IOException {
        return new StreamSource(new ClassPathResource(path).getInputStream());
    }

    private String result(String path) throws IOException{
        return IOUtils.toString(new ClassPathResource(path).getInputStream());
    }
}
