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
package org.openehealth.ipf.tutorials.xds

import static junit.framework.Assert.assertEquals
import static org.openehealth.ipf.commons.ihe.xds.responses.Status.*
import static org.openehealth.ipf.commons.ihe.xds.metadata.AvailabilityStatus.*

import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Arrays
import java.util.Date
import java.util.List
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

import javax.activation.DataHandler
import javax.mail.util.ByteArrayDataSource

import org.apache.commons.io.IOUtils
import org.apache.cxf.transport.servlet.CXFServlet
import org.junit.*
import org.openehealth.ipf.commons.ihe.xds.SampleData
import org.openehealth.ipf.platform.camel.ihe.xds.commons.StandardTestContainer
import org.openehealth.ipf.commons.ihe.xds.metadata.DocumentEntry
import org.openehealth.ipf.commons.ihe.xds.metadata.Identifiable
import org.openehealth.ipf.commons.ihe.xds.requests.ProvideAndRegisterDocumentSet
import org.openehealth.ipf.commons.ihe.xds.requests.QueryRegistry
import org.openehealth.ipf.commons.ihe.xds.requests.RegisterDocumentSet
import org.openehealth.ipf.commons.ihe.xds.requests.RetrieveDocument
import org.openehealth.ipf.commons.ihe.xds.requests.RetrieveDocumentSet
import org.openehealth.ipf.commons.ihe.xds.requests.query.FindDocumentsQuery
import org.openehealth.ipf.commons.ihe.xds.responses.QueryResponse
import org.openehealth.ipf.commons.ihe.xds.responses.Response
import org.openehealth.ipf.commons.ihe.xds.responses.RetrievedDocumentSet

/**
 * Tests for thread-safety.
 * @author Jens Riemschneider
 */
@Ignore // To avoid slowing down the normal build
class TestThreading extends StandardTestContainer {
    def ITI18 = "xds-iti18://localhost:${port}/xds-iti18"
    def ITI41 = "xds-iti41://localhost:${port}/xds-iti41"
    def ITI42 = "xds-iti42://localhost:${port}/xds-iti42"
    def ITI43 = "xds-iti43://localhost:${port}/xds-iti43"

    def docId = new AtomicLong()

    def taskCounter = new AtomicLong()

    @BeforeClass
    static void classSetUp() {
        startServer(new CXFServlet(), 'context.xml', false)
    }

    @Test
    void test() {
        def executorService = Executors.newFixedThreadPool(4)

        def tasks = new ArrayList<Callable<Throwable>>()
        for (def idx = 0; idx < 500; ++idx) {
            tasks += new Task(code: provideTask, container: this)
            tasks += new Task(code: registerTask, container: this) 
            tasks += new Task(code: retrieveTask, container: this)
        }
        
        def start = new Date().time
        def futures = executorService.invokeAll(tasks)
        def failedResults = new ArrayList<Throwable>()
        for (def future : futures) {
            def result = future.get(1, TimeUnit.HOURS)
            if (result != null) {
                failedResults += result
            }
        }
        def end = new Date().time
        println(Thread.currentThread().id + ' -> (' + taskCounter.getAndIncrement() + ') finished (' + (end - start) + 'ms)')

        for (def e : failedResults) {            
            e.printStackTrace()
        }
        assertEquals(failedResults.toString(), 0, failedResults.size())
    }
    
    def provideTask = {
        def provide = SampleData.createProvideAndRegisterDocumentSet()
        provide.documents[0].dataHandler = new DataHandler(new ByteArrayDataSource('test', 'text/plain'))
        def docEntry = provide.documents[0].documentEntry
        def patientId = docEntry.patientId
        patientId.id = UUID.randomUUID().toString()            
        docEntry.uniqueId = '4.3.2.1.' + docId.incrementAndGet()

        def response = send(ITI41, provide, Response.class)
        assertEquals(SUCCESS, response.status)

        def query = new FindDocumentsQuery()
        query.patientId = docEntry.patientId
        query.status = [APPROVED]
        def queryReg = new QueryRegistry(query)
        queryReg.returnLeafObjects = true
        def queryResponse = send(ITI18, queryReg, QueryResponse.class)
        assertEquals(SUCCESS, queryResponse.status)
        assertEquals(1, queryResponse.documentEntries.size())
        assertEquals(docEntry.uniqueId, queryResponse.documentEntries[0].uniqueId)
    }
    
    def registerTask = {
        def register = SampleData.createRegisterDocumentSet()
        def docEntry = register.documentEntries[0]
        def patientId = docEntry.patientId
        patientId.id = UUID.randomUUID().toString()
        docEntry.uniqueId = '4.3.2.1.' + docId.incrementAndGet()

        def response = send(ITI42, register, Response.class)
        assertEquals(response.toString(), SUCCESS, response.status)

        def query = new FindDocumentsQuery()
        query.patientId = docEntry.patientId
        query.status = [APPROVED]
        def queryReg = new QueryRegistry(query)
        queryReg.returnLeafObjects = true
        def queryResponse = send(ITI18, queryReg, QueryResponse.class)
        assertEquals(SUCCESS, queryResponse.status)
        assertEquals(1, queryResponse.documentEntries.size())
        assertEquals(docEntry.uniqueId, queryResponse.documentEntries[0].uniqueId)
    }        
    
    def retrieveTask = {
        def provide = SampleData.createProvideAndRegisterDocumentSet()
        provide.documents[0].dataHandler = new DataHandler(new ByteArrayDataSource('test', 'text/plain'))
        def docEntry = provide.documents[0].documentEntry
        def patientId = docEntry.patientId
        patientId.id = UUID.randomUUID().toString()
        docEntry.uniqueId = '4.3.2.1.' + docId.incrementAndGet()

        def response = send(ITI41, provide, Response.class)
        assertEquals(response.toString(), SUCCESS, response.status)

        def retrieve = new RetrieveDocumentSet()
        def doc1 = new RetrieveDocument()
        doc1.documentUniqueId = provide.documents[0].documentEntry.uniqueId
        doc1.repositoryUniqueId = 'something'
        retrieve.documents.add(doc1)
        retrieve.documents.add(doc1)
        def retrieveResponse = send(ITI43, retrieve, RetrievedDocumentSet.class)
        assertEquals(retrieveResponse.toString(), SUCCESS, retrieveResponse.status)
        assertEquals(2, retrieveResponse.documents.size())

        assertEquals('test', read(retrieveResponse.documents[0].dataHandler))
        assertEquals('test', read(retrieveResponse.documents[1].dataHandler))
    }

    def read(dataHandler) {
        def inputStream = dataHandler.inputStream
        try {
            return IOUtils.toString(inputStream)
        }
        finally {
            inputStream.close()
        }
    }
}