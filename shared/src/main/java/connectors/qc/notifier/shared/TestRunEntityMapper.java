/**
 * @author krvsingh
 */
package connectors.qc.notifier.shared;

import java.util.Properties;

public class TestRunEntityMapper extends EntityMapper {

	public String testSetName;
	public String testInstance;
	public String testStatus;
	public String testPath;
	public String testSetPath;

	public String testBasePath;
	public String testVersion;
	public String testMessage;

	private static TestRunEntityMapper singleton = null;

	public static TestRunEntityMapper getInstance(String propFile) {

		if (singleton == null) {

			if (propFile != null) {
				singleton = new TestRunEntityMapper(propFile);
			} else {
				singleton = new TestRunEntityMapper();
			}
		}

		return singleton;
	}

	private TestRunEntityMapper(String propFile) {

		super(propFile);
		setProperties(EntityMapper.getTestproperties());
	}

	private TestRunEntityMapper() {

		super();
		setProperties(EntityMapper.getTestproperties());
	}

	private void setProperties(Properties properties) {

		testSetName = properties.getProperty("_testSetName");
		testInstance = properties.getProperty("_testInstance");
		testStatus = properties.getProperty("_testStatus");
		testPath = properties.getProperty("_testPath");
		testSetPath = properties.getProperty("_testSetPath");

		testBasePath = properties.getProperty("_testBasePath");
		testVersion = properties.getProperty("_testVersion");
		testMessage = properties.getProperty("_testMessage");
	}
}
