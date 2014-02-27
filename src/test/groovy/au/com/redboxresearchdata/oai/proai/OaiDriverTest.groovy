/*******************************************************************************
 *Copyright (C) 2014 Queensland Cyber Infrastructure Foundation (http://www.qcif.edu.au/)
 *
 *This program is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation; either version 2 of the License, or
 *(at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License along
 *with this program; if not, write to the Free Software Foundation, Inc.,
 *51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 ******************************************************************************/
package au.com.redboxresearchdata.oai.proai

import static org.junit.Assert.*
import org.apache.log4j.Logger
import java.util.Properties

/**
 * Main test for OaiDriver.
 * 
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class OaiDriverTest extends GroovyTestCase {
	private static final Logger logger = org.apache.log4j.Logger.getLogger(OaiDriverTest.class)
	private static final String PROPERTY_PREFIX = "proai.OaiDriver"
	
	void testOaiDriverMethods() {
		def testDir = new File("target/test").deleteDir()
		Properties properties = new Properties()
		properties.load(new FileReader(new File("src/test/resources/test.properties")))
					
		OaiDriver driver = new OaiDriver()
		driver.init(properties)
		def mdFormats = driver.listMetadataFormats()
		assertNotNull(mdFormats)
		assertNotEquals(0, mdFormats.size())
		
		def sets = driver.listSetInfo()
		assertNotNull(sets)
		assertNotEquals(0, sets.size())
		
		def records = driver.listRecords(new Date(), new Date(), "init-seed")
		assertNotNull(records)
		assertNotEquals(0, records.size())
		
		StringWriter writer = new StringWriter()
		PrintWriter pwriter = new PrintWriter(writer)		
		driver.writeRecordXML("1", "eac-cpf", "init-seed", pwriter)
		pwriter.flush()		
		String content = writer.toString()
		logger.debug("Record: ${content}")
		assertNotEquals(0, content.length())
		pwriter.close()
		
		writer = new StringWriter()
		pwriter = new PrintWriter(writer)
		driver.write(pwriter)
		pwriter.flush()
		content = writer.toString()
		logger.debug("Identity: ${content}")
		assertNotEquals(0, content.length())
		
		def date = driver.getLatestDate()
		logger.debug("Latest date: ${date}")
		assertNotNull(date)
	}
}
