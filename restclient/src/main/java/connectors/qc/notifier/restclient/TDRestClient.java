/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.commons.FileUtils;
import connectors.qc.notifier.restclient.commons.StringUtils;
import connectors.qc.notifier.restclient.model.ConnectionConstants;
import connectors.qc.notifier.restclient.model.TestInstanceLocation;
import connectors.qc.notifier.restclient.model.TestRunEntity;
import connectors.qc.notifier.restclient.model.TestRunEntityCustom;
import connectors.qc.notifier.restclient.model.TestRunEntityException;
import connectors.qc.notifier.restclient.qc.model.QcEntitites;
import connectors.qc.notifier.restclient.qc.model.QcExceptionEntity;
import connectors.qc.notifier.restclient.qc.model.QcRunEntity;
import connectors.qc.notifier.restclient.qc.model.QcRunStatus;
import connectors.qc.notifier.restclient.qc.model.QcTestEntity;
import connectors.qc.notifier.restclient.qc.model.QcTestInstanceEntity;
import connectors.qc.notifier.restclient.qc.model.QcTestInstanceRelations;
import connectors.qc.notifier.restclient.qc.model.QcTestSetEntity;
import connectors.qc.notifier.restclient.qc.rest.ConnectionManager;
import connectors.qc.notifier.restclient.qc.rest.QCException;
import connectors.qc.notifier.restclient.qc.rest.RequestBody;
import connectors.qc.notifier.restclient.qc.rest.Response;
import connectors.qc.notifier.restclient.qc.rest.RestClient;
import connectors.qc.notifier.restclient.qc.rest.RestException;
import connectors.qc.notifier.restclient.qc.rest.RestQueryBuilder;
import connectors.qc.notifier.restclient.qc.rest.RestXPathUtils;
import connectors.qc.notifier.restclient.qc.rest.RestXmlUtils;

public class TDRestClient implements ITDClient {

	private ConnectionManager manager = null;
	private TestRunEntity runEntity = null;
	private TestRunEntityCustom runEntityCustom = null;
	private static int urlId = 0;

	private static final Logger LOG = Logger.getLogger(TDRestClient.class
			.getName());

	public ConnectionManager getManager() {
		return manager;
	}

	/**
	 * Called by Console BatchClient
	 */
	public TDRestClient() {

		// Log into QC and hold connection
		manager = new ConnectionManager();
	}

	/**
	 *  
	 * @param qcPropFile
	 *            : path to qc.connection.properties
	 */
	public TDRestClient(String qcPropFile) {

		// Log into QC and hold connection
		manager = new ConnectionManager(qcPropFile);
	}

	/**
	 * 
	 * @param connParams
	 * @param connID
	 * @throws RestException
	 */
	public TDRestClient(String[] connParams, String connID)
			throws RestException {

		// Create a Connection Manager Wrapper over REST client instance and
		// login to QC
		manager = new ConnectionManager(new RestClient(connParams[0],
				connParams[1], connParams[2], connParams[3]), connParams[3],
				connParams[4]);

		manager.setConnectionID(connID);
		LOG.info(String.format(
				"Attaching Connection with ID = %s to TDRESTCLIENT", connID));
	}

	/**
	 * Helper Function for reLogin to QC this login request will validate
	 * authentication if auth fails ( session expired ) it will try to relogin
	 */
	public void refresh() {
		manager.login();
	}

	/**
	 * Helper Function for logout from QC
	 */
	public void shutdown() {

		String connId = manager.getConnectionID();
		if (StringUtils.isDefined(connId)) {
			LOG.info(String.format("Closing Connection with ID = %s", connId));
		}
		manager.logout();
	}

	public void setRunEntityCustom(TestRunEntityCustom customRunEntity) {
		this.runEntityCustom = customRunEntity;
	}
	
	public TestRunEntityCustom getRunEntityCustom() {
		return runEntityCustom;
	}

	/**
	 * Logger & Exception Handler
	 * 
	 * @param response
	 * @param throwException
	 */
	public void handleQCException(Response response, boolean throwException) {

		handleQCException(response, throwException, Level.SEVERE);
	}

