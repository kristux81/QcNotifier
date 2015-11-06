/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.rest;

import java.util.logging.Level;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.model.ConnectionEntity;
import connectors.qc.notifier.restclient.qc.model.ResourceAccessLevel;

public final class ConnectionManager {

	private ConnectionEntity conEntity = null;
	private RestClient restClient = null;

	// hold session params for reconnection
	private String connectionID = null;
	private String connectionUser = null;
	private String connectionPasswd = null;

	private static final Logger LOG = Logger.getLogger(ConnectionManager.class
			.getName());

	public ConnectionManager() {

		conEntity = new ConnectionEntity();

		// get a new rest client instance
		restClient = new RestClient(conEntity);

		// login to QC
		login();
	}

	public ConnectionManager(String qcConnFile) {

		conEntity = new ConnectionEntity(qcConnFile);

		// get a new rest client instance
		restClient = new RestClient(conEntity);

		// login to QC
		login();
	}

	public ConnectionManager(RestClient client, String user, String passwd) {

		// get a new rest client instance
		restClient = client;
		connectionUser = user;
		connectionPasswd = passwd;

		// login to QC
		login();
	}

	public void login() {
		LOG.fine("Attempting Login ...");
		/*
		LOG.finer("Domain = " + conEntity.getDomain());
		LOG.finer("Project = " + conEntity.getProject());
		LOG.finer("User = " + conEntity.getUsername());
		LOG.finer("Passwd = " + conEntity.getPassword());
        */
		String user = "";
		String passwd = "";

		if (conEntity != null) {
			user = conEntity.getUsername();
			passwd = conEntity.getPassword();
		} else {
			user = connectionUser;
			passwd = connectionPasswd;
		}

		try {
			new RestAuthenticator().login(restClient, user, passwd, LOG);
		} catch (RestException e) {
			LOG.log(Level.SEVERE, "QC LOGIN FAILED; EXCEPTION : ", e);
			throw e;
		}

		// add session cookies to further requests
		appendQCSessionCookies();
	}

	public void logout() {
		try {
			new RestAuthenticator().logout(restClient);
		} catch (RestException e) {
			LOG.log(Level.SEVERE, "QC LOGOUT FAILED; EXCEPTION : ", e);
		}
	}

	private void appendQCSessionCookies() {

		// issue a post request so that cookies relevant to the QC Session will
		// be added to the RestClient
		Response response = restClient.httpPost(
				restClient.build("rest/site-session"), null, null,
				ResourceAccessLevel.PUBLIC);
		if (!response.isOk()) {
			throw new RestException("Cannot appned QCSession cookies",
					response.getFailure());
		}
	}

	public Client getClientInstance() {
		return restClient;
	}

	public ConnectionEntity getConnectionEntity(){
	  return conEntity ;	
	}
	
	public String getConnectionID() {
		return connectionID;
	}

	public void setConnectionID(String connectionID) {
		this.connectionID = connectionID;
	}

}
