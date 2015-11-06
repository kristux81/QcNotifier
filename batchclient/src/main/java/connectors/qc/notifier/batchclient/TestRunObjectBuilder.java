package connectors.qc.notifier.batchclient;

import java.util.Map;

import connectors.qc.notifier.restclient.model.TestRunEntity;
import connectors.qc.notifier.restclient.model.TestRunEntityCustom;
import connectors.qc.notifier.shared.TDNotifierConstants;
import connectors.qc.notifier.shared.TestRunEntityMapper;

public final class TestRunObjectBuilder {

	private TestRunObjectBuilder() {
	}

	private static TestRunEntityMapper testEntityMapper = null;

	public static void init() {

		// load valid input tokens for parsing input file
		testEntityMapper = TestRunEntityMapper.getInstance(System
				.getProperty(TDNotifierConstants.MAPPER_PROPERTY_FILE));

	}

	public static TestRunEntityCustom buildTestRunObject(
			Map<String, String> section, TestRunEntity te) {

		TestRunEntityCustom teCustom = null;
		boolean foundValidField = false;
		boolean firstCustomField = false;

		for (Map.Entry<String, String> entry : section.entrySet()) {
			if (entry.getKey().toString()
					.equalsIgnoreCase(testEntityMapper.testBasePath)) {
				te.setTestBasePath(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString()
					.equalsIgnoreCase(testEntityMapper.testInstance)) {
				te.setTestInstance(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString()
					.equalsIgnoreCase(testEntityMapper.testMessage)) {
				te.setTestMessage(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString()
					.equalsIgnoreCase(testEntityMapper.testSetPath)) {
				te.setTestSetPath(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString()
					.equalsIgnoreCase(testEntityMapper.testPath)) {
				te.setTestPath(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString()
					.equalsIgnoreCase(testEntityMapper.testSetName)) {
				te.setTestSetName(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString()
					.equalsIgnoreCase(testEntityMapper.testStatus)) {
				te.setTestStatus(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString()
					.equalsIgnoreCase(testEntityMapper.testVersion)) {
				te.setTestVersion(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString().equalsIgnoreCase("owner")) {
				te.setTestOwner(entry.getValue().toString().trim());
				foundValidField = true;
			}

			if (entry.getKey().toString().equalsIgnoreCase("attachment")) {
				te.setAttachment(entry.getValue().toString().trim());
				foundValidField = true;
			}

			// if none of the above then it seems to be a custom/user-defined
			// field (since no validation logic against custom/user-defined
			// fields available)
			if (!foundValidField) {
				if (!firstCustomField) {
					teCustom = new TestRunEntityCustom();
					firstCustomField = true;
				}
				teCustom.setField(entry.getKey().toString().trim(), entry
						.getValue().toString().trim());
			}

			// reset for next loop
			foundValidField = false;
		}

		return teCustom;
	}

}
