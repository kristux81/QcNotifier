/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.model;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.TDRestClientConfig;
import connectors.qc.notifier.restclient.commons.StringUtils;

public final class TestRunEntityValidator {

	// no object instantiation for this class
	private TestRunEntityValidator() {
	}

	public static void validateTestStatus(TestRunEntity teObj) {

		final Logger LOG = Logger.getLogger(TestRunEntityValidator.class
				.getName());

		final String status = teObj.getTestStatus();
		if (StringUtils.isNotDefined(status)) {
			throw new TestRunEntityException("TestStatus Required");
		}

		String newStatus = "";
		try {
			Field[] fields = Class
					.forName(
							"connectors.qc.notifier.restclient.qc.model.QcRunStatus")
					.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (status.equalsIgnoreCase((String) fields[i].get(null))) {

					// convert to Camel case : QC is stupid enough to make us do this.
					newStatus = status.substring(0, 1).toUpperCase()
							+ status.substring(1).toLowerCase();
				}
			}
		} catch (IllegalArgumentException e) {
			LOG.log(Level.SEVERE, "", e);
		} catch (SecurityException e) {
			LOG.log(Level.SEVERE, "", e);
		} catch (ClassNotFoundException e) {
			LOG.log(Level.SEVERE, "", e);
		} catch (IllegalAccessException e) {
			LOG.log(Level.SEVERE, "", e);
		}

		if (newStatus.length() == 0) {
			throw new TestRunEntityException("TestStatus : " + status
					+ " Incorrect");
		}

		teObj.setTestStatus(newStatus);
	}

	public static void validateTestInstance(TestRunEntity teObj) {

		final String testInstance = teObj.getTestInstance().trim();
		if (StringUtils.isNotDefined(testInstance)) {
			throw new TestRunEntityException("TestInstance Required");
		}

		if (testInstance.charAt(0) == '[') {

			String[] str = testInstance.split("\\]", 2);

			switch (str.length) {
			case 1:
				// instance id not provided
				teObj.setTestName(testInstance);
				break;

			case 2:
				String[] str2 = str[0].split("\\[", 2);
				if (str2.length == 2) {

					try {
						Integer.parseInt(str2[1].trim());
					} catch (NumberFormatException e) {

						// seems that instance id not provided
						teObj.setTestName(testInstance);
						break;
					}

					teObj.setTestInstanceId(str2[1].trim());
					teObj.setTestName(str[1].trim());

				} else {
					// seems that instance id not provided
					teObj.setTestName(testInstance);
				}
				break;
			}

		} else {
			// seems that instance id not provided
			teObj.setTestName(testInstance);
		}

		// set default instanceID if not provided and default mode set
		if (StringUtils.isNotDefined(teObj.getTestInstanceId())
				&& TDRestClientConfig.isDefaultFirstInstance()) {
			
			teObj.setTestInstanceId("1");
		}
	}

	public static void validateTestSet(TestRunEntity teObj) {

		// Validate TestSetName
		if (StringUtils.isNotDefined(teObj.getTestSetName())) {
			throw new TestRunEntityException("TestSetName Required");
		}

		// Validate / preset TestBasePath
		if (StringUtils.isNotDefined(teObj.getTestBasePath())) {

			teObj.setTestBasePath(System
					.getProperty(ConnectionConstants.DEFAULT_TD_BASEPATH));
		}

		// Validate / preset TestSetPath
		if (StringUtils.isNotDefined(teObj.getTestSetPath())) {
			teObj.setTestSetPath(teObj.getTestBasePath());
		}

		if (StringUtils.isNotDefined(teObj.getTestSetPath())
				&& StringUtils.isNotDefined(teObj.getTestBasePath())) {

			throw new TestRunEntityException(
					"TestSetPath or TestBasePath Required");
		}
	}

	public static void preValidateObject(TestRunEntity teObj) {

		validateTestStatus(teObj);
		validateTestInstance(teObj);
		validateTestSet(teObj);
	}
}
