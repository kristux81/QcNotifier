/**
 * @author krvsingh
 */

package connectors.qc.notifier.mavenplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import connectors.qc.notifier.batchclient.TDBatchClient;
import connectors.qc.notifier.batchclient.commons.TDBatchClientConstants;
import connectors.qc.notifier.restclient.model.ConnectionConstants;
import connectors.qc.notifier.shared.TDNotifierConstants;

/*
 * Lifecycle default -> [validate, initialize, generate-sources, process-sources,
 *                       generate-resources, process-resources, compile,
 *                       process-classes, generate-test-sources, process-test-sources,
 *                       generate-test-resources, process-test-resources, test-compile,
 *                       process-test-classes, test, prepare-package, package,
 *                       pre-integration-test, integration-test, post-integration-test,
 *                       verify, install, deploy]
 * 
 *  we want to report results after completion of tests and prepare-package comes after
 *  test phase
 *  It can be overridden in POM using execution->phase->goal setting
 *  
 *  Example :
 *   <executions>
 *	 <execution>
 *	 <phase>verify</phase>
 *	 <goals>
 *	 <goal>batchnotify</goal>
 *	 </goals>
 *	 </execution>
 *	 </executions>
 *           
 */
@Mojo(name = "batchnotify", defaultPhase = LifecyclePhase.PACKAGE)
public class QcNotifyMojo extends AbstractMojo {

	/**
	 * Location of Absolute batch file path.
	 */
	@Parameter(property = PluginConstants.PLUGIN_PROPERTY_BATCH_FILE, defaultValue = PluginConstants.PLUGIN_PROPERTY_WORKSPACE
			+ TDNotifierConstants.DEFAULT_TD_BATCHFILE)
	private String batchFilePath;

	/**
	 * QC connection properties Absolute File path
	 */
	@Parameter(property = PluginConstants.PLUGIN_PROPERTY_QC_CONNECTION_FILE, defaultValue = PluginConstants.PLUGIN_PROPERTY_WORKSPACE
			+ ConnectionConstants.DEFAULT_TD_PROPERTY_FILE)
	private String qcPropertiesFile;

	/**
	 * QC connection properties
	 */
	@Parameter(property = ConnectionConstants.PROPERTY_TD_URL, defaultValue = ConnectionConstants.DEFAULT_TD_URL)
	private String tdUrl;

	@Parameter(property = ConnectionConstants.PROPERTY_TD_DOMAIN)
	private String tdDomain;

	@Parameter(property = ConnectionConstants.PROPERTY_TD_PROJECT)
	private String tdProject;

	@Parameter(property = ConnectionConstants.PROPERTY_TD_USER)
	private String tdUser;

	@Parameter(property = ConnectionConstants.PROPERTY_TD_PASSWORD)
	private String tdPassword;

	/**
	 * User can fail maven build if some or all notifications to QC fail It can
	 * be done by setting this flag to "true"
	 */
	@Parameter(property = PluginConstants.PLUGIN_PROPERTY_FAILBUILD, defaultValue = "false")
	private String failOnNotifFailure;

	/**
	 * User can call plugin without the batch file. This is a mock only mode. if
	 * set to true build will never be failed even without a missing batch file
	 */
	@Parameter(property = PluginConstants.PLUGIN_PROPERTY_MOCK, defaultValue = "false")
	private String mockWithoutFail;

	/**
	 * By default TDNotifier uses first instance in testset.
	 * 
	 * It can overridden by setting this flag as "false"
	 */
	@Parameter(property = TDNotifierConstants.PROPERTY_USE_FIRST_TESTINSTANCE, defaultValue = "true")
	private String defaultFirstInstance;

	/**
	 * By default TDNotifier has create MOde enabled. Test lab entities not
	 * found on QC will be automatically created.
	 * 
	 * In order to disable this set this flag as "false"
	 */
	@Parameter(property = TDNotifierConstants.PROPERTY_CREATE_IF_NOT_FOUND_MODE, defaultValue = "false")
	private String createMode;

	/**
	 * Entry Point
	 */
	public void execute() throws MojoFailureException, MojoExecutionException {

		// get QC connection properties from properties file if provided
		getQcConnectionPropertiesFromFile();

		// override file properties from the argument list of command line.
		overrideFromArguments();

		// set QC connection properties to system (will be used by RestClient)
		exportQcConnectionProperties();

		// set restClient behavior prior to batchClient instantiation
		configureBatchClientBehavior();

		int exitCode = TDBatchClientConstants.SUCCESS;
		try {
			exitCode = new TDBatchClient(batchFilePath).processInputFileToQC();
		} catch (Exception e) {

			// Will result into a BUILD FAILURE and further execution halted.
			throw new MojoExecutionException("Exception From BatchClient : ", e);

		} finally {
			sendExitMsgToMaven(exitCode);
		}
	}

