/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.model;


public interface QcTestInstanceEntity extends QcEntity {

	String TestInstance = "test-instance";
	String TestID = "test-id";
	String TestOrder = "test-order" ;
	String CycleID = "cycle-id";
	String SubTypeID = "subtype-id";
	String Status = "status";
	String Tester = "actual-tester" ;
	String Owner = "owner";
	String ExecDate = "exec-date" ;
	String ExecTime = "exec-time" ;
}
