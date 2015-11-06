package connectors.qc.notifier.restclient.qc.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.commons.StringUtils;
import connectors.qc.notifier.restclient.model.ConnectionEntity;
import connectors.qc.notifier.restclient.qc.model.ResourceAccessLevel;

public class RestClient implements Client {

	private String serverUrl;
	protected Map<String, String> headers = RestXmlUtils.getGenericXmlHeaders();
	protected Map<String, String> cookies = new HashMap<String, String>();
	private String restPrefix;
	private String username;

	public RestClient(ConnectionEntity connEntity) {

		this(connEntity.getUrl(), connEntity.getDomain(), connEntity
				.getProject(), connEntity.getUsername());
	}

	public RestClient(String url, String domain, String project, String user) {

		if (!url.endsWith("/")) {
			url += "/";
		}
		serverUrl = url;
		username = user;
		restPrefix = getPrefixUrl("rest", String.format("domains/%s", domain),
				String.format("projects/%s", project));
	}

	public String build(String suffix) {

		return String.format("%1$s%2$s", serverUrl, suffix);
	}

	public String buildRestRequest(String suffix) {

		return String.format("%1$s/%2$s", restPrefix, suffix);
	}

	public Response httpGet(String url, String queryString)
			throws RestException {

		return doHttp(RestXmlUtils.GET, url, queryString, null, headers,
				ResourceAccessLevel.PUBLIC);
	}

	public Response httpPost(String url, byte[] requestBody)
			throws RestException {

		return doHttp(RestXmlUtils.POST, url, null, requestBody, headers,
				ResourceAccessLevel.PUBLIC);
	}

	public Response httpPut(String url, byte[] requestBody)
			throws RestException {

		return doHttp(RestXmlUtils.PUT, url, null, requestBody, headers,
				ResourceAccessLevel.PUBLIC);
	}

	public Response httpGet(String url, String queryString,
			Map<String, String> headers) throws RestException {

		return doHttp(RestXmlUtils.GET, url, queryString, null, headers,
				ResourceAccessLevel.PUBLIC);
	}

	public Response httpPost(String url, byte[] requestBody,
			Map<String, String> headers) throws RestException {

		return doHttp(RestXmlUtils.POST, url, null, requestBody, headers,
				ResourceAccessLevel.PUBLIC);
	}

	public Response httpPut(String url, byte[] requestBody,
			Map<String, String> headers) throws RestException {

		return doHttp(RestXmlUtils.PUT, url, null, requestBody, headers,
				ResourceAccessLevel.PUBLIC);
	}

	public Response httpGet(String url, String queryString,
			Map<String, String> headers, ResourceAccessLevel resourceAccessLevel)
			throws RestException {

		return doHttp(RestXmlUtils.GET, url, queryString, null, headers,
				resourceAccessLevel);
	}

	public Response httpPost(String url, byte[] requestBody,
			Map<String, String> headers, ResourceAccessLevel resourceAccessLevel)
			throws RestException {

		return doHttp(RestXmlUtils.POST, url, null, requestBody, headers,
				resourceAccessLevel);
	}

	public Response httpPut(String url, byte[] requestBody,
			Map<String, String> headers, ResourceAccessLevel resourceAccessLevel)
			throws RestException {

		return doHttp(RestXmlUtils.PUT, url, null, requestBody, headers,
				resourceAccessLevel);
	}

	public String getServerUrl() {

		return serverUrl;
	}

	private String getPrefixUrl(String protocol, String domain, String project) {

		return String
				.format("%s%s/%s/%s", serverUrl, protocol, domain, project);
	}

	/**
	 * @param type
	 *            http operation: get post put delete
	 * @param url
	 *            to work on
	 * @param queryString
	 * @param requestBody
	 *            to write, if a writable operation
	 * @param headers
	 *            to use in the request
	 * @param cookies
	 *            to use in the request and update from the response
	 * @return http response
	 */
	private Response doHttp(String type, String url, String queryString,
			byte[] requestBody, Map<String, String> headers,
			ResourceAccessLevel resourceAccessLevel) {

		Response ret = null;
		if (StringUtils.isDefined(queryString)) {
			url += "?" + queryString;
			Logger.getLogger(RestClient.class.getName()).finest(url);
		}
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			connection.setRequestMethod(type);

			Map<String, String> decoratedHeaders = new HashMap<String, String>();
			if (headers != null) {
				decoratedHeaders.putAll(headers);
			}

			HttpRequestDecorator.decorateHeaderWithUserInfo(decoratedHeaders,
					getUsername(), resourceAccessLevel);

			prepareHttpRequest(connection, decoratedHeaders, requestBody);
			connection.connect();
			ret = retrieveHtmlResponse(connection);
			updateCookies(ret);
		} catch (Exception e) {
			throw new RestException(e);
		}

