package connectors.qc.notifier.batchGen.mapper;

import java.util.List;
import java.util.logging.Logger;

import connectors.qc.notifier.batchGen.parsers.IXmlParser;
import connectors.qc.notifier.batchGen.parsers.XPathParser;

/**
 * Sample Map file :
 * 
 * _testSetPath = /Root/VA 4.12.1 Automation Runs 
 * _testSetName = Smoke
 *                 Format =>            XPATH::ATTRIB
 * _testStatus = <XML_DATA>testsuite/testcase::status</XML_DATA>
 * 
 *                 Format =>         XPATH::ATTRIB#VALUE_LEN_FROM_BEGIN_INDEX 
 * _testPath = /Subject/<XML_DATA>testsuite::name#12</XML_DATA>/Linux
 * 
 *                 Format =>            XPATH::ATTRIB#VALUE_BEGIN_INDEX,VALUE_END_INDEX 
 * _testInstance = <XML_DATA>testsuite/testcase::name#4,10</XML_DATA>_test
 * 
 */

public class MappingParser {

	public static final String scoper = "::";
	public static final String pointer = "#";
	public static final String xmlTag = "<XML_DATA>";
	public static final String xmlTagEnd = "</XML_DATA>";
	public static final String SENTRY = "%";

	private String xmlFile = "";
	public void setInputFile(String inputFile) {
		xmlFile = inputFile;
	}


	private static final Logger LOG = Logger.getLogger(MappingParser.class
			.getName());

	private String getElementValue(String attrName, String xpath, String length) {

		/* 0 value means complete attrib content by default */
		int len = 0;

		/* large value not expected to be encountered */
		int end = -2049;

		if (length.indexOf(",") == -1) {
			len = Integer.parseInt(length.trim());
		} else {
			String tmpLen[] = length.split(",");
			if (tmpLen.length == 2) {
				len = Integer.parseInt(tmpLen[0].trim());
				end = Integer.parseInt(tmpLen[1].trim());
			}
		}

		IXmlParser parser = new XPathParser(xmlFile, xpath);
		List<String> list = parser.getValueList(parser.getChildNodes(),
				attrName);

		String temp = "";
		for (String val : list) {

			if (end == -2049) {
				if (len == 0) {
					temp += val;
				} else {
					temp += val.substring(0, len);
				}
			} else {
				if (end < 0) {
					val = val.substring(0, val.length() + end);
					temp += val.substring(len);
				} else {
					temp += val.substring(len, end);
				}
			}

			if (temp.length() > 0) {
				temp += SENTRY;
			}
		}

		// remove trailing sentry
		if (temp.length() > 1) {
			temp = temp.substring(0, temp.length() - 1);
		}

		return temp;
	}
	
	private String resolveXmlEntity(String input) {

		input = input.trim();
		String[] indexSplit = null;
		String[] atribSplit = input.split(scoper);

		switch (atribSplit.length) {

		case 1: // no attributes may be data values

			indexSplit = atribSplit[0].split(pointer);
			switch (indexSplit.length) {
			case 2: // limited text content
				return getElementValue(null, indexSplit[0], indexSplit[1]);

			case 1: // full text content
			default: // multiple data pointers : miss config ( ignore data
						// pointers )
				return getElementValue(null, indexSplit[0], "0");
			}

		case 2: // attribute provided

			indexSplit = atribSplit[1].split(pointer);
			switch (indexSplit.length) {
			case 2: // limited attrib content
				return getElementValue(indexSplit[0], atribSplit[0],
						indexSplit[1]);

			case 1: // full attrib content
			default: // multiple data pointers : miss config ( ignore data
						// pointers )
				return getElementValue(indexSplit[0], atribSplit[0], "0");
			}

			// multiple scopers : miss config
		default:
			return null;
		}
	}

	
	public String resolveValue(String input) throws MappingRuleParsingException {

		if (input == null) {
			return "";
		}

		if (input != null && input.indexOf(xmlTag) == -1
				&& input.indexOf(scoper) == -1) {

			return input;
		} else {
			String temp = "";
			String append = "";

			if (input.indexOf(xmlTag) != -1) {

				String[] firstSplit = input.split(xmlTag);
				for (int i = 0; i < firstSplit.length; i++) {

					if (firstSplit[i].indexOf(xmlTagEnd) != -1) {

						String[] secondSplit = firstSplit[i].split(xmlTagEnd);
						if (secondSplit.length <= 2) {

							temp += resolveXmlEntity(secondSplit[0]);
							if (secondSplit.length == 2) {
								append = secondSplit[1];
							}
						} else {
							throw new MappingRuleParsingException(
									"Failed to Parse Too Complex Rule : "
											+ input);
						}

					} else {
						temp += firstSplit[i];
					}
				}
			}

			return temp.replaceAll(SENTRY, append + SENTRY) + append;
		}
	}

}
