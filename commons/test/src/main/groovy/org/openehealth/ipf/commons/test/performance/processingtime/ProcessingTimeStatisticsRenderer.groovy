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
package org.openehealth.ipf.commons.test.performance.processingtime

import groovy.xml.MarkupBuilder
import org.openehealth.ipf.commons.test.performance.Statistics
import org.openehealth.ipf.commons.test.performance.StatisticsRenderer
import static org.openehealth.ipf.commons.test.performance.utils.NumberUtils.format

/**
 * @author Mitko Kolev
 */
class ProcessingTimeStatisticsRenderer implements StatisticsRenderer {
	
	String render(Statistics model) {
		StringWriter writer = new StringWriter()
		MarkupBuilder builder = new MarkupBuilder(writer)
		List names = model.getMeasurementNames()
		
		builder.throughput(){
			h3('Processing time')
			table(border:'1'){
				th('Measurement location')
				th('Number of measurements')
				th('Min (ms)')
				th('Max (ms)')
				th('Mean (ms)')
				th('Standard deviation (ms)')
				for (name in names){
					def summary = model.getStatisticalSummaryByName(name)
					tr(){
						td(name)
						td(summary.n)
						td(format(summary.min, 0))
						td(format(summary.max, 0))
						td(format(summary.mean, 0))
						td(format(summary.standardDeviation, 0))
					}
				}
			}
		}
		writer.toString()
	}
	
}