	/**
	 * Configure BatchClient behavior
	 */
	private void configureBatchClientBehavior() {

		/**
		 * if -Dcreate_if_not_found=true then createMode will be set
		 */

		if ("true".equalsIgnoreCase(createMode)) {
			getLog().debug("BatchClient Create Mode Activated");
			System.setProperty(
					TDNotifierConstants.PROPERTY_CREATE_IF_NOT_FOUND_MODE,
					"true");
		}

		/**
		 * if -Duse_first_instance=true then if multiple testInstances for the
		 * same test found in a test-set, first will be used
		 */
		if (!"true".equalsIgnoreCase(defaultFirstInstance)) {
			getLog().debug(
					"BatchClient Default First Instance Mode Deactivated");
			System.setProperty(
					TDNotifierConstants.PROPERTY_USE_FIRST_TESTINSTANCE, null);
		}
	}

	private void getQcConnectionPropertiesFromFile() {

		if (qcPropertiesFile != null && qcPropertiesFile.length() > 0) {

			Properties properties = new Properties();
			try {
				FileInputStream inputStream = new FileInputStream(new File(
						qcPropertiesFile));

				getLog().info(
						String.format(
								"Reading QC Connection Properties File : [%s]",
								qcPropertiesFile));
				properties.load(inputStream);

				inputStream.close();
			} catch (IOException e) {

				getLog().error(
						String.format(
								"QC Connection Properties File : [%s], NOT FOUND.",
								qcPropertiesFile));
				return;
			}

			try {
				setQcConnectionProperties(properties);
			} catch (Exception e) {
				getLog().warn(e);
			}
		}
	}

	private void setQcConnectionProperties(Properties properties) {

		String urlStr = properties
				.getProperty(ConnectionConstants.PROPERTY_TD_URL);
		if (urlStr != null) {
			tdUrl = urlStr;
		}

		String domainStr = properties
				.getProperty(ConnectionConstants.PROPERTY_TD_DOMAIN);
		if (domainStr != null) {
			tdDomain = domainStr;
		}

		String projStr = properties
				.getProperty(ConnectionConstants.PROPERTY_TD_PROJECT);
		if (projStr != null) {
			tdProject = projStr;
		}

		String userStr = properties
				.getProperty(ConnectionConstants.PROPERTY_TD_USER);
		if (userStr != null) {
			tdUser = userStr;
		}

		String pswdStr = properties
				.getProperty(ConnectionConstants.PROPERTY_TD_PASSWORD);
		if (pswdStr != null) {
			tdPassword = pswdStr;
		}
	}

	/*
	 * Override QC connection params if provided in argument list
	 */
	private void overrideFromArguments() {

		setQcConnectionProperties(System.getProperties());
	}

	/*
	 * The System properties will be extracted by the loadGlobals() in Rest
	 * Client during login to QC
	 */
	private void exportQcConnectionProperties() {

		getLog().debug("QC URL = " + tdUrl);
		if (tdUrl != null) {
			System.setProperty(ConnectionConstants.PROPERTY_TD_URL, tdUrl);
		}

		getLog().debug("QC DOMAIN = " + tdDomain);
		if (tdDomain != null) {
			System.setProperty(ConnectionConstants.PROPERTY_TD_DOMAIN, tdDomain);
		}

		getLog().debug("QC PROJECT = " + tdProject);
		if (tdProject != null) {
			System.setProperty(ConnectionConstants.PROPERTY_TD_PROJECT,
					tdProject);
		}

		getLog().debug("QC USER = " + tdUser);
		if (tdUser != null) {
			System.setProperty(ConnectionConstants.PROPERTY_TD_USER, tdUser);
		}

		if (tdPassword != null) {
			System.setProperty(ConnectionConstants.PROPERTY_TD_PASSWORD,
					tdPassword);
		}
	}

	private void sendExitMsgToMaven(int exitCode) throws MojoFailureException {

		getLog().info("BatchClient Execution Ended with ErrCode : " + exitCode);

		switch (exitCode) {

		case TDBatchClientConstants.SUCCESS:
			break;

		/**
		 * Malformed batch file or no tests to run
		 */
		case TDBatchClientConstants.ERR_NO_SECTIONS_IN_FILE:

			decideBuildStatus(failOnNotifFailure, "It seems No tests were run");
			break;

		case TDBatchClientConstants.ERR_FEW_SECTIONS_SKIPPED:
		case TDBatchClientConstants.ERR_FEW_SECTIONS_FAILED:

			decideBuildStatus(
					failOnNotifFailure,
					"It seems all test runs were not posted to QC. See batch file : [*.unprocessed], for list of failed/skipped tests runs");
			break;

		case TDBatchClientConstants.ERR_QC_LOGIN_FAILED:

			decideBuildStatus(failOnNotifFailure,
					"QC Login Failed, may be due to incorrect or insufficeint credentials");
			break;

		case TDBatchClientConstants.ERR_PARSING_INPUT_FILE:

			decideBuildStatus(mockWithoutFail, String.format(
					"Failed to Parse batch File : [%s]", batchFilePath));
			break;

		/**
		 * Seems this plugin is outdated with respect to batchClient
		 */
		default:

			// May result into a BUILD FAILURE and further execution halted.
			throw new MojoFailureException(
					"Unregistered Errocode from BatchClient. Fix Needed !!");
		}
	}

	private void decideBuildStatus(String flag, String errMsg)
			throws MojoFailureException {

		if ("true".equalsIgnoreCase(flag)) {

			// Will result into a BUILD FAILURE and further execution
			// halted.
			throw new MojoFailureException(errMsg);

		} else {
			getLog().warn(errMsg);
		}
	}
}
