/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.qc.rest;

public class QCException extends RuntimeException {
    
    private static final long serialVersionUID = -4386355008323770858L;
    
    public QCException(Throwable cause) {
        
        super(cause);
    }
    
    public QCException(String message) {
        
        super(message);
    }
    
    public QCException(String message, Throwable cause) {
        
        super(message, cause);
    }
}
