/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.commons.StringUtils;
import connectors.qc.notifier.restclient.model.TestRunEntityException;
import connectors.qc.notifier.restclient.qc.model.QcEntitites;
import connectors.qc.notifier.restclient.qc.model.QcExceptionEntity;
import connectors.qc.notifier.restclient.qc.model.QcFolderEntity;
import connectors.qc.notifier.restclient.qc.rest.ConnectionManager;
import connectors.qc.notifier.restclient.qc.rest.QCException;
import connectors.qc.notifier.restclient.qc.rest.RequestBody;
import connectors.qc.notifier.restclient.qc.rest.Response;
import connectors.qc.notifier.restclient.qc.rest.RestQueryBuilder;
import connectors.qc.notifier.restclient.qc.rest.RestXPathUtils;

public final class TDRestFolderAPI {

	private TDRestFolderAPI() {
	}

	private static final Logger LOG = Logger.getLogger(TDRestFolderAPI.class
			.getName());

	private static void logQCException(Response response) {

		LOG.severe(String.format("HTTP %s : [%s]", response.getStatusCode(),
				RestXPathUtils.getQCExceptionElementText(response.toString(),
						QcExceptionEntity.Title)));
	}

	/**
	 * 
	 * @param manager
	 *            : REST Client instance
	 * @param TestEntityFolderName
	 *            : TestSet Folder or Test Folder name
	 * @param TestEntityFolderParentID
	 *            : Parent id of TestSet Folder or Test Folder
	 * @param testEntityType
	 *            : Constant String "test-folders" or "test-set-folders"
	 * @return ID of TestSet Folder or Test Folder found in QC tree
	 * @throws QCException
	 *             : Exception if any from QC
	 */
	private static String getTestEntityFolderIDByName(
			ConnectionManager manager, String testEntityFolderName,
			String testEntityFolderParentID, String testEntityType) {

		String testEntityFolderID = "";
		final String url = manager.getClientInstance().buildRestRequest(
				testEntityType);
		final String query = RestQueryBuilder.folderQuery(
				testEntityFolderParentID, testEntityFolderName);

		final Response testEntityFolderResponse = manager.getClientInstance()
				.httpGet(url, query);

		if (testEntityFolderResponse.isOk()) {
			testEntityFolderID = RestXPathUtils.getAttributeValue(
					testEntityFolderResponse.toString(), QcFolderEntity.ID);
		} else {
			TDRestFolderAPI.logQCException(testEntityFolderResponse);
		}

		// Create test-set folders if test-set folder not found and create mode
		// set
		if (testEntityType.equals(QcEntitites.TestSetFolders)
				&& StringUtils.isNotDefined(testEntityFolderID)
				&& TDRestClientConfig.isCreateIfNotFound()) {

			testEntityFolderID = postTestSetFolder(manager,
					testEntityFolderName, testEntityFolderParentID);
		}

		return testEntityFolderID;
	}

	/**
	 * 
	 @param manager
	 *            : REST Client instance
	 * @param testSetFolderName
	 *            : TestSet Folder name to create
	 * @param testSetFolderParentId
	 *            : TestSet Folder Parent Id
	 * @return ID of newly created TestSet Folder
	 */
	private static String postTestSetFolder(ConnectionManager manager,
			String testSetFolderName, String testSetFolderParentId) {

		String testSetFolderID = "";
		final String url = manager.getClientInstance().buildRestRequest(
				QcEntitites.TestSetFolders);
		final byte[] body = RequestBody.getTestSetFolderPost(testSetFolderName,
				testSetFolderParentId);
		final Response testSetFolderResponse = manager.getClientInstance()
				.httpPost(url, body);

		if (testSetFolderResponse.isOk()) {

			testSetFolderID = RestXPathUtils.getAttributeValueSingle(
					testSetFolderResponse.toString(), QcFolderEntity.ID);
			LOG.info(String.format(
					"TestSet Folder : [%s] CREATED with ID : %s",
					testSetFolderName, testSetFolderID));

		} else {
			TDRestFolderAPI.logQCException(testSetFolderResponse);
		}

		return testSetFolderID;
	}

