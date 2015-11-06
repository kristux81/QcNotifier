/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.model;


public interface QcRunStatus extends QcTestInstanceEntity {

	String Passed = "Passed";
	String Failed = "Failed";
	String Blocked = "Blocked";

	String NotCompleted = "Not Completed";
	String NA = "N/A";
	String NoRun = "No Run";
}
