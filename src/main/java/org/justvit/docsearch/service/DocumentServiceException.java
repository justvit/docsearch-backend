package org.justvit.docsearch.service;

/**
 * Error while the document service working
 */
public class DocumentServiceException extends RuntimeException {
    public DocumentServiceException() {
    }

    public DocumentServiceException(String message) {
        super(message);
    }

    public DocumentServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentServiceException(Throwable cause) {
        super(cause);
    }

    public DocumentServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
