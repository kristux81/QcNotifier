/**
 * @author krvsingh
 */
package connectors.qc.notifier.batchclient.parsers;

import java.util.Map;

/*
 * Common minimum agreement for a parser
 */
public abstract class FileParser {

	public Map<Integer, Map<String, String>> getAllSections() { return null ; }
	public Map<String, String> getSection( Integer section ) { return null ; }
	public void showAllSections() { System.err.println("Use a Proper Parser"); }
	
	public String getValue(Integer section, String key) { return null ; }
	public int getSectionCount() { return 0 ; }
	
}
