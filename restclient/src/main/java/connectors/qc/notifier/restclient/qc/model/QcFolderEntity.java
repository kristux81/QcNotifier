/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.model;


public interface QcFolderEntity extends QcTestEntity {

	String TestFolderRoot = "Subject";
	String TestFolderRootParentId = "0";

	String TestSetFolderRoot = "Root";
	String TestSetFolderRootId = "0";
}
