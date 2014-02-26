/**
 * 
 */
package au.com.redboxresearchdata.oai.proai

import groovy.sql.Sql;

import java.io.PrintWriter
import java.util.Date;
import java.util.Properties;

import proai.MetadataFormat;
import proai.Record;
import proai.SetInfo;
import proai.driver.OAIDriver;
import proai.driver.RemoteIterator;
import proai.driver.impl.MetadataFormatImpl
import proai.driver.impl.RemoteIteratorImpl
import proai.error.RepositoryException;

import org.apache.log4j.Logger;

/**
 * @author Shilo Banihit
 *
 */
class OaiDriver implements OAIDriver {
	
	private static final Logger logger = Logger.getLogger(OAIDriver.class)
	private static final String PROPERTY_PREFIX = "proai.OaiDriver"
	private static final String KEY_METADATAFORMAT = "metadataformat"
	private static final String KEY_IDENTITY = "identity"
	private static final String KEY_SETS = "sets"
	private static final String KEY_RECORDS = "records"
	 
	def sql
	def ddls
	def ddl_check
	def statements_select
	def statements_init
	
	

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#close()
	 */
	public void close() throws RepositoryException {
		sql.close()
	}

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#getLatestDate()
	 */
	public Date getLatestDate() throws RepositoryException {
		return new Date()
	}

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#init(java.util.Properties)
	 */
	public void init(Properties prop) throws RepositoryException {
		def connInfo= [url:prop.getProperty("${PROPERTY_PREFIX}.db.url"), user:prop.getProperty("${PROPERTY_PREFIX}.db.user"), password:prop.getProperty("${PROPERTY_PREFIX}.db.pw"), driver:prop.getProperty("${PROPERTY_PREFIX}.db.driver")]
		logger.debug(connInfo)
		sql = Sql.newInstance(connInfo)
		
		def tables = [KEY_METADATAFORMAT, KEY_IDENTITY, KEY_SETS, KEY_RECORDS]
		
		ddls = [:]
		ddl_check = [:]
		statements_init = [:]
		statements_select = [:]
		// get the statements
		tables.each {table-> 
			ddls[table] = prop.getProperty("${PROPERTY_PREFIX}.db.ddl.${table}")
			ddl_check[table] = prop.getProperty("${PROPERTY_PREFIX}.db.ddl.${table}.check")
			statements_init[table] = prop.getProperty("${PROPERTY_PREFIX}.db.sql.${table}.init")
			statements_select[table] = prop.getProperty("${PROPERTY_PREFIX}.db.sql.${table}.select")
		}						
		// determine if we need to ddl and seed
		tables.each {table->
			if (!tableExists(ddl_check[table])) {
				logger.debug("DDL Executing: ${ddls[table]}")
				sql.execute(ddls[table])
				logger.debug("Verifying...")
				if (!tableExists(ddl_check[table])) {
					logger.error("Failed to create table: ${ddls[table]}")
					return					
				}
				if (statements_init[table]) {
					// seeding data...
					logger.debug("Seeding data...")
					sql.execute(statements_init[table])
				}
			}			
		}				
	}
	
	def tableExists(ddlCheck) {
		def rows = sql.firstRow(ddlCheck)
		if (rows && rows.size() > 0) {
			logger.debug("ExistsFlag: ${rows.existsFlag}")			
			if (rows.existsFlag || rows.existsflag) {
				logger.debug("Table exists: ${ddlCheck}")
				return true
			} 
		} else {
			logger.error("DDL Check failed, no rows: ${ddlCheck}")
 		}
		return false
	}

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#listMetadataFormats()
	 */
	public RemoteIterator<? extends MetadataFormat> listMetadataFormats() throws RepositoryException {
		def sqlStatement = statements_select[KEY_METADATAFORMAT]
		logger.debug("Executing: ${sqlStatement}")		
		def rows = sql.rows(sqlStatement)		
		def list = new ArrayList<MetadataFormat>() 
		rows.each{row->
			list << new MetadataFormatImpl(row["metadataPrefix"], row["schemaTxt"], row["metadataNamespace"])
			logger.debug("Return MetadataFormat....prefix: '${row.metadataPrefix}', schema:'${row.schemaTxt}', namespace:'${row.metadataNamespace}'") 
		}
		return new RemoteIteratorImpl<MetadataFormat>(list.iterator())		
	}

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#listRecords(java.util.Date, java.util.Date, java.lang.String)
	 */
	public RemoteIterator<? extends Record> listRecords(Date from, Date until, String mdPrefix) throws RepositoryException {
		def list = new ArrayList<Record>()
		def sqlStatement = statements_select[KEY_RECORDS]
		logger.debug("Executing: ${sqlStatement}")
		def rows = sql.rows(sqlStatement)
		rows.each{row->
			list << new RecordImpl(row.id.toString(), row.metadataPrefix, row.source)
			logger.debug("Return Record....metadataPrefix: '${row.metadataPrefix}', source:'${row.source}")
		}
		return new RemoteIteratorImpl<Record>(list.iterator())
	}

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#listSetInfo()
	 */
	public RemoteIterator<? extends SetInfo> listSetInfo()
			throws RepositoryException {
		def list = new ArrayList<SetInfo>()
		def sqlStatement = statements_select[KEY_SETS]
		logger.debug("Executing: ${sqlStatement}")
		def rows = sql.rows(sqlStatement)
		rows.each {row->
			list << new SetInfoImpl(row.spec, row.xmlEntry)
			logger.debug("Returned Set... spec:'${row.spec}', xmlEntry:'${row.xmlEntry}'")
		}
		return new RemoteIteratorImpl<SetInfo>(list.iterator())
	}

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#write(java.io.PrintWriter)
	 */
	public void write(PrintWriter writer) throws RepositoryException {		
		def sqlStatement = statements_select[KEY_IDENTITY]
		logger.debug("Executing: ${sqlStatement}")
		def row = sql.firstRow(sqlStatement)
		writer.println(row.xmlEntry)
		logger.debug("Returned Identity: ${row.xmlEntry}")		
	}

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#writeRecordXML(java.lang.String, java.lang.String, java.lang.String, java.io.PrintWriter)
	 */
	public void writeRecordXML(String itemId, String mdPrefix, String sourceInfo, PrintWriter writer) throws RepositoryException {
		logger.debug("Asking to return Record... itemId:'${itemId}', mdPrefix:'${mdPrefix}', sourceInfo:'${sourceInfo}'")
		def sqlStatement = "${statements_select[KEY_RECORDS]} WHERE id=?"
		logger.debug("Executing: ${sqlStatement}")
		def row = sql.firstRow(sqlStatement, Integer.parseInt(itemId))
		logger.debug("xmlEntry: ${row.xmlEntry}")		  			
		writer.println(row.xmlEntry)
	}

}
