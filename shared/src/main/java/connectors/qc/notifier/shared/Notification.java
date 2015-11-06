package connectors.qc.notifier.shared;

import java.util.Arrays;

public class Notification {

	/** Identifier to send this type of object by REST call */
	public static final String NOTIFICATION_IDF = "notification";

	private String testSetPath = null;

	/** The full path of the test to notify */
	private String testPath = null;

	/** The name of the group which contains the test to notify */
	private String testSet = null;

	/** The name of the instance of the test to notify */
	private String testInstance = null;

	/** The status of the test to report in TD */
	private String testStatus = null;

	/** The version of the test to report in TD */
	private String testVersion = null;

	/** The message of the test to report in TD */
	private String testMessage = null;

	private String tdURL;

	private String tdUser;

	private String tdPassword;

	private String tdDomain;

	private String tdProject;

	public Notification() {
		this(null, null, null, null, null, null, null, null, null, null, null, null);
	}
	
	public void setTestSetPath(String testSetPath) {
		this.testSetPath = testSetPath;
	}

	public void setTestPath(String testPath) {
		this.testPath = testPath;
	}

	public void setTestSet(String testSet) {
		this.testSet = testSet;
	}

	public void setTestInstance(String testInstance) {
		this.testInstance = testInstance;
	}

	public void setTestStatus(String testStatus) {
		this.testStatus = testStatus;
	}

	public void setTestVersion(String testVersion) {
		this.testVersion = testVersion;
	}

	public void setTestMessage(String testMessage) {
		this.testMessage = testMessage;
	}

	public void setTdURL(String tdURL) {
		this.tdURL = tdURL;
	}

	public void setTdUser(String tdUser) {
		this.tdUser = tdUser;
	}

	public void setTdPassword(String tdPassword) {
		this.tdPassword = tdPassword;
	}

	public void setTdDomain(String tdDomain) {
		this.tdDomain = tdDomain;
	}

	public void setTdProject(String tdProject) {
		this.tdProject = tdProject;
	}

	public Notification(String tdURL, String tdUser, String tdPassword,
			String tdDomain, String tdProject, String testSetPath,
			String testPath, String testSet, String testInstance,
			String testStatus, String testVersion, String testMessage) {

		this.tdURL = tdURL;
		this.tdDomain = tdDomain;
		this.tdProject = tdProject;
		this.tdUser = tdUser;
		this.tdPassword = tdPassword;

		this.testSetPath = testSetPath;
		this.testSet = testSet;
		this.testInstance = testInstance;
		this.testStatus = testStatus;
		this.testPath = testPath;
		this.testVersion = testVersion;
		this.testMessage = testMessage;
	}

	/**
	 * Connection Param Getters
	 * 
	 * @return
	 */
	public String getTdURL() {
		return tdURL;
	}

	public String getTdUser() {
		return tdUser;
	}

	public String getTdPassword() {
		return tdPassword;
	}

	public String getTdDomain() {
		return tdDomain;
	}

	public String getTdProject() {
		return tdProject;
	}

	/**
	 * Test Param Getters
	 * 
	 * @return
	 */

	public String getTestSetPath() {
		return testSetPath;
	}

	public String getTestSet() {
		return testSet;
	}

	public String getTestInstance() {
		return testInstance;
	}

	public String getTestStatus() {
		return testStatus;
	}

	public String getTestVersion() {
		return testVersion;
	}

	public String getTestMessage() {
		return testMessage;
	}

	public String getTestPath() {
		return testPath;
	}

	public String[] getConnectionParams() {

		return new String[] { tdURL, tdDomain, tdProject, tdUser, tdPassword };
	}

	public String[] getTestParams() {

		return new String[] { testSet, testInstance, testStatus, testSetPath,
				testPath, testVersion, testMessage };
	}
	
	 // serialize the object
	public String serialize(){
		
		// connection Params
		String[] conn = this.getConnectionParams();
		String connStr = Arrays.toString(conn) ;
		connStr = connStr.substring(1, connStr.length()-1);
		
		// test Params
		String[] test = this.getTestParams();
		String testStr = Arrays.toString(test) ;
		testStr = testStr.substring(1, testStr.length()-1);
			
	  return testStr + ", " + connStr ;
	}

	// deserialize the object
	public static Notification deSerialize(String input, boolean GlobalUser){

		Notification  notif = null;
		String[] fields = input.split(", ");

		if((GlobalUser && fields.length == 10) || fields.length == 12){
			notif = new Notification();
		} 
		
		if(notif == null){
			System.err.println("[NOTIFICATION] : QC Connection or Test Parameter(s) Missing.");
		}else {
			
			// test params
			notif.setTestSet(fields[0]);
			notif.setTestInstance(fields[1]);
			notif.setTestStatus(fields[2]);
			notif.setTestSetPath(fields[3]);
			notif.setTestPath(fields[4]);
			notif.setTestVersion(fields[5]);
			notif.setTestMessage(fields[6]);
			
			// connection params
			notif.setTdURL(fields[7]);
			notif.setTdDomain(fields[8]);
			notif.setTdProject(fields[9]);
			
			if(!GlobalUser){
				notif.setTdUser(fields[10]);
				notif.setTdPassword(fields[11]);
			}
		}

	  return notif ;
	}
	
}
