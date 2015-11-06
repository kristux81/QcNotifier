/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.model;


public interface QcRunEntity extends QcTestInstanceEntity {

	String Name = "name";
	String SubTypeID = "subtype-id";
	String TestInstanceId = "test-instance";
	String TestCycleId = "testcycl-id";
	String Version = "vc-version-number" ;
	String Message = "comments" ;
	String ExecDate = "execution-date" ;
	String ExecTime = "execution-time" ;
}
