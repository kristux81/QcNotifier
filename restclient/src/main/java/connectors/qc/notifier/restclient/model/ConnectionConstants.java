package connectors.qc.notifier.restclient.model;

public interface ConnectionConstants {

	String DEFAULT_TD_PROPERTY_FILE = "qc.connection.properties";
	String DEFAULT_TD_URL = "http://alm:8080/qcbin/";
	String DEFAULT_TD_BASEPATH = "Root/";
	
	String PROPERTY_TD_URL = "td.url";
	String PROPERTY_TD_DOMAIN = "td.domain";
	String PROPERTY_TD_PROJECT = "td.project";
	String PROPERTY_TD_BASEPATH = "td.basepath";
	String PROPERTY_TD_USER = "td.user"; 
	String PROPERTY_TD_PASSWORD = "td.password"; 
	String PROPERTY_CUSTOM_OS = "td.custom.os";
}
