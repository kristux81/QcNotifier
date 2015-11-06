/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.commons;

import java.net.URI;
import java.net.URISyntaxException;

public final class ClassUtils {

	// no object instantiation for this class 
	private ClassUtils() {}
	
	public static String getJarDir(Class<?> aclass) throws URISyntaxException{

		URI uri = null ;
		try {
			uri = aclass.getProtectionDomain().getCodeSource().getLocation().toURI();
		} catch (SecurityException ex) {
			uri = aclass.getResource(aclass.getSimpleName() + ".class").toURI();
		} 
		
		String extURL = uri.getPath();

		// from getCodeSource
		if (extURL.endsWith(".jar")) {
			extURL = extURL.substring(0, extURL.lastIndexOf('/'));
		}
		else { // from getResource
			String suffix = "/" + (aclass.getName()).replace(".", "/")
					+ ".class";
			extURL = extURL.replace(suffix, "");
			if (extURL.startsWith("jar:") && extURL.endsWith(".jar!")) {
				extURL = extURL.substring(4, extURL.lastIndexOf('/'));
			}
		}

		if (extURL.startsWith("file:/")) {
			extURL = extURL.substring(5);
		}

		return extURL;
	}
	
}
