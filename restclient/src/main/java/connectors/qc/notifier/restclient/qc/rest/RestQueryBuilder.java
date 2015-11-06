/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.commons.StringUtils;
import connectors.qc.notifier.restclient.qc.model.QcFolderEntity;
import connectors.qc.notifier.restclient.qc.model.QcTestEntity;
import connectors.qc.notifier.restclient.qc.model.QcTestInstanceEntity;
import connectors.qc.notifier.restclient.qc.model.QcTestInstanceRelations;
import connectors.qc.notifier.restclient.qc.model.QcTestSetEntity;

public final class RestQueryBuilder {

	private RestQueryBuilder() {
	}

	private static String getEncoded(String queryString) {

		final Logger LOG = Logger.getLogger(RestQueryBuilder.class.getName());

		try {
			return URLEncoder.encode(queryString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			LOG.log(Level.SEVERE, "Failed to Build URL, Exception : ", e);
		}

		return "";
	}

	public static String getQuery(String key, String value) {

		StringBuilder queryStream = new StringBuilder("{");
		queryStream.append(key);
		queryStream.append("['");
		queryStream.append(value);
		queryStream.append("']}");

		return "query=" + getEncoded(queryStream.toString());
	}

	public static String getQuery(Map<String, String> kv) {

		if (kv.size() == 1) {
			Object[] keys = kv.keySet().toArray();
			Object[] values = kv.values().toArray();

			return RestQueryBuilder.getQuery(keys[0].toString(),
					values[0].toString());
		}

		StringBuilder queryStream = new StringBuilder();
		for (Map.Entry<String, String> entry : kv.entrySet()) {
			queryStream.append(entry.getKey().toString());
			queryStream.append("['");
			queryStream.append(entry.getValue().toString().trim());
			queryStream.append("'];");
		}

		return "query="
				+ getEncoded("{"
						+ queryStream.substring(0, queryStream.length() - 1)
						+ "}");
	}

	public static String testInstanceQuery(String testSetId, String testName,
			String testInstanceId) {

		String fieldRealization = "fields="
				+ QcTestInstanceRelations.RealizedByTest + "&";

		Map<String, String> params = new HashMap<String, String>();
		params.put(QcTestInstanceRelations.ContainsTestSet, testSetId);
		params.put(QcTestInstanceRelations.RealizedByTest, testName);
		
		if(StringUtils.isDefined(testInstanceId)){
			params.put(QcTestInstanceEntity.TestInstance, testInstanceId);
		}

		return fieldRealization + RestQueryBuilder.getQuery(params);
	}

	public static String testSetQuery(String testSetFolderId, String testSetName) {

		Map<String, String> params = new HashMap<String, String>();
		params.put(QcTestSetEntity.ParentID, testSetFolderId);
		params.put(QcTestSetEntity.Name, testSetName);

		return RestQueryBuilder.getQuery(params);
	}

	public static String testQuery(String testFolderId, String testName) {

		Map<String, String> params = new HashMap<String, String>();
		if (StringUtils.isDefined(testFolderId)) {
			params.put(QcTestEntity.ParentID, testFolderId);
		}
		params.put(QcTestEntity.Name, testName);

		return RestQueryBuilder.getQuery(params);
	}

	public static String folderQuery(String folderParentId, String folderName) {

		Map<String, String> params = new HashMap<String, String>();
		params.put(QcFolderEntity.ParentID, folderParentId);
		params.put(QcFolderEntity.Name, folderName);

		return RestQueryBuilder.getQuery(params);
	}

}
