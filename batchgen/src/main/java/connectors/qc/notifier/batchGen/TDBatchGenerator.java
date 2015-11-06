package connectors.qc.notifier.batchGen;

import java.util.logging.Logger;

import connectors.qc.notifier.batchGen.mapper.TDBatchMapper;

public class TDBatchGenerator {

	private static final Logger LOG = Logger.getLogger(TDBatchGenerator.class
			.getName());

	public static void main(String[] args) throws Exception {

		if (args.length == 2) {
			new TDBatchMapper(args[1], args[0]).Serialize(null);
		} else {
			LOG.severe("Invalid or No Arguments Provided. Usage : \"THIS_PROG <input_file> <map_file>\"");
			System.exit(1);
		}
	}

}
