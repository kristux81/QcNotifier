package connectors.qc.notifier.restclient;


public final class TDRestClientConfig {

	private TDRestClientConfig(){
	}
	
	/**
	 * If set to true will create test-set and testInstance ( if not found )
	 * else on false, will fail reporting error ( if test-set and testInstance
	 * not found )
	 */
	private static boolean createIfNotFound = false;

	/**
	 * If set to true will assume testInstanceid = 1 in case testInstance id not
	 * provided. i.e. if set to true it will read testInstance name ABC as
	 * [1]ABC
	 * 
	 */
	private static boolean defaultFirstInstance = false;

	/**
	 * Enhance TDNotifier Operation
	 * 
	 * @param iscreateIfNotFound
	 *            : True means testSet and testInstance will be created if not
	 *            found instead of throwing exception
	 */
	public static void setCreateIfNotFound(boolean iscreateIfNotFound) {
		TDRestClientConfig.createIfNotFound = iscreateIfNotFound;
	}

	public static boolean isCreateIfNotFound() {
		return createIfNotFound;
	}

	/**
	 * Enhance TDNotifier Operation
	 * 
	 * @param isdefaultFirstInstance
	 *            : True means if testInstance Id not provided it will be
	 *            defaulted to 1
	 */
	public static void setDefaultFirstInstance(boolean isdefaultFirstInstance) {
		TDRestClientConfig.defaultFirstInstance = isdefaultFirstInstance;
	}

	public static boolean isDefaultFirstInstance() {
		return defaultFirstInstance;
	}
}
