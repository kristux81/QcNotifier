/**
 * @author krvsingh
 */
package connectors.qc.notifier.batchclient.parsers;

import connectors.qc.notifier.restclient.commons.StringUtils;

/*
 * Implements a Factory Pattern to bind to ResultParser Interface
 * Returns a suitable parser on the basis of File format ( i.e. meta data or file extension )
 * or external stimuli ( some command argument )
 */
public abstract class Parser extends FileParser {
	
	private static Parser parser = null ;
	
	// default parser
	public static Parser getInstance(String path) throws Exception{
		parser = new IniParser(path);
		return parser ;
	}
	
	public static Parser getInstance(String path, String parserType) throws Exception{
			
		// default to ini parser
		if(StringUtils.isNotDefined(parserType)){
			parser = new IniParser(path);
			return parser ;
		}
		
        if("XML".equalsIgnoreCase(parserType)) {
        	parser = new XmlParser(path);
        }
		else if("JSON".equalsIgnoreCase(parserType)) {
			parser = new JsonParser(path);
		}
		else {
			parser = new IniParser(path);
		}
       
        return parser ;
	}

}