	/**
	 * Logger & Exception Handler
	 * 
	 * @param response
	 * @param throwException
	 * @param level
	 */
	private void handleQCException(Response response, boolean throwException,
			Level level) {

		String exceptionMsg = String.format("HTTP %s : [%s]", response
				.getStatusCode(), RestXPathUtils.getQCExceptionElementText(
				response.toString(), QcExceptionEntity.Title));

		if (throwException) {
			throw new QCException(exceptionMsg, response.getFailure());
		} else {
			LOG.log(level, exceptionMsg);
		}
	}

	/**
	 * Post a new Test Set if not found
	 * 
	 * @return Id of newly created Test Set
	 * @throws QCException
	 *             if failed to POST (with error details from QC )
	 */
	private String postTestSet(String testSetFolderId) {

		String testsetId = null;
		final String testSetName = runEntity.getTestSetName();

		final byte[] body = RequestBody.getTestSetPost(testSetName,
				testSetFolderId);
		final String url = manager.getClientInstance().buildRestRequest(
				QcEntitites.TestSets);
		final Response response = manager.getClientInstance().httpPost(url,
				body);

		if (response.isOk()) {

			testsetId = RestXPathUtils.getAttributeValueSingle(
					response.toString(), QcTestSetEntity.ID);

			LOG.info(String
					.format("TestSet : [%s] CREATED with ID : %s at testSetPath : [%s]",
							testSetName, testsetId, runEntity.getTestSetPath()));
		} else {
			handleQCException(response, false);
		}

		return testsetId;
	}

	/**
	 * Post a new Test Instance if not found
	 * 
	 * @return Id of newly created Test Instance
	 * @throws QCException
	 *             if failed to POST (with error details from QC )
	 */
	private String postTestInstance(TestInstanceLocation instanceLocation) {

		String testInstanceId = null;

		final byte[] body = RequestBody.getTestInstancePost(instanceLocation,
				runEntity.getTestOwner());
		final String url = manager.getClientInstance().buildRestRequest(
				QcEntitites.TestInstances);
		final Response response = manager.getClientInstance().httpPost(url,
				body);

		if (response.isOk()) {

			testInstanceId = RestXPathUtils.getAttributeValueSingle(
					response.toString(), QcTestInstanceEntity.ID);

			LOG.info(String.format(
					"Test Instance id : %s CREATED in Test Set : [%s]",
					testInstanceId, runEntity.getTestSetName()));
		} else {
			handleQCException(response, false);
		}

		return testInstanceId;
	}

	/**
	 * Post a new Test Run
	 * 
	 * Mapping Test Instance to RUN :
	 * 
	 * "cycle-id" as "cycle-id" & "testcycle-ver-stamp" as "testcycl-id" &
	 * "test-config-id" as "test-config-id"
	 * 
	 * @return Id of created Test Run
	 * @throws QCException
	 *             if failed to POST (with error details from QC )
	 */
	private boolean postTestRun(TestInstanceLocation instanceLocation) {

		boolean updateStatus = false;
		final String status = runEntity.getTestStatus();

		// it seems a run is not required
		if (status.equalsIgnoreCase(QcRunStatus.NoRun)) {
			LOG.warning(String
					.format("Skipping Status update for Test Instance : [%s] since input status=[%s]",
							runEntity.getTestInstance(), status));
			return true;
		}

		// POST a Run object ( with status = "NOT Completed" )
		final byte[] postBody = RequestBody.getTestRunPost(instanceLocation,
				runEntity, runEntityCustom);
		final String postUrl = manager.getClientInstance().buildRestRequest(
				QcEntitites.Runs);
		final Response postResponse = manager.getClientInstance().httpPost(
				postUrl, postBody);

		if (postResponse.isOk()) {

			final String runId = RestXPathUtils.getAttributeValueSingle(
					postResponse.toString(), QcRunEntity.ID);
			final String runName = RestXPathUtils.getAttributeValueSingle(
					postResponse.toString(), QcRunEntity.Name);

			// Change status of posted run (this will trigger changes to
			// test-instance)
			final byte[] body = RequestBody.getTestRunPut(status);
			final String url = manager.getClientInstance().buildRestRequest(
					QcEntitites.Runs);
			final Response putResponse = manager.getClientInstance().httpPut(
					url + "/" + runId, body);

			if (putResponse.isOk()) {
				
				updateStatus = true;
				LOG.info(String
						.format("Test Instance : [%s] UPDATED with RUN id=%s and name=\"%s\"",
								runEntity.getTestInstance(), runId, runName));
			} else {
				handleQCException(putResponse, false);
			}

			// for failed test runs attach file, if provided
			final String attachment = runEntity.getAttachment();
			if (status.equalsIgnoreCase(QcRunStatus.Failed)
					&& StringUtils.isDefined(attachment)) {

				String[] attachList = attachment.split(";");
				for (int i = 0; i < attachList.length; i++) {

					String thisAttachment = attachList[i].trim();
					if (StringUtils.isDefined(thisAttachment)) {
						postAttachment(runId, thisAttachment);
					}
				}
			}

		} else {
			handleQCException(postResponse, false);
		}

		return updateStatus;
	}