	/**
	 * 
	 * @param testEntityPath
	 *            : Full path of TestSet or Test
	 * @param testEntityType
	 *            : Constant String "test-folders" or "test-set-folders"
	 * @return List of folders in path
	 * @throws TestRunEntityException
	 *             : If Path empty or Invalid
	 */
	public static List<String> getTestEntityFoldersFromPath(
			String testEntityPath, String testEntityType) {

		String root = "";
		String enityName = "";

		if (testEntityType.equals(QcEntitites.TestFolders)) {
			root = QcFolderEntity.TestFolderRoot;
			enityName = "TestPath";
		} else if (testEntityType.equals(QcEntitites.TestSetFolders)) {
			root = QcFolderEntity.TestSetFolderRoot;
			enityName = "TestSetPath";
		}

		if (StringUtils.isNotDefined(testEntityPath)) {
			throw new TestRunEntityException(enityName + " REQUIRED");
		}

		String[] testEntityFolders = testEntityPath.split("/");
		int first = 0, last = testEntityFolders.length - 1;

		List<String> testEntityPathFolders = new ArrayList<String>();
		if (last > first) {
			if (testEntityFolders[first].length() == 0) {
				first = 1;
			}

			if (testEntityFolders[last].length() == 0) {
				last = last - 1;
			}

			for (int i = first; i <= last; i++) {
				testEntityPathFolders.add(testEntityFolders[i]);
			}
		} else if (last == first
				&& testEntityFolders[first].equalsIgnoreCase(root)) {
			testEntityPathFolders.add(root);
		} else {
			throw new TestRunEntityException(String.format(
					"%s : [%s] INVALID !!", enityName, testEntityPath));
		}

		return testEntityPathFolders;
	}

	/**
	 * 
	 * @param manager
	 *            : REST Client instance
	 * @param testSetPath
	 *            : Test Set Path
	 * @return Leaf TestSetFolder Id in testSetPath
	 * @throws TestRunEntityException
	 *             : If Path empty or Invalid
	 * @throws QCException
	 *             : Exception if any from QC
	 */
	public static String getParentTestSetFolderId(ConnectionManager manager,
			String testSetPath) {

		// id of "Root" folder = 0
		String testSetFolderId = QcFolderEntity.TestSetFolderRootId;

		final List<String> testEntityFolders = getTestEntityFoldersFromPath(
				testSetPath, QcEntitites.TestSetFolders);

		// id of Root acts as parent-id for next set of folders
		if (testEntityFolders.get(0).equalsIgnoreCase(
				QcFolderEntity.TestSetFolderRoot)) {
			for (int i = 1; i < testEntityFolders.size(); i++) {

				testSetFolderId = getTestEntityFolderIDByName(manager,
						testEntityFolders.get(i), testSetFolderId,
						QcEntitites.TestSetFolders);

				// i.e test-set folder does not exist on QC test lab
				if (StringUtils.isNotDefined(testSetFolderId)) {
					return null;
				}
			}
		}

		return testSetFolderId;
	}

	/**
	 * 
	 * @param manager
	 *            : REST Client instance
	 * @param testPath
	 *            : Test Path
	 * @return Leaf TestFolder Id in testPath
	 * @throws TestRunEntityException
	 *             : If Path empty or Invalid
	 * @throws QCException
	 *             : Exception if any from QC
	 */
	public static String getParentTestFolderId(ConnectionManager manager,
			String testPath) {

		// Parent Id of "Subject" folder = 0
		String testFolderId = getTestEntityFolderIDByName(manager,
				QcFolderEntity.TestFolderRoot,
				QcFolderEntity.TestFolderRootParentId, QcEntitites.TestFolders);

		final List<String> testEntityFolders = getTestEntityFoldersFromPath(
				testPath, QcEntitites.TestFolders);

		// id of Root acts as parent-id for next set of folders
		if (testEntityFolders.get(0).equalsIgnoreCase(
				QcFolderEntity.TestFolderRoot)) {
			for (int i = 1; i < testEntityFolders.size(); i++) {

				testFolderId = getTestEntityFolderIDByName(manager,
						testEntityFolders.get(i), testFolderId,
						QcEntitites.TestFolders);

				// i.e test folder does not exist on QC test plan
				if (StringUtils.isNotDefined(testFolderId)) {
					return null;
				}
			}
		}

		return testFolderId;
	}
}
