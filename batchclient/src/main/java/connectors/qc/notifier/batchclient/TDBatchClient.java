/**
 * @author krvsingh
 */
package connectors.qc.notifier.batchclient;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import connectors.qc.notifier.batchclient.commons.TDBatchClientConstants;
import connectors.qc.notifier.batchclient.commons.TDBatchClientUtils;
import connectors.qc.notifier.batchclient.parsers.FileParser;
import connectors.qc.notifier.batchclient.parsers.Parser;
import connectors.qc.notifier.restclient.ITDClient;
import connectors.qc.notifier.restclient.TDRestClient;
import connectors.qc.notifier.restclient.TDRestClientConfig;
import connectors.qc.notifier.restclient.model.TestRunEntity;
import connectors.qc.notifier.restclient.model.TestRunEntityCustom;
import connectors.qc.notifier.restclient.model.TestRunEntityException;
import connectors.qc.notifier.restclient.model.TestRunEntityValidator;
import connectors.qc.notifier.restclient.qc.rest.RestException;
import connectors.qc.notifier.shared.TDBatchWriter;
import connectors.qc.notifier.shared.TDNotifierConstants;

public class TDBatchClient {

	private static final Logger LOG = Logger.getLogger(TDBatchClient.class
			.getName());

	private String outFile = "";
	private File inputFile = null;
	private File processedFile = null;

	public TDBatchClient(String filename) {

		// input file validation
		inputFile = new File(filename);
		if (!inputFile.exists()) {
			LOG.severe("FILE NOT FOUND : " + filename);
			return;
		}

		outFile = TDBatchClientUtils.getOutputFileName(inputFile);
		processedFile = new File(outFile);

		// initialize test run objects builder
		TestRunObjectBuilder.init();

		// if -Dcreate_if_not_found=true then createMode will be set
		// if the property set to false or undefined default mode (error
		// reporting) will be used
		TDRestClientConfig.setCreateIfNotFound("true".equalsIgnoreCase(System
				.getProperty(
						TDNotifierConstants.PROPERTY_CREATE_IF_NOT_FOUND_MODE,
						"false")));

		// if -Duse_first_instance=true then if multiple testInstances
		// for the same test found in a test-set, first will be used
		// if the property set to false or undefined default mode (error
		// reporting) will be used
		TDRestClientConfig.setDefaultFirstInstance("true"
				.equalsIgnoreCase(System.getProperty(
						TDNotifierConstants.PROPERTY_USE_FIRST_TESTINSTANCE,
						"false")));
	}

	public int processInputFileToQC() {

		// Parse input File into Objects
		FileParser parser = null;
		try {
			parser = Parser.getInstance(inputFile.getAbsolutePath());
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "", ex);
			return TDBatchClientConstants.ERR_PARSING_INPUT_FILE;
		}

		if (parser.getSectionCount() == 0) {
			LOG.severe("NO VALID SECTIONS TO PROCESS !!");
			inputFile.renameTo(processedFile);
			return TDBatchClientConstants.ERR_NO_SECTIONS_IN_FILE;
		}

		// Get TDNotifier Client instance
		ITDClient clientProvider = null;

		try {
			clientProvider = new TDRestClient();
		} catch (RestException e) {
			return TDBatchClientConstants.ERR_QC_LOGIN_FAILED;
		}

		StringBuilder fsec = new StringBuilder();
		int passCount = 0, failCount = 0, skipCount = 0;

		// Iterate over parsed objects and push them to QC in a loop
		for (int i = 0; i < parser.getSectionCount(); i++) {

			int sectionId = i + 1;
			LOG.info("------------------- SECTION : " + sectionId
					+ " ---------------");

			Map<String, String> section = parser.getSection(i);
			TestRunEntity runEntity = new TestRunEntity();
			TestRunEntityCustom customEntity = TestRunObjectBuilder
					.buildTestRunObject(section, runEntity);

			try {
				TestRunEntityValidator.preValidateObject(runEntity);
			} catch (TestRunEntityException e) {
				LOG.log(Level.SEVERE, "", e);
				LOG.info(" >>>>>>>>>> SECTION " + sectionId
						+ " : [UPDATING SKIPPED]");
				fsec.append(sectionId);
				fsec.append(" ");
				skipCount++;
				TDBatchClientUtils.writeUnprocessedSection(outFile, section);
				continue;
			}

			clientProvider.setRunEntityCustom(customEntity);

			// post it to QC using the above runEntity object
			if (!clientProvider.updateTestRunStatus(runEntity)) {
				LOG.info(" >>>>>>>>>> SECTION " + sectionId
						+ " : [UPDATING FAILED]");
				fsec.append(sectionId);
				fsec.append(" ");
				failCount++;
				TDBatchClientUtils.writeUnprocessedSection(outFile, section);
			} else {
				passCount++;
			}
		}

		// release writer resource to be used by others
		TDBatchWriter.shutDown();

		TDBatchClientUtils.logSummary(LOG, passCount, failCount, skipCount,
				fsec.toString());
		clientProvider.shutdown();

		inputFile.renameTo(processedFile);

		int code = TDBatchClientConstants.SUCCESS;
		if (skipCount > 0) {
			code = TDBatchClientConstants.ERR_FEW_SECTIONS_SKIPPED;
		}
		if (failCount > 0) {
			code = TDBatchClientConstants.ERR_FEW_SECTIONS_FAILED;
		}

		return code;
	}

	/**
	 * 
	 * @param args
	 *            : arg[0] : input File name(if available in current directory)
	 *            input File Path (if available in any other directory )
	 * 
	 *            arg[1] : Input File Format ( For Future Implementation )
	 * 
	 */
	public static void main(String[] args) {

		int exitCode = TDBatchClientConstants.ERR_UNKNOWN;
		switch (args.length) {
		case 0:
			exitCode = TDBatchClientConstants.ERR_INPUT_FILE_NOT_IN_ARG;
			LOG.severe("Input file Required as Argument");
			break;
		case 1:
			exitCode = new TDBatchClient(args[0]).processInputFileToQC();
			break;
		default:
			exitCode = TDBatchClientConstants.ERR_ARG_LIST_NOT_SUPPORTED;
			LOG.severe("Argument List NOT SUPPORTED");
			break;
		}

		System.exit(exitCode);
	}

}