	/**
	 * Add attachment file to given run Id
	 * 
	 * @param runId
	 * @return true if success
	 */
	private void postAttachment(String runId, String filePath) {

		// file will be stored with this name on the QC Server
		String fileName = null;
		byte[] fileContent = null;

		if (FileUtils.isUrlFile(filePath)) {

			fileName = "attachment_" + urlId + ".url";
			urlId++;

			// get contents of an internet shortcut file
			fileContent = FileUtils.getUrlFileData(filePath);

		} else {

			// get filename from local file path
			fileName = new File(filePath).getName();
			if (StringUtils.isNotDefined(fileName)) {
				LOG.warning("Attachment FileName : " + filePath + " INVALID");
				return;
			}

			// get file content
			fileContent = FileUtils.getFileData(filePath);
			if (fileContent == null || fileContent.length == 0) {
				LOG.warning(String.format(
						"Failed to Attach Empty File : [%s] to Run ID : %s",
						fileName, runId));
				return;
			}
		}

		final String url = manager.getClientInstance().buildRestRequest(
				QcEntitites.Runs + "/" + runId + "/" + QcEntitites.Attachments);

		final Map<String, String> headers = RestXmlUtils
				.getAttachmentXmlHeaders(fileName);

		final Response response = manager.getClientInstance().httpPost(url,
				fileContent, headers);

		if (response.isOk()) {
			LOG.info(String.format("Attachment : [%s] Added to Run ID : %s",
					fileName, runId));
		} else {
			handleQCException(response, false, Level.WARNING);
		}
	}

	/**
	 * Find testSet by testSetName and testSetPath (not required if testSetName
	 * unique to QC )
	 * 
	 * @return Id of Found TestSet
	 * @throws QCException
	 *             if No relevant TestSet found in given TestSetPath
	 * @throws TestRunEntityException
	 *             if TestSet not unique and TestSetPath not provided
	 */
	private String getTestSetId() {

		final String testSetName = runEntity.getTestSetName();
		final String testSetPath = runEntity.getTestSetPath();

		// Get Parent testSetFolder ID
		final String testSetFolderId = TDRestFolderAPI
				.getParentTestSetFolderId(manager, testSetPath);

		if (StringUtils.isNotDefined(testSetFolderId)) {
			throw new TestRunEntityException(String.format(
					"TestSetPath : [%s] NOT FOUND on QC", testSetPath));
		}

		String testSetId = null;
		final String url = manager.getClientInstance().buildRestRequest(
				QcEntitites.TestSets);

		final String query = RestQueryBuilder.testSetQuery(testSetFolderId,
				testSetName);

		final Response testSetResponse = manager.getClientInstance().httpGet(
				url, query);

		if (testSetResponse.isOk()) {
			List<String> testSetIdList = RestXPathUtils
					.getAttributeValueForAll(testSetResponse.toString(),
							QcTestSetEntity.ID);

			switch (testSetIdList.size()) {
			case 0:

				// If Create Mode set : Create TestSet If not Found
				if (TDRestClientConfig.isCreateIfNotFound()) {
					testSetId = postTestSet(testSetFolderId);
				} else {
					throw new TestRunEntityException(String.format(
							"TestSet : [%s] NOT FOUND in TestSetPath : [%s]",
							testSetName, testSetPath));
				}
				break;

			case 1:
				testSetId = testSetIdList.get(0);
				LOG.info(String.format(
						"testSet : [%s] Found at testSetPath : [%s]",
						testSetName, testSetPath));
				break;
			}
		} else {
			handleQCException(testSetResponse, true);
		}

		return testSetId;
	}

