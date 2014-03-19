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
 * Proai repository-specific data driver. 
 *
 * <p>
 * The driver retrieves data from DB tables 
 * </p>
 * @author <a href="https://github.com/shilob">Shilo Banihit</a>
 *
 */
class OaiDriver implements OAIDriver {
	
	private static final Logger logger = Logger.getLogger(OAIDriver.class)
	private static final String PROPERTY_PREFIX = "proai.OaiDriver"
	private static final String KEY_METADATAFORMAT = "metadataformat"
	private static final String KEY_IDENTITY = "identity"
	private static final String KEY_SETS = "sets"
	private static final String KEY_RECORDS = "records"
	private static final String KEY_CHANGE = "change"
	 
	def sql
	def ddls
	def ddl_check
	def statements_select
	def statements_init
	def init_sql
	def df_change
	
	

	/* (non-Javadoc)
	 * @see proai.driver.OAIDriver#close()
	 */
	public void close() throws RepositoryException {
		sql.close()
	}

	/**
	* Returns the date of the most recent DB-wide change. 
	*/
	public Date getLatestDate() throws RepositoryException {		
		def sqlStatement = statements_select[KEY_CHANGE]
		def row = sql.firstRow(sqlStatement)
		Date date = new Date()
		if (row.latestModDate) {
			logger.debug("Got latestModDate: ${row.latestModDate}")
			date = Date.parse(df_change, row.latestModDate)			
		} else {
			logger.error("SQL did not return a 'latestModDate' column, returning the current date.")
		}
		logger.debug("Returning date: ${date}")
		return date				
	}

	/**
	* The main entry point from Proai. The method expects the DB init properties as well as the SQL statements.
	*/
	public void init(Properties prop) throws RepositoryException {
		def connInfo= [url:prop.getProperty("${PROPERTY_PREFIX}.db.url"), user:prop.getProperty("${PROPERTY_PREFIX}.db.user"), password:prop.getProperty("${PROPERTY_PREFIX}.db.pw"), driver:prop.getProperty("${PROPERTY_PREFIX}.db.driver")]
		logger.debug(connInfo)
		sql = Sql.newInstance(connInfo)
		
		def tables = [KEY_METADATAFORMAT, KEY_IDENTITY, KEY_SETS, KEY_RECORDS, KEY_CHANGE]
		
		ddls = [:]
		ddl_check = [:]
		statements_init = [:]
		statements_select = [:]
		init_sql = prop.getProperty("${PROPERTY_PREFIX}.db.sql.init")
		df_change = prop.getProperty("${PROPERTY_PREFIX}.df.change")
		// get the statements
		tables.each {table-> 
			ddls[table] = prop.getProperty("${PROPERTY_PREFIX}.db.ddl.${table}")
			ddl_check[table] = prop.getProperty("${PROPERTY_PREFIX}.db.ddl.${table}.check")
			statements_init[table] = prop.getProperty("${PROPERTY_PREFIX}.db.sql.${table}.init")
			statements_select[table] = prop.getProperty("${PROPERTY_PREFIX}.db.sql.${table}.select")
		}
		if (init_sql) {
			logger.debug("Init sql provided, executing: ${init_sql}")
			sql.execute(init_sql)
		}
		// determine if we need to ddl and seed
		tables.each {table->
			if (ddl_check[table]) {
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
			} else {
				logger.debug("No ddl check for table: ${table}, skipping.")
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
		if (row) {
			writer.println(row.xmlEntry)
			logger.debug("Returned Identity: ${row.xmlEntry}")
		} else {
			logger.error("It seems that there is no identity record. Please insert a record.")
		}		
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
