/**
 * 
 */
package au.com.redboxresearchdata.oai.proai

import proai.Record

/**
 * @author Shilo Banihit
 *
 */
class RecordImpl implements Record {
	
	String itemId
	String mdPrefix
	String sourceInfo
	
	public RecordImpl(String itemId, String mdPrefix, String sourceInfo) {
		this.itemId = itemId
		this.mdPrefix = mdPrefix
		this.sourceInfo = sourceInfo	
	}

	/* (non-Javadoc)
	 * @see proai.Record#getItemID()
	 */
	public String getItemID() {
		return itemId
	}

	/* (non-Javadoc)
	 * @see proai.Record#getPrefix()
	 */
	public String getPrefix() {
		return mdPrefix
	}

	/* (non-Javadoc)
	 * @see proai.Record#getSourceInfo()
	 */
	public String getSourceInfo() {
		return sourceInfo
	}

}
