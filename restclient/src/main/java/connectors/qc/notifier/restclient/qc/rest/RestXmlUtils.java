/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.rest;

import java.util.HashMap;
import java.util.Map;

public final class RestXmlUtils {

	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DEL = "DELETE";

	public static final String COOKIE = "Cookie";
	public static final String APP_XML = "application/xml";
	public static final String APP_OCTET = "application/octet-stream";

	// no object instantiation for this class
	private RestXmlUtils() {
	}

	public static String fieldXml(String field, String value) {

		return String.format("<Field Name=\"%s\"><Value>%s</Value></Field>",
				field, value);
	}

	public static Map<String, String> getGenericXmlHeaders() {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpHeaders.CONTENT_TYPE, APP_XML);
		headers.put(HttpHeaders.ACCEPT, APP_XML);

		return headers;
	}

	public static Map<String, String> getAttachmentXmlHeaders(String fileName) {

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(HttpHeaders.SERVER_FILENAME, fileName);
		headers.put(HttpHeaders.CONTENT_TYPE, APP_OCTET);

		return headers;
	}

}
