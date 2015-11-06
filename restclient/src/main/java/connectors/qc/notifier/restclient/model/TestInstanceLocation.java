/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.model;

public class TestInstanceLocation {

	private String testSetId = "";
	private String testId = "";
	private String testInstanceId = "";
	private String testOrderId = "";
	private String testType = "" ;
	

	public String getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(String testSetId) {
		this.testSetId = testSetId;
	}

	public String getTestId() {
		return testId;
	}

	public void setTestId(String testId) {
		this.testId = testId;
	}

	public String getTestInstanceId() {
		return testInstanceId;
	}
	
	public void setTestInstanceId(String testInstanceId) {
		this.testInstanceId = testInstanceId;
	}

	public String getTestOrderId() {
		return testOrderId;
	}

	public void setTestOrderId(String testOrderId) {
		this.testOrderId = testOrderId;
	}

	public String getTestType() {

		// if it contains QC package name also then return only class name
		if (testType.contains(".")) {
			return testType.substring(testType.lastIndexOf(".") + 1);
		}
		return testType;
	}

	public void setTestType(String testType) {
		this.testType = testType;
	}
	
}