	/**
	 * Find test by testName and testPath(not required if testName unique to QC)
	 * 
	 * @return Id of Found Test
	 * @throws QCException
	 *             if No relevant Test found in given TestPath
	 * @throws TestRunEntityException
	 *             if Test not unique and TestPath not provided
	 */
	private String getTestId(TestInstanceLocation instanceLocation) {

		final String testPath = runEntity.getTestPath();
		final String testName = runEntity.getTestName();

		// Get Parent testFolder ID
		String testFolderId = null;
		String errMsg = "";

		// if testPath provided but is incorrect/incomplete
		if (StringUtils.isDefined(testPath)) {

			LOG.info(String.format(
					"Trying to Locate test : [%s] using testPath : [%s]",
					testName, testPath));

			testFolderId = TDRestFolderAPI.getParentTestFolderId(manager,
					testPath);
			if (StringUtils.isNotDefined(testFolderId)) {
				errMsg = String.format("TestPath : [%s] NOT FOUND on QC",
						testPath);
			}
		}

		// try to get testName even if testPath unknown
		String testId = null;
		final String url = manager.getClientInstance().buildRestRequest(
				QcEntitites.Tests);

		final String query = RestQueryBuilder.testQuery(testFolderId, testName);

		final Response testResponse = manager.getClientInstance().httpGet(url,
				query);

		if (testResponse.isOk()) {
			List<String> testIdList = RestXPathUtils.getAttributeValueForAll(
					testResponse.toString(), QcTestEntity.ID);

			int testCount = testIdList.size();
			switch (testCount) {
			case 0:
				if (StringUtils.isDefined(testPath)) {
					if (StringUtils.isDefined(errMsg)) {
						throw new TestRunEntityException(errMsg);
					} else {
						throw new TestRunEntityException(String.format(
								"Test : [%s] NOT FOUND in TestPath : [%s]",
								testName, testPath));
					}
				} else {
					throw new TestRunEntityException(String.format(
							"Test : [%s] NOT FOUND", testName));
				}

			case 1:
				testId = testIdList.get(0);
				LOG.info(String.format("test : [%s] Found at testPath : [%s]",
						testName, testPath));

				// get test type
				instanceLocation.setTestType((String) RestXPathUtils
						.getAttributeValueForAll(testResponse.toString(),
								QcTestEntity.SubTypeID).get(0));
				break;

			default:

				if (StringUtils.isDefined(errMsg)) {
					LOG.warning(errMsg);
				}

				throw new TestRunEntityException(
						String.format(
								"A total of %s Tests with label : [%s] FOUND in test Plan. Provide testPath to Locate correct Test.",
								testIdList.size(), testName));
			}

		} else {
			handleQCException(testResponse, true);
		}

		return testId;
	}

	/**
	 * Return count of testInstances in testSetId
	 * 
	 * @param testSetId
	 * @return count
	 */
	private int getTestInstanceCount(String testSetId) {

		int count = 0;
		final String url = manager.getClientInstance().buildRestRequest(
				QcEntitites.TestInstances);
		final String query = RestQueryBuilder.getQuery(
				QcTestInstanceRelations.ContainsTestSet, testSetId);
		final Response testInstanceResponse = manager.getClientInstance()
				.httpGet(url, query);

		if (testInstanceResponse.isOk()) {
			count = RestXPathUtils.getResultEntitiesCount(testInstanceResponse
					.toString());
		}

		return count;
	}

