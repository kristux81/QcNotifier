/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.rest;

import java.util.Map;

import connectors.qc.notifier.restclient.qc.model.ResourceAccessLevel;

public interface Client {

	Response httpGet(String url, String queryString);

	Response httpPost(String url, byte[] requestBody);

	Response httpPut(String url, byte[] requestBody);

	Response httpGet(String url, String queryString, Map<String, String> headers);

	Response httpPost(String url, byte[] requestBody,
			Map<String, String> headers);

	Response httpPut(String url, byte[] requestBody, Map<String, String> headers);

	Response httpGet(String url, String queryString,
			Map<String, String> headers, ResourceAccessLevel resourceAccessLevel);

	Response httpPost(String url, byte[] requestBody,
			Map<String, String> headers, ResourceAccessLevel resourceAccessLevel);

	Response httpPut(String url, byte[] requestBody,
			Map<String, String> headers, ResourceAccessLevel resourceAccessLevel);

	String build(String suffix);

	String buildRestRequest(String suffix);

	String getServerUrl();

	String getUsername();
}
