/**
 * @author krvsingh
 */
package connectors.qc.notifier.batchclient.parsers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Supported File Format : .ini
 * section must be enclosed in "[]" and can names have same or empty name 
 */
public class IniParser extends Parser {

	private static final Logger LOG = Logger.getLogger(IniParser.class
			.getName());

	private Pattern section = Pattern.compile("\\s*\\[([^]]*)\\]\\s*");
	private Pattern keyValue = Pattern.compile("\\s*([^=]*)=(.*)");

	// line Comment Patterns ( following must be at the beginning of line )
	private String hashComment = "#";
	private String cppComment = "//";

	private Map<Integer, Map<String, String>> entries = new HashMap<Integer, Map<String, String>>();

	public IniParser(String path) throws Exception {

		int secCounter = 0;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			throw new Exception("Input file : " + path
					+ " DOES NOT EXIT or is INVALID !!");
		}

		String line;
		Integer sectionId = null;

		try {
			while ((line = br.readLine()) != null) {

				line = line.trim();

				// skip empty lines
				if (line.isEmpty()) {
					continue;
				}

				// ignore comment lines
				if (line.startsWith(hashComment) || line.startsWith(cppComment)) {
					continue;
				}

				Matcher m = section.matcher(line);
				if (m.matches()) {
					sectionId = secCounter;
					secCounter++;

				} else if (sectionId != null) {

					m = keyValue.matcher(line);
					if (m.matches()) {
						String key = m.group(1).trim();
						String value = m.group(2).trim();
						Map<String, String> kv = entries.get(sectionId);

						if (kv == null) {
							entries.put(sectionId,
									kv = new HashMap<String, String>());
						}
						
						//Allow multi-line inputs
						String prevValue = kv.get(key);
						if( prevValue != null){
							value = prevValue + "\n" + value ;
						}
						
						kv.put(key, value);
					}
				}
			}

			if (br != null) {
				br.close();
			}

		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Failed to Parse Input File : " + path
					+ ", Exception : ", e);
		}
	}

	@Override
	public Map<String, String> getSection(Integer sectionId) {

		Map<String, String> kv = entries.get(sectionId);
		if (kv == null) {
			return null;
		}
		return kv;
	}

	@Override
	public String getValue(Integer sectionId, String key) {

		Map<String, String> kv = entries.get(sectionId);
		if (kv == null) {
			return null;
		}
		return kv.get(key);
	}

	@Override
	public int getSectionCount() {
		return entries.size();
	}

	@Override
	public Map<Integer, Map<String, String>> getAllSections() {
		return entries;
	}

	// Debug
	@Override
	public void showAllSections() {

		for (Map.Entry<Integer, Map<String, String>> entry : entries.entrySet()) {
			LOG.info(entry.getKey().toString() + " : "
					+ entry.getValue().toString());
		}
	}

}
