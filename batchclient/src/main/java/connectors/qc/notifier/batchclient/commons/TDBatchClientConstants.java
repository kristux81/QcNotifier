package connectors.qc.notifier.batchclient.commons;

public interface TDBatchClientConstants {

	/*
	 * Returns Codes with BatchClient
	 */
	int ERR_ARG_LIST_NOT_SUPPORTED = -127;
	int ERR_NO_SECTIONS_IN_FILE = -3;
	int ERR_PARSING_INPUT_FILE = -2;
	int ERR_INPUT_FILE_NOT_IN_ARG = -1;
	int SUCCESS = 0;
	int ERR_FEW_SECTIONS_SKIPPED = 1;
	int ERR_FEW_SECTIONS_FAILED = 2;
	int ERR_QC_LOGIN_FAILED = 64;
	int ERR_UNKNOWN = 128 ;

}
