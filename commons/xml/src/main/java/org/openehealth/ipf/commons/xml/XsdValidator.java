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
package org.openehealth.ipf.commons.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openehealth.ipf.commons.core.modules.api.ValidationException;
import org.openehealth.ipf.commons.core.modules.api.Validator;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Validation of XML documents based on a XML Schema. Before using this class
 * consider using a validating Parser class.
 * 
 * @author Christian Ohr
 */
public class XsdValidator implements Validator<Source, String> {

    private final static Log LOG = LogFactory.getLog(XsdValidator.class);
    private ResourceLoader resourceLoader;
    private final static LSResourceResolverImpl lrri = new LSResourceResolverImpl();
    private String schemaLanguage = XMLConstants.W3C_XML_SCHEMA_NS_URI;

    private final static Map<String, Schema> cachedSchemas = Collections
            .synchronizedMap(new HashMap<String, Schema>(3));

    public XsdValidator() {
        resourceLoader = new DefaultResourceLoader();
    }

    public XsdValidator(ClassLoader classloader) {
        resourceLoader = new DefaultResourceLoader(classloader);
    }

    @Override
    public void validate(Source message, String schema) {
        List<ValidationException> exceptions = doValidate(message, schema);
        if (exceptions != null && exceptions.size() > 0) {
            throw new ValidationException(exceptions);
        }
    }

    /**
     * @param message
     *            the message to be validated
     * @param schemaSource
     *            the XML schema to validate against
     * @return an array of validation exceptions
     */
    protected List<ValidationException> doValidate(Source message,
            String schemaResource) {
        try {
            LOG.debug("Validating XML message");
            Schema schema = obtainSchema(schemaResource);
            javax.xml.validation.Validator validator = schema.newValidator();
            CollectingErrorHandler errorHandler = new CollectingErrorHandler();
            validator.setErrorHandler(errorHandler);
            validator.validate(message);
            List<ValidationException> exceptions = errorHandler.getExceptions();
            if (exceptions.size() > 0) {
                LOG.debug("Message validation found " + exceptions.size()
                        + " problems");
            } else {
                LOG.debug("Message validation successful");
            }
            return exceptions;
        } catch (SAXException e) {
            return Arrays
                    .asList(new ValidationException(
                            "Unexpected validation failure because "
                                    + e.getMessage(), e));
        } catch (IOException e) {
            return Arrays
                    .asList(new ValidationException(
                            "Unexpected validation failure because "
                                    + e.getMessage(), e));
        }
    }

    protected Schema obtainSchema(String schemaResource) throws SAXException, IOException {
        if (!cachedSchemas.containsKey(schemaResource)) {
            createSchema(schemaResource);
        }
        return cachedSchemas.get(schemaResource);
    }

    protected synchronized void createSchema(String schemaResource) throws SAXException, IOException {
        if (!cachedSchemas.containsKey(schemaResource)) {
            // SchemaFactory is neither thread-safe nor reentrant
            SchemaFactory factory = SchemaFactory.newInstance(getSchemaLanguage());

            // Register resource resolver to resolve external XML schemas
            factory.setResourceResolver(lrri);
            Schema schema = factory.newSchema(schemaSource(schemaResource));
            cachedSchemas.put(schemaResource, schema);
        }
    }

    public Source schemaSource(String resource) throws IOException {
        Resource r = resourceLoader.getResource(resource);
        if (r != null) {
            if (r.getURL() != null) {
                return new StreamSource(r.getInputStream(), r.getURL()
                        .toExternalForm());
            } else {
                return new StreamSource(r.getInputStream());
            }
        } else {
            throw new IllegalArgumentException("Schema not specified properly");
        }
    }

    public String getSchemaLanguage() {
        return schemaLanguage;
    }

    public void setSchemaLanguage(String schemaLanguage) {
        this.schemaLanguage = schemaLanguage;
    }

    // Just for testing purposes
    static Map<String, Schema> getCachedSchemas() {
        return cachedSchemas;
    }

    /**
     * Error handler that collects {@link SAXParseException}s and provides them
     * wrapped in an {@link ValidationException} array.
     * 
     * @author Christian Ohr
     */
    static class CollectingErrorHandler implements ErrorHandler {

        private List<SAXParseException> exceptions;

        @Override
        public void error(SAXParseException exception) throws SAXException {
            if (exceptions == null) {
                exceptions = new ArrayList<SAXParseException>();
            }
            exceptions.add(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            if (exceptions == null) {
                exceptions = new ArrayList<SAXParseException>();
            }
            exceptions.add(exception);
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            // TODO LOG some message
        }

        public List<ValidationException> getExceptions() {
            List<ValidationException> validationExceptions = new ArrayList<ValidationException>();
            if (exceptions != null) {
                for (SAXParseException exception : exceptions) {
                    validationExceptions
                            .add(new ValidationException(exception));
                }
            }
            return validationExceptions;

        }

        public void reset() {
            if (exceptions != null) {
                exceptions.clear();
            }
        }
    }
}