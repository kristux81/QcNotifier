package connectors.qc.notifier.restclient.commons;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileUtils {

	private FileUtils() {
	}

	private static final Logger LOG = Logger.getLogger(FileUtils.class
			.getName());

	/**
	 * builds url file content for url
	 * 
	 * @param url
	 * @return url File Content as byte array
	 * 
	 *         Typical URL File Content :
	 * 
	 *         [InternetShortcut] 
	 *         URL=<url>
	 * 
	 */
	public static byte[] getUrlFileData(String url) {

		final byte[] shortcutHeader = "[InternetShortcut]\r\n".getBytes();
		final byte[] urlDescriptor = ("URL=" + url).getBytes();

		ByteArrayOutputStream content = new ByteArrayOutputStream();
		content.write(shortcutHeader, 0, shortcutHeader.length);
		content.write(urlDescriptor, 0, urlDescriptor.length);

		return content.toByteArray();
	}

	/**
	 * check if it is a Url
	 * 
	 * @param filePath
	 * @return Url name if it is a url otherwise null
	 */
	public static boolean isUrlFile(String filePath) {

		String urlPattern = "((https?|ftp):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(filePath);
		if (m.find()) {
			return true;
		}
		
		return false;
	}

	/**
	 * get file content for local/physical files
	 * 
	 * @param filePath
	 * @return file content as byte array
	 */
	public static byte[] getFileData(String filePath) {

		File file = new File(filePath);
		byte[] result = null;
		try {
			InputStream iStream = new BufferedInputStream(new FileInputStream(
					file));
			result = readFileToArray(iStream);
		} catch (FileNotFoundException ex) {
			LOG.log(Level.WARNING, "", ex);
		}
		
		return result;
	}


    /**
     * Convert file inputStream to byte array
     * 
     * @param iStream
     * @return byte array of file content
     */
	private static byte[] readFileToArray(InputStream iStream) {

		byte[] buffer = new byte[32 * 1024];
		ByteArrayOutputStream result = null;
		try {
			try {
				result = new ByteArrayOutputStream(buffer.length);
				int bytesRead = -1;
				while ((bytesRead = iStream.read(buffer)) > 0) {
					result.write(buffer, 0, bytesRead);
				}
			} finally {
				iStream.close();
			}
		} catch (IOException ex) {
			LOG.log(Level.WARNING, "", ex);
		}
		return result.toByteArray();
	}

}
