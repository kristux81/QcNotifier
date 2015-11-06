/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.commons.ClassUtils;
import connectors.qc.notifier.restclient.commons.StringUtils;

public class ConnectionEntity {

	private static final Logger LOG = Logger.getLogger(ConnectionEntity.class
			.getName());

	private String url = "";
	private String domain = "";
	private String project = "";
	private String username = "";
	private String password = "";

	public ConnectionEntity() {

		// load from properties file
		loadGlobals();

		// override by system ENV
		loadLocals();
	}

	public ConnectionEntity(String propFilePath) {

		// load from properties file
		getPropertiesFromFile(propFilePath);

		// override by system ENV
		loadLocals();
	}

	private void loadGlobals() {

		String file = null;
		try {
			file = ClassUtils.getJarDir(this.getClass()) + "/"
					+ ConnectionConstants.DEFAULT_TD_PROPERTY_FILE;
		} catch (URISyntaxException e1) {
			LOG.log(Level.WARNING,
					"Error Loading Global Connection Property File : ", e1);
			file = null;
		}

		getPropertiesFromFile(file);
	}

	private void loadLocals() {

		LOG.fine("Attempting to read System Properties ..");

		// set URL
		String urlstr = "";
		try {
			urlstr = System.getProperty(ConnectionConstants.PROPERTY_TD_URL);
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Property : "
					+ ConnectionConstants.PROPERTY_TD_URL
					+ " Undefined or Null. System Defaults will be used.");
		}

		if (StringUtils.isDefined(urlstr)) {
			url = urlstr;
		} else {
			if (StringUtils.isNotDefined(url)) {
				url = ConnectionConstants.DEFAULT_TD_URL;
			}
		}

		// set domain
		String domainstr = System
				.getProperty(ConnectionConstants.PROPERTY_TD_DOMAIN);
		if (StringUtils.isDefined(domainstr)) {
			domain = domainstr;
		}

		// set Project
		String projectstr = System
				.getProperty(ConnectionConstants.PROPERTY_TD_PROJECT);
		if (StringUtils.isDefined(projectstr)) {
			project = projectstr;
		}

		// set Username
		String user = System.getProperty(ConnectionConstants.PROPERTY_TD_USER);
		if (StringUtils.isDefined(user)) {
			username = user;
		}

		// set Password
		String pwd = System
				.getProperty(ConnectionConstants.PROPERTY_TD_PASSWORD);
		if (StringUtils.isDefined(pwd)) {
			password = pwd;
		}
	}

	private void getPropertiesFromFile(String file) {

		if (file != null) {
			Properties properties = new Properties();
			try {
				FileInputStream inputStream = new FileInputStream(
						new File(file));
				LOG.fine(String.format(
						"Reading Global connection Properties File : %s", file));
				properties.load(inputStream);

				inputStream.close();
			} catch (IOException e) {
				LOG.warning(String
						.format("Connection Properties File \"%s\" NOT FOUND. Will read connection parameters from System",
								file));
				return;
			}

			try {
				setProperties(properties);
			} catch (Exception e) {
				LOG.log(Level.WARNING, "", e);
			}
		}
	}

	private void setProperties(Properties properties) {

		url = properties.getProperty(ConnectionConstants.PROPERTY_TD_URL);
		domain = properties.getProperty(ConnectionConstants.PROPERTY_TD_DOMAIN);
		project = properties
				.getProperty(ConnectionConstants.PROPERTY_TD_PROJECT);
		username = properties.getProperty(ConnectionConstants.PROPERTY_TD_USER);
		password = properties
				.getProperty(ConnectionConstants.PROPERTY_TD_PASSWORD);

		// export System user to environment if read from file
		System.setProperty(ConnectionConstants.PROPERTY_TD_USER, username);
	}

	public String getUrl() {
		return url;
	}

	public String getDomain() {
		return domain;
	}

	public String getProject() {
		return project;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
