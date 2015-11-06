/**
 * @author krvsingh
 */
package connectors.qc.notifier.batchGen.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import connectors.qc.notifier.restclient.model.TestRunEntity;
import connectors.qc.notifier.shared.TDBatchWriter;
import connectors.qc.notifier.shared.TDNotifierConstants;
import connectors.qc.notifier.shared.TestRunEntityMapper;

public class TDBatchMapper {

	private static MappingParser propParser = null;
	private static Properties mappingProp = null;

	private List<TestRunEntity> sections = new ArrayList<TestRunEntity>();
	private static final TestRunEntityMapper runEntity = TestRunEntityMapper
			.getInstance(System
					.getProperty(TDNotifierConstants.MAPPER_PROPERTY_FILE));;
	private static final Logger LOG = Logger.getLogger(TDBatchMapper.class
			.getName());

	public TDBatchMapper(String propFile, String xml) throws Exception {

		mappingProp = new Properties();
		try {
			FileInputStream inputStream = new FileInputStream(
					new File(propFile));
			mappingProp.load(inputStream);

			inputStream.close();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "", e);
			return;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "", e);
			return;
		}

		// get parser
		propParser = getMapParser(mappingProp.getProperty("_parser"));
		propParser.setInputFile(xml);

		// extract objects from input file to batch file
		extractRunObjects();
	}

	protected MappingParser getMapParser(String parserClass) {

		MappingParser propParser = null;
		if (parserClass != null & parserClass.length() > 0) {
			try {
				propParser = (MappingParser) Class.forName(parserClass)
						.newInstance();

				LOG.info("Using Custom Parser : " + parserClass);

			} catch (ClassNotFoundException e) {
				LOG.log(Level.SEVERE, "", e);
			} catch (ClassCastException e) {
				LOG.log(Level.SEVERE, "", e);
			} catch (InstantiationException e) {
				LOG.log(Level.SEVERE, "", e);
			} catch (IllegalAccessException e) {
				LOG.log(Level.SEVERE, "", e);
			} catch (SecurityException e) {
				LOG.log(Level.SEVERE, "", e);
			} catch (IllegalArgumentException e) {
				LOG.log(Level.SEVERE, "", e);
			}
		}

		// default to Provided property parser
		if (propParser == null) {

			propParser = new MappingParser();
			LOG.info("Defaulting to Provided Property Parser");
		}

		return propParser;
	}

	protected String[] getFieldsList(String fieldName) {
		return propParser.resolveValue(mappingProp.getProperty(fieldName))
				.split(MappingParser.SENTRY);
	}

	protected void extractRunObjects() throws Exception {

		String testInstance[] = getFieldsList("_testInstance");
		String testSetName[] = getFieldsList("_testSetName");
		String testSetPath[] = getFieldsList("_testSetPath");
		String testPath[] = getFieldsList("_testPath");
		String testStatus[] = getFieldsList("_testStatus");

		for (int i = 0; i < testInstance.length; i++) {

			TestRunEntity tre = new TestRunEntity();
			tre.setTestInstance(testInstance[i]);

			try {
				tre.setTestSetName(testSetName[i]);
			} catch (ArrayIndexOutOfBoundsException e) {
				tre.setTestSetName(testSetName[0]);
			}

			try {
				tre.setTestSetPath(testSetPath[i]);
			} catch (ArrayIndexOutOfBoundsException e) {
				tre.setTestSetPath(testSetPath[0]);
			}

			try {
				tre.setTestPath(testPath[i]);
			} catch (ArrayIndexOutOfBoundsException e) {
				tre.setTestPath(testPath[0]);
			}

			tre.setTestStatus(testStatus[i]);

			sections.add(tre);
		}
	}

	protected static String initWriter(String filename) {

		String batchFile = "";
		if (filename != null && filename.length() > 0) {
			batchFile = filename;
		} else {
			batchFile = System.getProperty(
					TDNotifierConstants.PROPERTY_TD_BATCHFILE,
					TDNotifierConstants.DEFAULT_TD_BATCHFILE);
		}
		TDBatchWriter.initBatchWriter(batchFile);

		return batchFile;
	}

	public void Serialize(String filename) {

		filename = initWriter(filename);

		// serialize to batch file
		int cnt = 0;
		for (Iterator<TestRunEntity> iterator = sections.iterator(); iterator.hasNext();) {
			TestRunEntity tre = iterator.next();

			TDBatchWriter.addSection();
			TDBatchWriter.addKeyValue(runEntity.testSetName, tre.getTestSetName());
			TDBatchWriter.addKeyValue(runEntity.testInstance, tre.getTestInstance());
			TDBatchWriter.addKeyValue(runEntity.testStatus, tre.getTestStatus());
			TDBatchWriter.addKeyValue(runEntity.testPath, tre.getTestPath());
			TDBatchWriter.addKeyValue(runEntity.testSetPath, tre.getTestSetPath());

			// insert newline and flush file buffers to file
			TDBatchWriter.newLine();
			cnt++;
		}

		LOG.info(String.format("[%s] Sections writen to batch file : [%s]",
				cnt, filename));
	}

}
