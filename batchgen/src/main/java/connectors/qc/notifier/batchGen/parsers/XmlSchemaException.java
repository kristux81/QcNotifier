/**
 * @author krvsingh
 */
package connectors.qc.notifier.batchGen.parsers;

public class XmlSchemaException extends RuntimeException {
    
    private static final long serialVersionUID = -3386355008323L;
    
    public XmlSchemaException(Throwable cause) {
        
        super(cause);
    }
    
    public XmlSchemaException(String message) {
        
        super(message);
    }
    
    public XmlSchemaException(String message, Throwable cause) {
        
        super(message, cause);
    }
}
