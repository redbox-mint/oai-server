/**
 * 
 */
package au.com.redboxresearchdata.oai.proai

import java.io.PrintWriter

import proai.SetInfo;
import proai.error.ServerException;

/**
 * @author Shilo Banihit
 *
 */
class SetInfoImpl implements SetInfo {

	String spec
	String xmlEntry
	
	public SetInfoImpl(String spec, String xmlEntry) {
		this.spec = spec
		this.xmlEntry = xmlEntry
	}
	public void write(PrintWriter writer) throws ServerException {
		writer.println(xmlEntry)
	}
	
	public String getSetSpec() {
		return spec;
	}

}
