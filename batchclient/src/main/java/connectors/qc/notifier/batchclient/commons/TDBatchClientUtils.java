package connectors.qc.notifier.batchclient.commons;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import connectors.qc.notifier.shared.TDBatchWriter;

public final class TDBatchClientUtils {

	private TDBatchClientUtils(){}
	
	public static String getOutputFileName(File inputFile){
		
		// build filenames
		final String preFix = (new SimpleDateFormat("MM-dd-yyyy"))
				.format(new Date());
		final long postFix = System.currentTimeMillis();
		final String processedFileName = String.format("%s_%s_%s", preFix,
				inputFile.getName(), postFix);

		// write the processed/unprocessed file to input file's path
		String FileDir = "";
		if (!inputFile.isDirectory()) {
			FileDir = inputFile.getParent();
			if (FileDir == null) {
				FileDir = "";
			} else {
				if( ! FileDir.endsWith(File.separator)){
					FileDir += File.separator ;
				}
			}
		}
		
		return FileDir + processedFileName ;
	}
	
	public static void logSummary(Logger LOG, int pass, int fail, int skip, String flist) {

		LOG.info("********************** SUMMARY ****************************");
		LOG.info(String.format("[PASSED COUNT]  : \t\t\t%s", pass));
		LOG.info(String.format("[FAILED COUNT]  : \t\t\t%s", fail));
		LOG.info(String.format("[SKIPPED COUNT] : \t\t\t%s", skip));
		LOG.info(String.format("[FAILED/SKIPPED SECTION IDS] : %s", flist));
	}
	
	public static void writeUnprocessedSection(String outFile, Map<String, String> section) {

		// this will be called once throughout the lifetime of the batchclient JVM.
		TDBatchWriter.initBatchWriter(outFile + ".unprocessed");
		
		TDBatchWriter.addSection();
		for (Map.Entry<String, String> entry : section.entrySet()) {
			TDBatchWriter.addKeyValue(entry.getKey().toString(), entry
					.getValue().toString());
		}

		// insert newline and flush file buffers to file
		TDBatchWriter.newLine();
	}
}
