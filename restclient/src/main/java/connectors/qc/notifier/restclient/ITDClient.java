/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient;

import connectors.qc.notifier.restclient.model.TestRunEntity;
import connectors.qc.notifier.restclient.model.TestRunEntityCustom;

/*
 * agreement for a QC Notifier
 */
public interface ITDClient {

	boolean updateTestRunStatus(TestRunEntity runEntity);

	void refresh();

	void shutdown();

	void setRunEntityCustom(TestRunEntityCustom customRunEntity);
}
