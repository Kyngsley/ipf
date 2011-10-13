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
package org.openehealth.ipf.modules.hl7dsl

import static org.openehealth.ipf.modules.hl7dsl.MessageAdapters.*
import ca.uhn.hl7v2.model.v22.message.ADT_A01
import ca.uhn.hl7v2.model.v231.message.ADT_A40

/**
 * @author Christian Ohr
 */
public class ShortcutSyntaxTest extends GroovyTestCase{

     MessageAdapter<ca.uhn.hl7v2.model.v22.message.ADT_A01> msg1
     MessageAdapter<ca.uhn.hl7v2.model.v25.message.ADT_A01> msg2
     MessageAdapter<ca.uhn.hl7v2.model.v24.message.ORU_R01> msg3
     
     void setUp() {
         msg1 = load('msg-01.hl7')
         msg2 = load('msg-06.hl7')
         msg3 = load('msg-02.hl7')
     }
     
     void testShortCut22() {
         assert 'Nachname' == msg1.NK1(0)[2][1].value
         assert 'Nachname' == msg1.NK1[2][1].value
         assert 'Nachname' == msg1.NK1[2].value
         
         // PID-5 in HL7v2 is not repeatable, and the family name component is a primitive (ST)
         assert 'Nachname' == msg1.PID[5][1].value
         assert 'Vorname'  == msg1.PID[5][2].value
         assert 'Nachname' == msg1.PID[5].value
     }
     
     void testShortCut25OnRepeatableSegment() {
         assert 'Nachname' == msg2.NK1(0)[2][1][1].value
         assert 'Nachname' == msg2.NK1(0)[2][1].value
         assert 'Nachname' == msg2.NK1[2][1].value
         assert 'Nachname' == msg2.NK1[2].value
         assert '333-4444' == msg2.NK1(0)[5](0)[1].value
         assert '333-5555' == msg2.NK1(0)[5](1)[1].value
         assert '333-4444' == msg2.NK1[5].value
         assert '333-5555' == msg2.NK1[5](1).value
     }
     
     void testShortCut25OnRepeatableField() {     
         // PID-5 in HL7v2 is repeatable, and the family name component is again a component (FN)
         assert 'Nachname' == msg2.PID[5](0)[1][1].value
         assert 'Nachname' == msg2.PID[5](0)[1].value
         assert 'Nachname' == msg2.PID[5][1].value
         assert 'Nachname' == msg2.PID[5].value
         assert 'Vorname'  == msg2.PID[5](0)[2].value
         assert 'Vorname'  == msg2.PID[5][2].value
         assert 'Vorname'  == msg2.PID[5][2].value
     }

     void testShortCut25OnRepeatableGroup() {     
         assert 'TEST' == msg3.PATIENT_RESULT(0).PATIENT.PID[5][1].value
         assert 'TEST' == msg3.PATIENT_RESULT.PATIENT.PID[5][1].value
         assert 'TEST' == msg3.PATIENT_RESULT.PATIENT.PID[5].value
     }
     
     void testWriteShortCutOnRepeatableField() {         
         // PID-5 in HL7v2 is repeatable, and the family name component is again a component (FN)
         msg2.PID[5](0)[1][1] = 'Nachname1'
         assert 'Nachname1' == msg2.PID[5][1].value
         msg2.PID[5][1][1] = 'Nachname2'
         assert 'Nachname2' == msg2.PID[5][1].value
         msg2.PID[5][1] = 'Nachname3'
         assert 'Nachname3' == msg2.PID[5][1].value
         msg2.PID[5] = 'Nachname4'
         assert 'Nachname4' == msg2.PID[5][1].value
         msg2.PID[5][1] = msg2.PID[5][2]
         assert 'Vorname' == msg2.PID[5][1].value
         msg2.PID[5][1] = msg2.NK1[2]
         assert 'Nachname' == msg2.PID[5][1].value
         msg2.PID[5] = msg2.NK1[2]
         assert 'Nachname' == msg2.PID[5][1].value
     }
     
     void testWriteShortCutOnRepeatableSegment() {         
         msg2.NK1(0)[2][1][1] = 'Nachname1'
         assert 'Nachname1' == msg2.NK1[2][1].value
         msg2.NK1(0)[2][1] = 'Nachname2'
         assert 'Nachname2' == msg2.NK1[2][1].value
         msg2.NK1[2][1] = 'Nachname3'
         assert 'Nachname3' == msg2.NK1[2][1].value
         msg2.NK1[2] = 'Nachname4'
         assert 'Nachname4' == msg2.NK1[2][1].value
     }
     
     void testUndesiredReplication() {
         ADT_A40 hapiMessage = new ADT_A40()
         MessageAdapter<ADT_A40> adt = new MessageAdapter(hapiMessage)
         def grp = adt.PIDPD1MRGPV1(0)
         grp.PID[5][1] = 'X'
         grp.PID[5][2] = 'Y'
		 assert adt.count('PIDPD1MRGPV1') == 1
		 assert adt.PIDPD1MRGPV1.PID[5][1].value == 'X'
   }      
}
