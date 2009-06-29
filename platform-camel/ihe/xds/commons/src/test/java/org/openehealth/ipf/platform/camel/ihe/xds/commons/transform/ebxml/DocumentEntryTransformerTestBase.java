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
package org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml;

import static org.junit.Assert.*;
import static org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.EbrsTestUtils.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.Classification;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.ExtrinsicObject;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.ebxml.Slot;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Address;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Author;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.AvailabilityStatus;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.DocumentEntry;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.EntryUUID;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.PatientInfo;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.UniqueID;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.metadata.Vocabulary;
import org.openehealth.ipf.platform.camel.ihe.xds.commons.transform.ebxml.DocumentEntryTransformer;

/**
 * Tests for {@link DocumentEntryTransformer}.
 * @author Jens Riemschneider
 */
public abstract class DocumentEntryTransformerTestBase {
    protected DocumentEntryTransformer transformer;
    private DocumentEntry documentEntry;
    
    @Before
    public final void baseSetUp() {
        Author author = new Author();
        author.setAuthorPerson(createPerson(1));
        author.getAuthorInstitution().add("inst1");
        author.getAuthorInstitution().add("inst2");
        author.getAuthorRole().add("role1");
        author.getAuthorRole().add("role2");
        author.getAuthorSpecialty().add("spec1");
        author.getAuthorSpecialty().add("spec2");
        
        Address address = new Address();
        address.setCity("city");
        address.setCountry("country");
        address.setCountyParishCode("countyParishCode");
        address.setOtherDesignation("otherDesignation");
        address.setStateOrProvince("stateOrProvince");
        address.setStreetAddress("streetAddress");
        address.setZipOrPostalCode("zipOrPostalCode");
        
        PatientInfo sourcePatientInfo = new PatientInfo();
        sourcePatientInfo.setAddress(address);
        sourcePatientInfo.setDateOfBirth("dateOfBirth");
        sourcePatientInfo.setGender("F");
        sourcePatientInfo.setName(createName(3));
        sourcePatientInfo.getIds().add(createIdentifiable(5));
        sourcePatientInfo.getIds().add(createIdentifiable(6));

        documentEntry = new DocumentEntry();
        documentEntry.setAuthor(author);
        documentEntry.setAvailabilityStatus(AvailabilityStatus.APPROVED);
        documentEntry.setClassCode(createCode(1));
        documentEntry.setComments(createLocal(10));
        documentEntry.setCreationTime("123");
        documentEntry.setEntryUUID(new EntryUUID("uuid"));
        documentEntry.setFormatCode(createCode(2));
        documentEntry.setHash("hash");
        documentEntry.setHealthcareFacilityTypeCode(createCode(3));
        documentEntry.setLanguageCode("languageCode");
        documentEntry.setLegalAuthenticator(createPerson(2));
        documentEntry.setMimeType("text/plain");
        documentEntry.setPatientID(createIdentifiable(3));
        documentEntry.setPracticeSettingCode(createCode(4));
        documentEntry.setServiceStartTime("234");
        documentEntry.setServiceStopTime("345");
        documentEntry.setSize(174L);
        documentEntry.setSourcePatientID(createIdentifiable(4));
        documentEntry.setSourcePatientInfo(sourcePatientInfo);
        documentEntry.setTitle(createLocal(11));
        documentEntry.setTypeCode(createCode(5));
        documentEntry.setUniqueID(new UniqueID("uniqueId"));
        documentEntry.setUri("uri");
        documentEntry.getConfidentialityCodes().add(createCode(6));
        documentEntry.getConfidentialityCodes().add(createCode(7));
        documentEntry.getEventCodeList().add(createCode(8));
        documentEntry.getEventCodeList().add(createCode(9));
    }

