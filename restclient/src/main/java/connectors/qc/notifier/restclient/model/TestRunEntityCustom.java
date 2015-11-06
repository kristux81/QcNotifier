/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.commons.StringUtils;

public class TestRunEntityCustom {

	private Map<String, String> customFields = null;
	private static final Logger LOG = Logger
			.getLogger(TestRunEntityCustom.class.getName());

	public TestRunEntityCustom() {
		customFields = new HashMap<String, String>();
	}

	public void setField(String fieldLbl, String fieldVal) {

		if (StringUtils.isDefined(fieldLbl) && StringUtils.isDefined(fieldVal)) {
			customFields.put(fieldLbl, fieldVal);
		} else {
			LOG.warning(String.format(
					"Skipping INVALID Custom field : '%s', or value : '%s'",
					fieldLbl, fieldVal));
		}
	}

	public void setAllFields(Map<String, String> customFields) {
		customFields.putAll(customFields);
	}

	public Map<String, String> getAllFields() {
		return customFields;
	}

}
