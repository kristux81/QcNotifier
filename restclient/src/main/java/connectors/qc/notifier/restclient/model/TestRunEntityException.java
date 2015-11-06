/**
 * @author krvsingh
 */
package connectors.qc.notifier.restclient.model;

public class TestRunEntityException extends RuntimeException {
    
    private static final long serialVersionUID = -3386355008323770858L;
    
    public TestRunEntityException(Throwable cause) {
        
        super(cause);
    }
    
    public TestRunEntityException(String message) {
        
        super(message);
    }
    
    public TestRunEntityException(String message, Throwable cause) {
        
        super(message, cause);
    }
}