    @Test
    public void testToEbXML() {
        ExtrinsicObject ebXML = transformer.toEbXML(documentEntry);        
        assertNotNull(ebXML);
        
        assertEquals("Approved", ebXML.getStatus());
        assertEquals("text/plain", ebXML.getMimeType());
        assertEquals("uuid", ebXML.getId());
        assertEquals(Vocabulary.DOC_ENTRY_CLASS_NODE, ebXML.getObjectType());
        
        assertEquals(createLocal(10), ebXML.getDescription());        
        assertEquals(createLocal(11), ebXML.getName());
        
        List<Slot> slots = ebXML.getSlots();
        assertSlot(Vocabulary.SLOT_NAME_CREATION_TIME, slots, "123");
        assertSlot(Vocabulary.SLOT_NAME_HASH, slots, "hash");
        assertSlot(Vocabulary.SLOT_NAME_LANGUAGE_CODE, slots, "languageCode");
        assertSlot(Vocabulary.SLOT_NAME_SERVICE_START_TIME, slots, "234");
        assertSlot(Vocabulary.SLOT_NAME_SERVICE_STOP_TIME, slots, "345");
        assertSlot(Vocabulary.SLOT_NAME_SIZE, slots, "174");
        assertSlot(Vocabulary.SLOT_NAME_SOURCE_PATIENT_ID, slots, "id 4^^^namespace 4&uni 4&uniType 4");
        assertSlot(Vocabulary.SLOT_NAME_URI, slots, "1|uri");
        assertSlot(Vocabulary.SLOT_NAME_LEGAL_AUTHENTICATOR, slots, "id 2^familyName 2^givenName 2^prefix 2^second 2^suffix 2^^^namespace 2&uni 2&uniType 2");
        assertSlot(Vocabulary.SLOT_NAME_SOURCE_PATIENT_INFO, slots, 
                "PID-3|id 5^^^namespace 5&uni 5&uniType 5~id 6^^^namespace 6&uni 6&uniType 6",
                "PID-5|familyName 3^givenName 3^prefix 3^second 3^suffix 3",
                "PID-7|dateOfBirth",
                "PID-8|F",
                "PID-11|streetAddress^otherDesignation^city^stateOrProvince^zipOrPostalCode^country^^^countyParishCode");
        
        
        Classification classification = assertClassification(Vocabulary.DOC_ENTRY_AUTHOR_CLASS_SCHEME, ebXML, 0, "", -1);
        assertSlot(Vocabulary.SLOT_NAME_AUTHOR_PERSON, classification.getSlots(), "id 1^familyName 1^givenName 1^prefix 1^second 1^suffix 1^^^namespace 1&uni 1&uniType 1");
        assertSlot(Vocabulary.SLOT_NAME_AUTHOR_INSTITUTION, classification.getSlots(), "inst1", "inst2");
        assertSlot(Vocabulary.SLOT_NAME_AUTHOR_ROLE, classification.getSlots(), "role1", "role2");
        assertSlot(Vocabulary.SLOT_NAME_AUTHOR_SPECIALTY, classification.getSlots(), "spec1", "spec2");
        
        classification = assertClassification(Vocabulary.DOC_ENTRY_CLASS_CODE_CLASS_SCHEME, ebXML, 0, "code 1", 1);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 1");
        
        classification = assertClassification(Vocabulary.DOC_ENTRY_CONFIDENTIALITY_CODE_CLASS_SCHEME, ebXML, 0, "code 6", 6);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 6");

        classification = assertClassification(Vocabulary.DOC_ENTRY_CONFIDENTIALITY_CODE_CLASS_SCHEME, ebXML, 1, "code 7", 7);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 7");

        classification = assertClassification(Vocabulary.DOC_ENTRY_EVENT_CODE_CLASS_SCHEME, ebXML, 0, "code 8", 8);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 8");

        classification = assertClassification(Vocabulary.DOC_ENTRY_EVENT_CODE_CLASS_SCHEME, ebXML, 1, "code 9", 9);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 9");
        
        classification = assertClassification(Vocabulary.DOC_ENTRY_FORMAT_CODE_CLASS_SCHEME, ebXML, 0, "code 2", 2);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 2");

        classification = assertClassification(Vocabulary.DOC_ENTRY_HEALTHCARE_FACILITY_TYPE_CODE_CLASS_SCHEME, ebXML, 0, "code 3", 3);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 3");
        
        classification = assertClassification(Vocabulary.DOC_ENTRY_PRACTICE_SETTING_CODE_CLASS_SCHEME, ebXML, 0, "code 4", 4);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 4");

        classification = assertClassification(Vocabulary.DOC_ENTRY_TYPE_CODE_CLASS_SCHEME, ebXML, 0, "code 5", 5);
        assertSlot(Vocabulary.SLOT_NAME_CODING_SCHEME, classification.getSlots(), "scheme 5");
        
        assertExternalIdentifier(Vocabulary.DOC_ENTRY_PATIENT_ID_EXTERNAL_ID, ebXML, 
                "id 3^^^namespace 3&uni 3&uniType 3", Vocabulary.DOC_ENTRY_LOCALIZED_STRING_PATIENT_ID);

        assertExternalIdentifier(Vocabulary.DOC_ENTRY_UNIQUE_ID_EXTERNAL_ID, ebXML, 
                "uniqueId", Vocabulary.DOC_ENTRY_LOCALIZED_STRING_UNIQUE_ID);
        
        assertEquals(10, ebXML.getClassifications().size());
        assertEquals(10, ebXML.getSlots().size());
        assertEquals(2, ebXML.getExternalIdentifiers().size());
    }

    @Test
    public void testToEbXMLNull() {
        assertNull(transformer.toEbXML(null));
    }
   
    @Test
    public void testToEbXMLEmpty() {
        ExtrinsicObject ebXML = transformer.toEbXML(new DocumentEntry());        
        assertNotNull(ebXML);
        
        assertNull(ebXML.getStatus());
        assertNull(ebXML.getMimeType());
        assertNull(ebXML.getId());
        
        assertNull(ebXML.getDescription());        
        assertNull(ebXML.getName());
        
        assertEquals(0, ebXML.getSlots().size());
        assertEquals(0, ebXML.getClassifications().size());
        assertEquals(0, ebXML.getExternalIdentifiers().size());
    }
    
    
    
    @Test
    public void testFromEbXML() {
        ExtrinsicObject ebXML = transformer.toEbXML(documentEntry);
        DocumentEntry result = transformer.fromEbXML(ebXML);
        
        assertNotNull(result);
        assertEquals(documentEntry.toString(), result.toString());
    }
    
    @Test
    public void testFromEbXMLNull() {
        assertNull(transformer.fromEbXML(null));
    }
    
    @Test
    public void testFromEbXMLEmpty() {
        ExtrinsicObject ebXML = transformer.toEbXML(new DocumentEntry());        
        DocumentEntry result = transformer.fromEbXML(ebXML);
        assertEquals(new DocumentEntry(), result);
    }
}