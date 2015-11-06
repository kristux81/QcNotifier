/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.model;

public class TestRunEntity {

	// External Members
	private String testSetName = "";
	private String testInstance = "";
	private String testStatus = "";
	private String testBasePath = "";
	private String testPath = "";
	private String testSetPath = "";
	private String testVersion = "";
	private String testMessage = "";
	private String testOwner = "";
	private String attachment = "" ; 

	// Internal Members
	private String testInstanceId;
	private String testName;
	

	public String getTestSetName() {
		return testSetName;
	}

	public void setTestSetName(String testSetName) {
		this.testSetName = testSetName;
	}

	public String getTestInstance() {
		return testInstance;
	}

	public void setTestInstance(String testInstance) {
		this.testInstance = testInstance;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}

	public String getTestBasePath() {
		return testBasePath;
	}

	public void setTestBasePath(String testBasePath) {
		this.testBasePath = testBasePath;
	}

	public String getTestPath() {
		return testPath;
	}

	public void setTestPath(String testPath) {
		this.testPath = testPath;
	}

	public String getTestSetPath() {
		return testSetPath;
	}

	public void setTestSetPath(String testSetPath) {
		this.testSetPath = testSetPath;
	}

	public String getTestVersion() {
		return testVersion;
	}

	public void setTestVersion(String testVersion) {
		this.testVersion = testVersion;
	}

	public String getTestMessage() {
		return testMessage;
	}

	public void setTestMessage(String testMessage) {
		this.testMessage = testMessage;
	}

	public String getTestInstanceId() {
		return testInstanceId;
	}

	public void setTestInstanceId(String testInstanceId) {
		this.testInstanceId = testInstanceId;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestOwner() {
		return testOwner;
	}

	public void setTestOwner(String testOwner) {
		this.testOwner = testOwner;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}
