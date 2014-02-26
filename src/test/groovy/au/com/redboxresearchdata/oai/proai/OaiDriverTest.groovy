/**
 * 
 */
package au.com.redboxresearchdata.oai.proai

import static org.junit.Assert.*
import org.apache.log4j.Logger
import java.util.Properties

/**
 * @author Shilo Banihit
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
	}
}
