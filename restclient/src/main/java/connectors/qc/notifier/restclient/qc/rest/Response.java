package connectors.qc.notifier.restclient.qc.rest;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * This is a naive implementation of an HTTP response. We use it to simplify matters in the
 * examples. It is nothing more than a container of the response headers and the response body.
 */
public class Response {
    
    private Map<String, List<String>> headers;
    private byte[] body;
    private Exception failure;
    private int statusCode = -1;
    
    public Response() {
        
        this(null, null, null, -1);
    }
    
    public Response(Exception failure) {
        
        this(null, null, failure, -1);
    }
    
    public Response(
            Map<String, List<String>> responseHeaders,
            byte[] responseBody,
            Exception failure,
            int statusCode) {
        
        headers = responseHeaders;
        body = responseBody;
        this.failure = failure;
        this.statusCode = statusCode;
    }
    
    public Map<String, List<String>> getHeaders() {
        
        return headers;
    }
    
    public void setHeaders(Map<String, List<String>> responseHeaders) {
        
        headers = responseHeaders;
    }
    
    public byte[] getData() {
        
        return body;
    }
    
    public void setData(byte[] responseBody) {
        
        body = responseBody;
    }
    
    /**
     * @return the failure if the access to the requested URL failed, such as a 404 or 500. If no
     *         such failure occurred this method returns null.
     */
    public Exception getFailure() {
        
        return failure;
    }
    
    public void setFailure(Exception cause) {
        
        this.failure = cause;
    }
    
    public int getStatusCode() {
        
        return statusCode;
    }
    
    public void setStatusCode(int status) {
        
        statusCode = status;
    }
    
    public boolean isOk() {
        
        return getFailure() == null
               && (getStatusCode() == HttpURLConnection.HTTP_OK
                   || getStatusCode() == HttpURLConnection.HTTP_CREATED || getStatusCode() == HttpURLConnection.HTTP_ACCEPTED);
    }
    
    /**
     * @see Object#toString() return the contents of the byte[] body as a string.
     */
    @Override
    public String toString() {
        
        return new String(body);
    }
}
