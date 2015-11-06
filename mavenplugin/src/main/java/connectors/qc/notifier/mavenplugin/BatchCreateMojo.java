/**
 * @author krvsingh
 */

package connectors.qc.notifier.mavenplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import connectors.qc.notifier.batchGen.mapper.TDBatchMapper;
import connectors.qc.notifier.shared.TDNotifierConstants;

@Mojo(name = "batchcreate", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class BatchCreateMojo extends AbstractMojo {

	/**
	 * Location of Absolute batch file path.
	 */
	@Parameter(property = PluginConstants.PLUGIN_PROPERTY_BATCH_FILE, defaultValue = PluginConstants.PLUGIN_PROPERTY_WORKSPACE
			+ TDNotifierConstants.DEFAULT_TD_BATCHFILE)
	private String batchFilePath;

	/**
	 * Location of Absolute surefire test xml report.
	 */
	@Parameter(property = PluginConstants.PLUGIN_PROPERTY_SUREFIREFILE, defaultValue = PluginConstants.PLUGIN_PROPERTY_SUREFIRE
			+ PluginConstants.PLUGIN_PROPERTY_DEFAULT_SUREFIREFILE)
	private String surefireFilePath;
	
	/**
	 * Location of surefire test xml to batch file mapping rule file.
	 */
	@Parameter(property = PluginConstants.PLUGIN_PROPERTY_MAPFILE, defaultValue = PluginConstants.PLUGIN_PROPERTY_WORKSPACE
			+ PluginConstants.PLUGIN_PROPERTY_DEFAULT_MAPFILE)
	private String mapFilePath;

	/**
	 * Entry Point
	 */
	public void execute() throws MojoFailureException, MojoExecutionException {

		try {
			new TDBatchMapper(mapFilePath, surefireFilePath).Serialize(batchFilePath);
		} catch (Exception e) {

			// Will result into a BUILD FAILURE and further execution halted.
			throw new MojoExecutionException("Exception From BatchCreate : ", e);
		}
	}
}