	/**
	 * Tries to locate testInstance on the basis of testPath if multiple
	 * instances of tests with same name exist.
	 * 
	 * Example : [1]test, [1]test ; where first test => /Subject/abc/test &
	 * second test => /Subject/xyz/test
	 * 
	 * Tries to select correct instance if more than one instance of single test
	 * found
	 * 
	 * Example : [1]test, [2]test ; where test => /Subject/abc/test
	 * 
	 * @param testInstanceResponse
	 *            : Rest response containing list of testInstances
	 * @param instanceLocation
	 *            : object to hold extracted absolute location of testInstance
	 * @return owner name ff correct instance found else null
	 */

	private String findTestInstance(List<String> testInstanceIdList,
			Response testInstanceResponse, TestInstanceLocation instanceLocation) {

		final List<String> testIdList = RestXPathUtils
				.getRelatedAttributeValueForAll(
						testInstanceResponse.toString(), QcTestEntity.ID);

		boolean duplicateTests = false;
		for (int i = 1; i < testIdList.size(); i++) {
			if (Integer.parseInt(testIdList.get(i)) != Integer
					.parseInt(testIdList.get(i - 1))) {
				duplicateTests = true;
				break;
			}
		}

		// if there are multiple tests with same name.
		if (duplicateTests) {

			LOG.warning(String
					.format("Duplicate Tests instances with test name : [%s] FOUND in testSet : [%s]",
							runEntity.getTestName(), runEntity.getTestSetName()));

			// try to get correct test by testPath
			final String testId = getTestId(instanceLocation);

			// if correct testId is found get value of corresponding
			// testInstance
			if (StringUtils.isDefined(testId)) {

				instanceLocation.setTestId(testId);

				// count all instances with given testId
				List<Integer> matchedPos = new ArrayList<Integer>();

				for (int j = 0; j < testIdList.size(); j++) {
					if (Integer.parseInt(testIdList.get(j)) == Integer
							.parseInt(testId)) {

						matchedPos.add(j);
					}
				}

				// if multiple instances of same testId exist
				if (matchedPos.size() > 1) {
					throw new TestRunEntityException(
							String.format(
									"Multiple Test Instances for Test : [%s] FOUND. Make sure testInstance contains Id also. Example : [1]test",
									runEntity.getTestName()));
				} else {
					if (matchedPos.size() == 1) {

						int index = matchedPos.get(0);
						instanceLocation.setTestInstanceId(testInstanceIdList
								.get(index));

						String owner = RestXPathUtils.getAttributeValueForAll(
								testInstanceResponse.toString(),
								QcTestInstanceEntity.Owner).get(index);

						// if owner not set make actual-tester the owner for run
						if (StringUtils.isNotDefined(owner)) {
							owner = RestXPathUtils.getAttributeValueForAll(
									testInstanceResponse.toString(),
									QcTestInstanceEntity.Tester).get(index);
						}

						return owner;
					}
				}
			}
		}

		// if there are no multiple tests then
		// there must be multiple testInstance of same test
		else {
			throw new TestRunEntityException(
					String.format(
							"Multiple Test Instances for Test : [%s] FOUND. Make sure testInstance contains Id also. Example : [1]test",
							runEntity.getTestName()));
		}

		return null;
	}

