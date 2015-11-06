/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.model;


public interface QcTestInstanceRelations extends QcEntity {

	String ContainsTestSet = "contains-test-set.id" ;
	String RealizedByTest = "realized-by-test.name" ;
}
