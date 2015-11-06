/**
 * @author krvsingh
 */
package connectors.qc.notifier.batchGen.mapper;

public class MappingRuleParsingException extends RuntimeException {
    
    private static final long serialVersionUID = -338635500830858L;
    
    public MappingRuleParsingException(Throwable cause) {
        
        super(cause);
    }
    
    public MappingRuleParsingException(String message) {
        
        super(message);
    }
    
    public MappingRuleParsingException(String message, Throwable cause) {
        
        super(message, cause);
    }
}
