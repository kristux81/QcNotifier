package connectors.qc.notifier.shared;

/**
 * This interface regroup all constants of the TDNotifier module.
 * 
 * @author sMercier
 */
public interface TDNotifierConstants
{	
	
	/* TD NOTIFIER SERVER */
	String PROPERTY_TDN_SRV_HOST = "td.notifier.server.host";
	String PROPERTY_TDN_SRV_PORT = "td.notifier.server.port";
	
	String DEFAULT_TDN_SRV_HOST = "localhost";
	String DEFAULT_TDN_SRV_PORT = "8080";
	String DEFAULT_TDN_SRV_URL = "tdnotifier";
	
	
	/* TD BATCH WRITER */
	/** Output Batch File Name for BatchWriter */
	String PROPERTY_TD_BATCHFILE = "td.notifier.batchfile";
	
	/** Default output Batch File Name for BatchWriter */
	String DEFAULT_TD_BATCHFILE = "batch_test_runs";
	
	String DEFAULT_TD_BATCHFILE_SECTION = "TESTRUN";
	
	/** Override property file to rename test entity labels */
	String MAPPER_PROPERTY_FILE = "test_entity_mapper_file";
	
	/**
	 * If this property is set "true", the application will update 
	 * first Test-Instance if more than one test-instances of the 
	 * same test found in a given test-set, instead of reporting
	 * Error.
	 *  
	 */
	String PROPERTY_USE_FIRST_TESTINSTANCE = "use_first_instance";

	/**
	 * If this property is set "true", the application will create Test-Set
	 * and/or Test_instances if not found in the given Test-Set-Path, instead of
	 * reporting Error.
	 * 
	 */
	String PROPERTY_CREATE_IF_NOT_FOUND_MODE = "create_if_not_found";
}
