/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.model;

public interface QcTypes {

	// Test Types
	String MANUAL = "MANUAL";
	String AUTOMATED = "VAPI-XP-TEST";
	String SYSTEM = "SYSTEM-TEST";
	
	// Test Instance Type QC package
	String INSTANCE = "hp.qc.test-instance." ;
	
	// Test Run Type QC package
	String RUN = "hp.qc.run." ;
	
	// Test Set Types
	String DEFAULT = "hp.qc.test-set.default";
	
}
