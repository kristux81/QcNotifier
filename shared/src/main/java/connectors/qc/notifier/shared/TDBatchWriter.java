/**
  * @author krvsingh
 */
package connectors.qc.notifier.shared;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TDBatchWriter {

	private static final Logger LOG = Logger.getLogger(TDBatchWriter.class
			.getName());

	private static String currentFile = "";
	private static BufferedWriter writer = null;

	// NO objects for this utility class
	private TDBatchWriter() {
	}

	private static void initWriter(String file) {

		try {
			writer = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			LOG.log(Level.SEVERE, String.format(
					"Failed to Create/Write File : [%s] ; Exception : ", file),
					e);
		}

	}

	public static void initBatchWriter(String file) {

		// singleton
		if (writer == null) {

			// default if no filename provided
			if (file == null || file.length() == 0) {
				currentFile = TDNotifierConstants.DEFAULT_TD_BATCHFILE;
			} else {
				currentFile = file;
			}

			initWriter(currentFile);
		}
		// in order to force reset batch file
		else {
			if (file != null && file.length() > 0
					&& (!currentFile.equals(file))) {

				try {
					writer.close();
				} catch (IOException e) {
					LOG.log(Level.SEVERE, "Failed to close Batch File Writer");
				}
				writer = null;
				initWriter(file);
			}
		}
	}

	public static void addSection() {
		addSection(null);
	}

	public static void addSection(String sec) {

		if (sec == null || sec.length() == 0) {
			sec = TDNotifierConstants.DEFAULT_TD_BATCHFILE_SECTION;
		}

		try {
			writer.append(String.format("[%s]", sec));
			writer.newLine();
		} catch (IOException e) {
			LOG.log(Level.SEVERE,
					String.format(
							"Failed to Write Section : (%s) in File : [%s] ; Exception : ",
							sec, currentFile), e);
		}
	}

	public static void addKeyValue(String key, String value) {

		try {
			if (key != null && key.length() != 0) {
				writer.append(String.format("%s=%s", key, value));
			}
			writer.newLine();

		} catch (IOException e) {
			LOG.log(Level.SEVERE,
					String.format(
							"Failed to Write Key-Value : [%s] in File : [%s] ; Exception : ",
							String.format("%s=%s", key, value), currentFile), e);
		}
	}

	public static void newLine() {

		addKeyValue(null, null);
		flushToFile();
	}

	public static void flushToFile() {

		try {
			writer.flush();
		} catch (IOException e) {
			// ignore
		}
	}
	
	public static void shutDown() {
		
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				// ignore
			}
			writer = null;
		}
	}

}
