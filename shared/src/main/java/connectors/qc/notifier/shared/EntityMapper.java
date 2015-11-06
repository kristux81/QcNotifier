package connectors.qc.notifier.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class EntityMapper {

	private static final Properties testProperties = new Properties();
	private static final String mapperResource = "tdnotifier.properties";
	private static final Logger LOG = Logger.getLogger(EntityMapper.class
			.getName());

	public EntityMapper() {
		try {
			InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream(mapperResource);
			testProperties.load(inputStream);

			inputStream.close();
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "", e);
			return;
		}
	}

	public EntityMapper(String propFile) {
		try {
			FileInputStream inputStream = new FileInputStream(
					new File(propFile));
			testProperties.load(inputStream);

			inputStream.close();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "", e);
			return;
		} catch (Exception e) {
			LOG.log(Level.WARNING, "", e);
			return;
		}
	}

	public static Properties getTestproperties() {
		return testProperties;
	}
	
}
