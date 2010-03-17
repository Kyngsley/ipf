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
package org.openehealth.ipf.commons.ihe.hl7v3

import groovy.xml.MarkupBuilder
import groovy.util.slurpersupport.GPathResult
import org.openehealth.ipf.commons.xml.XmlYielder

/*
 * Generic routines for HL7 v3 transformation.
 * @author Dmytro Rud, Marek V�clav�k
 */
class Hl7v3Utils {

    /**
     * Creates and configures an XML builder based on the given output stream.
     */
    static MarkupBuilder getBuilder(OutputStream output) {
        Writer writer = new OutputStreamWriter(output, 'UTF-8')
        MarkupBuilder builder = new MarkupBuilder(writer)
        builder.setDoubleQuotes(true)
        builder.setOmitNullAttributes(true)
        builder.setOmitEmptyAttributes(true)
        return builder
    }
    
    
    /**
     * Parses the given XML document and performs some post-configuration 
     * of the generated {@link GPathResult} object. 
     */
    static GPathResult slurp(String document) {
        GPathResult xml = new XmlSlurper(false, true).parseText(document)
        xml.declareNamespace(
            '*'   : 'urn:hl7-org:v3',              
            'xsi' : 'http://www.w3.org/2001/XMLSchema-instance',
            'xsd' : 'http://www.w3.org/2001/XMLSchema')
        return xml
    }
    
    
    /**
     * Creates an XML element that represents an instance identifier with given contents.
     * <p>
     * Both root and extension can be empty or <code>null</code>.
     */
    static void buildInstanceIdentifier(
            MarkupBuilder builder, 
            String elementName, 
            boolean useNullFlavor, 
            String root, 
            String extension,
            String assigningAuthorityName = null) 
    {    
        if (root || extension) {
            def map = [root: root, 
                       extension: extension, 
                       assigningAuthorityName: assigningAuthorityName].findAll { it.value } 
            builder."$elementName"(map)
        } else if (useNullFlavor){
            builder."$elementName"(nullFlavor: 'UNK')
        }
    }         


    /**
     * Yields crossed sender and receiver elements from original message.
     */
    static void buildReceiverAndSender(
            MarkupBuilder builder, 
            GPathResult originalXml, 
            String defaultNamespaceUri) 
    {
        builder.receiver(typeCode: 'RCV') {
            XmlYielder.yieldChildren(originalXml.sender, builder, defaultNamespaceUri)
        }
        builder.sender(typeCode: 'SND') {
            XmlYielder.yieldChildren(originalXml.receiver, builder, defaultNamespaceUri)
        }
    }

    
    /**
     * Inserts an XML element with the given name if the corresponding content is present.
     */
    static void conditional(MarkupBuilder builder, String elementName, String content) {
        if (content) {
            builder."${elementName}"(content)
        }
    }
    

    /**
     * Removes time zone from date/time representation (i.e. suffix "[+-].*").
     */
    static String dropTimeZone(String s) {
        int pos = s.indexOf('+')
        if (pos == -1) {
            pos = s.indexOf('-')
        }
        return (pos > 0) ? s.substring(0, pos) : s
    }
    

    /**
     * Creates an HL7 v3 "custodian" element.
     */
    static createCustodian(MarkupBuilder builder, String mpiSystemIdRoot, mpiSystemIdExtension) {
        builder.custodian(typeCode: 'CST') {
            assignedEntity(classCode: 'ASSIGNED') {
                buildInstanceIdentifier(builder, 'id', false, 
                        mpiSystemIdRoot, mpiSystemIdExtension)
            }
        }
    }
    
    
    /**
     * Some schemas prescribe the existence of an patientPerson element,
     * but we do not have any data to fill in there.
     */
    static void fakePatientPerson(MarkupBuilder builder) {
        builder.patientPerson(classCode: 'PSN', determinerCode: 'INSTANCE') {
            name(nullFlavor: 'UNK')
        }
    }
    
}