package connectors.qc.notifier.restclient.qc.model;

import connectors.qc.notifier.restclient.qc.rest.HttpHeaders;

public enum ResourceAccessLevel {
	PUBLIC(null), PROTECTED(HttpHeaders.PtaL), PRIVATE(HttpHeaders.PvaL);

	private String headerName;

	private ResourceAccessLevel(String header) {
		headerName = header;
	}

	public String getUserHeaderName() {
		return headerName;
	}
}
