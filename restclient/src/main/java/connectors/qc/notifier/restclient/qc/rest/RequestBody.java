/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.rest;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import connectors.qc.notifier.restclient.model.TestInstanceLocation;
import connectors.qc.notifier.restclient.model.TestRunEntity;
import connectors.qc.notifier.restclient.model.TestRunEntityCustom;
import connectors.qc.notifier.restclient.qc.model.QcFolderEntity;
import connectors.qc.notifier.restclient.qc.model.QcRunEntity;
import connectors.qc.notifier.restclient.qc.model.QcRunStatus;
import connectors.qc.notifier.restclient.qc.model.QcTestInstanceEntity;
import connectors.qc.notifier.restclient.qc.model.QcTestSetEntity;
import connectors.qc.notifier.restclient.qc.model.QcTypes;

public final class RequestBody {

	private RequestBody() {
	}

	private static String getRunName(TestRunEntity runEntity) {

		return "TDNotifier_" + runEntity.getTestSetName() + "_"
				+ runEntity.getTestInstance() + "_"
				+ new SimpleDateFormat("MM-dd_HH-mm-ss").format(new Date());
	}

	/**
	 * Test Instance PUT Request Body
	 */
	public static byte[] getTestInstancePut(String status, String execDate,
			String execTime) {

		StringBuilder builder = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>");

		builder.append("<Entity Type=\"test-instance\"><Fields>");
		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.Status, status));
		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.ExecDate,
				execDate));
		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.ExecTime,
				execTime));
		builder.append("</Fields></Entity>");

		return builder.toString().getBytes(Charset.forName("UTF-8"));
	}

	/**
	 * Test Instance POST Request Body
	 */
	public static byte[] getTestInstancePost(
			TestInstanceLocation instanceLocation, String owner) {

		StringBuilder builder = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>");

		builder.append("<Entity Type=\"test-instance\"><Fields>");
		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.CycleID,
				instanceLocation.getTestSetId()));
		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.SubTypeID,
				QcTypes.INSTANCE + instanceLocation.getTestType()));

		// Run status must be "NO Run" initially
		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.Status,
				QcRunStatus.NoRun));

		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.TestID,
				instanceLocation.getTestId()));
		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.TestOrder,
				instanceLocation.getTestOrderId()));
		builder.append(RestXmlUtils.fieldXml(QcTestInstanceEntity.Owner,
				owner));
		builder.append("</Fields></Entity>");

		return builder.toString().getBytes(Charset.forName("UTF-8"));
	}

	/**
	 * RUN POST Request Body
	 */
	public static byte[] getTestRunPost(TestInstanceLocation instanceLocation,
			TestRunEntity runEntity, TestRunEntityCustom customRunEntity) {

		String runName = getRunName(runEntity);

		StringBuilder builder = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>");

		builder.append("<Entity Type=\"run\"><Fields>");
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.Name, runName));
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.SubTypeID, QcTypes.RUN
				+ instanceLocation.getTestType()));
		
		// for the test run the status will be changed at later stage via PUT
		// (in order to trigger automatic change of test-instance status )
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.Status, QcRunStatus.NotCompleted));
		
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.TestInstanceId,
				instanceLocation.getTestInstanceId()));
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.TestID,
				instanceLocation.getTestId()));
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.TestCycleId,
				instanceLocation.getTestInstanceId()));
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.CycleID,
				instanceLocation.getTestSetId()));
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.Owner,
				runEntity.getTestOwner()));
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.Message,
				runEntity.getTestMessage()));

		if (customRunEntity != null) {
			for (Map.Entry<String, String> entry : customRunEntity
					.getAllFields().entrySet()) {
				builder.append(RestXmlUtils.fieldXml(entry.getKey().toString(),
						entry.getValue().toString()));
			}
		}

		builder.append("</Fields></Entity>");

		return builder.toString().getBytes(Charset.forName("UTF-8"));
	}

	
	/**
	 * RUN PUT Request Body
	 */
	public static byte[] getTestRunPut(String status) {

		StringBuilder builder = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>");

		builder.append("<Entity Type=\"run\"><Fields>");
		builder.append(RestXmlUtils.fieldXml(QcRunEntity.Status, status));
		builder.append("</Fields></Entity>");

		return builder.toString().getBytes(Charset.forName("UTF-8"));
	}
	
	/**
	 * TestSet POST Request Body
	 */
	public static byte[] getTestSetPost(String testSetName,
			String testSetFolderId) {

		StringBuilder builder = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>");

		builder.append("<Entity Type=\"test-set\"><Fields>");
		builder.append(RestXmlUtils.fieldXml(QcTestSetEntity.Name, testSetName));

		builder.append(RestXmlUtils.fieldXml(QcTestSetEntity.SubTypeID,
				QcTypes.DEFAULT));
		builder.append(RestXmlUtils.fieldXml(QcTestSetEntity.ParentID,
				testSetFolderId));
		builder.append("</Fields></Entity>");

		return builder.toString().getBytes(Charset.forName("UTF-8"));
	}

	/**
	 * TestSet Folder POST Request Body
	 */
	public static byte[] getTestSetFolderPost(String testSetFolderName,
			String testSetFolderParentId) {

		StringBuilder builder = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>");

		builder.append("<Entity Type=\"test-set-folder\"><Fields>");
		builder.append(RestXmlUtils.fieldXml(QcFolderEntity.Name,
				testSetFolderName));

		builder.append(RestXmlUtils.fieldXml(QcFolderEntity.ParentID,
				testSetFolderParentId));
		builder.append("</Fields></Entity>");

		return builder.toString().getBytes(Charset.forName("UTF-8"));
	}

}
