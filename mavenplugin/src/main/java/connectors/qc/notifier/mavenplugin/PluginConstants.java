/**
 * @author krvsingh
 */

package connectors.qc.notifier.mavenplugin;

public interface PluginConstants {

	String PLUGIN_PROPERTY_FAILBUILD = "fail-build-on-batchnotify-fail";
	String PLUGIN_PROPERTY_MOCK = "mock-without-fail";

	String PLUGIN_PROPERTY_BATCH_FILE = "batchfilepath";
	String PLUGIN_PROPERTY_QC_CONNECTION_FILE = "qcpropertiesfilepath";
	
	String PLUGIN_PROPERTY_WORKSPACE = "${basedir}/";
	String PLUGIN_PROPERTY_SUREFIRE = "${basedir}/target/surefire-reports/" ;
	
	String PLUGIN_PROPERTY_MAPFILE = "mapfilepath";
	String PLUGIN_PROPERTY_SUREFIREFILE = "surefirefilepath";
	
	String PLUGIN_PROPERTY_DEFAULT_MAPFILE = "xml-batch.map";
	String PLUGIN_PROPERTY_DEFAULT_SUREFIREFILE = "testng-results.xml";
}