	/**
	 * Find testInstance by testSetId and testName
	 * 
	 * @return Id of Found TestInstance
	 * @throws QCException
	 *             if No relevant TestInstance found in given TestSet
	 */
	private void getTestInstanceId(TestInstanceLocation instanceLocation) {

		final String testSetId = instanceLocation.getTestSetId();
		final String testInstance = runEntity.getTestInstance();

		final String url = manager.getClientInstance().buildRestRequest(
				QcEntitites.TestInstances);

		// Get testInstance Id from QC using testsetid, testname
		final String query = RestQueryBuilder.testInstanceQuery(testSetId,
				runEntity.getTestName(), runEntity.getTestInstanceId());

		final Response testInstanceResponse = manager.getClientInstance()
				.httpGet(url, query);

		if (testInstanceResponse.isOk()) {

			String owner = runEntity.getTestOwner();

			// if owner not provided, default to QC system user
			if (StringUtils.isNotDefined(owner)) {
				runEntity.setTestOwner(System
						.getProperty(ConnectionConstants.PROPERTY_TD_USER));
			}

			String testId = null;
			String testInstanceId = null;

			final List<String> testInstanceIdList = RestXPathUtils
					.getAttributeValueForAll(testInstanceResponse.toString(),
							QcTestInstanceEntity.ID);

			final String testSetName = runEntity.getTestSetName();

			switch (testInstanceIdList.size()) {
			case 0:

				// If Create Mode set : Create TestInstance If not Found
				// owner will be required in createMode
				if (TDRestClientConfig.isCreateIfNotFound()) {

					testId = getTestId(instanceLocation);
					instanceLocation.setTestId(testId);

					// get testOrder by counting existing testInstances in the
					// testSet
					final int testOrder = getTestInstanceCount(testSetId) + 1;
					instanceLocation.setTestOrderId(String.valueOf(testOrder));
					testInstanceId = postTestInstance(instanceLocation);

				} else {
					throw new TestRunEntityException(String.format(
							"TestInstance : [%s] NOT FOUND in TestSet : [%s]",
							testInstance, testSetName));
				}
				break;

			case 1:
				testInstanceId = testInstanceIdList.get(0);
				testId = RestXPathUtils.getRelatedAttributeValue(
						testInstanceResponse.toString(), QcTestEntity.ID);

				// get test instance type
				instanceLocation.setTestType((String) RestXPathUtils
						.getAttributeValueForAll(
								testInstanceResponse.toString(),
								QcTestInstanceEntity.SubTypeID).get(0));

				LOG.info(String.format(
						"testInstance : [%s] Found in testSet : [%s]",
						testInstance, testSetName));

				// extract owner from testInstance in case of searchMode
				owner = RestXPathUtils.getAttributeValue(
						testInstanceResponse.toString(),
						QcTestInstanceEntity.Owner);

				// if owner not set make actual-tester the owner for run
				if (StringUtils.isNotDefined(owner)) {
					owner = RestXPathUtils.getAttributeValue(
							testInstanceResponse.toString(),
							QcTestInstanceEntity.Tester);
				}
				break;

			default:
				owner = findTestInstance(testInstanceIdList,
						testInstanceResponse, instanceLocation);
				break;
			}

			// Set owner obtained from test instance to testEntity object
			if (StringUtils.isDefined(owner)) {
				runEntity.setTestOwner(owner);
			}

			// Set other params to TestInstanceLocation object
			if (StringUtils.isDefined(testId)
					&& StringUtils.isDefined(testInstanceId)) {

				instanceLocation.setTestId(testId);
				instanceLocation.setTestInstanceId(testInstanceId);
			}

		} else {
			handleQCException(testInstanceResponse, true);
		}
	}

	/**
	 * @param : TestRunEntity object ( test run info param )
	 * @return : true for successful Run creation in QC, false otherwise
	 */
	public boolean updateTestRunStatus(TestRunEntity runEntity) {

		LOG.finer(String
				.format("[ testSet = %s ,Instance = %s ,Status = %s ,testSetPath = %s ,testPath = %s ,Version = %s ,Message = %s ]",
						runEntity.getTestSetName(),
						runEntity.getTestInstance(), runEntity.getTestStatus(),
						runEntity.getTestSetPath(), runEntity.getTestPath(),
						runEntity.getTestVersion(), runEntity.getTestMessage()));

		this.runEntity = runEntity;
		TestInstanceLocation testLocator = new TestInstanceLocation();

		try {

			// Get testSet id from QC using testSetname & testSetFolderpath
			// (if testSetname not unique )
			testLocator.setTestSetId(this.getTestSetId());

			// Try to get unique testInstance Id from QC using testSet id from
			// previous stage by setting testInstance cycle-id = test-set-id
			getTestInstanceId(testLocator);

		} catch (TestRunEntityException e) {
			LOG.log(Level.WARNING, "", e);
			return false;
		} catch (QCException e) {
			LOG.log(Level.SEVERE, "", e);
			return false;
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "", e);
			return false;
		}

		// PUT TestInstance & POST a RUN
		return postTestRun(testLocator);
	}

}
