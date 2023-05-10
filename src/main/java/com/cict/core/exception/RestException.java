package com.cict.core.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Error thrown by calls to a REST service. This exception contains the key to the
 * error only. It is expected that the {@link GenericExceptionMapper} will map the
 * exception to a proper fault response.
 */
public class RestException extends RuntimeException {
    //TODO: need to break this down, function below is from old codes



    private String path;
    private int status;
    private ErrorKey errorKey;
    private Map<String, Object> params = new HashMap<>();

    public RestException(ErrorKey errorKey){
        super(errorKey.getMessage());
        this.errorKey = errorKey;
        this.status = errorKey.getStatusCode();
    }
    /**
     * Constructs the exception.
     */
    public RestException(ErrorKey errorKey, Object... params) {
        super(errorKey.getMessage());

        this.errorKey = errorKey;
        if (params != null) {
            int i = 0;
            if (params.length % 2 != 0) {
                i++;
                if (params[0] instanceof String) {
                    path = (String) params[0];
                } else if (params[0] instanceof ErrorParam) {
                    path = ((ErrorParam) params[0]).getName();
                } else {
                    throw new RuntimeException("RestException with key " + errorKey +
                            " thrown with odd number (" + params.length + ") of parameters ");
                }
            }

            for (; i < params.length; i += 2) {
                String key;
                if (params[i] instanceof ErrorParam) {
                    key = ((ErrorParam) params[i]).getName();
                } else if (params[i] instanceof String) {
                    key = (String) params[i];
                } else {
                    throw new RuntimeException("RestException with param index " + i + " (" +
                            params[i] + ") is not an ErrorParam or String");
                }

                if (path == null) {
                    path = key;
                }

                this.params.put(key, params[i + 1]);
            }
        }
    }

    /**
     * Returns the {@link ErrorKey} associated with this exception.
     */
    public ErrorKey getErrorKey() {
        return errorKey;
    }

    /**
     * Returns the parameters associated with the error.
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * Returns the exception path.
     */
    public String getPath() {
        return path;
    }

    public int getStatus() {
        return status;
    }

}
