/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.rest;

public class RestException extends RuntimeException {
    
    private static final long serialVersionUID = -5386355008323770858L;
    
    public RestException(Throwable cause) {
        
        super(cause);
    }
    
    public RestException(String message) {
        
        super(message);
    }
    
    public RestException(String message, Throwable cause) {
        
        super(message, cause);
    }
}