		return ret;
	}

	/**
	 * @param connnection
	 *            connection to set the headers and bytes in
	 * @param headers
	 *            to use in the request, such as content-type
	 * @param bytes
	 *            the actual data to post in the connection.
	 */
	private void prepareHttpRequest(HttpURLConnection connnection,
			Map<String, String> headers, byte[] bytes) {

		// set all cookies for request
		connnection.setRequestProperty(RestXmlUtils.COOKIE, getCookies());
		setConnectionHeaders(connnection, headers);
		setConnectionData(connnection, bytes);
	}

	private void setConnectionData(HttpURLConnection connnection, byte[] bytes) {

		if (bytes != null && bytes.length > 0) {
			connnection.setDoOutput(true);
			try {
				OutputStream out = connnection.getOutputStream();
				out.write(bytes);
				out.flush();
				out.close();
			} catch (Exception e) {
				throw new RestException(e);
			}
		}
	}

	private void setConnectionHeaders(HttpURLConnection connnection,
			Map<String, String> headers) {

		if (headers != null) {
			Iterator<Entry<String, String>> headersIterator = headers
					.entrySet().iterator();
			while (headersIterator.hasNext()) {
				Entry<String, String> header = headersIterator.next();
				connnection.setRequestProperty(header.getKey(),
						header.getValue());
			}
		}
	}

	/**
	 * @param con
	 *            that is already connected to its url with an http request, and
	 *            that should contain a response for us to retrieve
	 * @return a response from the server to the previously submitted http
	 *         request
	 * @throws IOException
	 */
	private Response retrieveHtmlResponse(HttpURLConnection connection) {

		Response ret = new Response();

		try {
			ret.setStatusCode(connection.getResponseCode());
			ret.setHeaders(connection.getHeaderFields());
		} catch (Exception e) {
			throw new RestException(e);
		}

		InputStream inputStream;
		// select the source of the input bytes, first try 'regular' input
		try {
			inputStream = connection.getInputStream();
		}
		// if the connection to the server somehow failed, for example 404 or
		// 500,con.getInputStream() will throw an exception, which
		// we'll keep. we'll also store the body of the exception page,
		// in the response data. */
		catch (Exception e) {
			inputStream = connection.getErrorStream();
			ret.setFailure(e);
		}

		// this takes data from the previously set stream (error or input)
		// and stores it in a byte[] inside the response
		ByteArrayOutputStream container = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int read;
		try {
			while ((read = inputStream.read(buf, 0, 1024)) > 0) {
				container.write(buf, 0, read);
			}
			ret.setData(container.toByteArray());
		} catch (Exception ex) {
			throw new RestException(ex);
		}

		return ret;
	}

	private void updateCookies(Response response) {

		Iterable<String> newCookies = response.getHeaders().get(
				HttpHeaders.SET_COOKIE);
		if (newCookies != null) {
			for (String cookie : newCookies) {
				int equalIndex = cookie.indexOf('=');
				int semicolonIndex = cookie.indexOf(';');
				String cookieKey = cookie.substring(0, equalIndex);
				String cookieValue = cookie.substring(equalIndex + 1,
						semicolonIndex);
				cookies.put(cookieKey, cookieValue);
			}
		}
	}

	private String getCookies() {

		StringBuilder ret = new StringBuilder();
		if (!cookies.isEmpty()) {
			for (Entry<String, String> entry : cookies.entrySet()) {
				ret.append(entry.getKey()).append("=").append(entry.getValue())
						.append(";");
			}
		}

		return ret.toString();
	}

	public String getUsername() {
		return username;
	}
}
