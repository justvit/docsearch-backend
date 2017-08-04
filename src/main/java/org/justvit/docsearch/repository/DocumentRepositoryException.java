package org.justvit.docsearch.repository;

/**
 * Error while the docuemnt repo working
 */
public class DocumentRepositoryException extends RuntimeException {
    public DocumentRepositoryException() {
    }

    public DocumentRepositoryException(String message) {
        super(message);
    }

    public DocumentRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public DocumentRepositoryException(Throwable cause) {
        super(cause);
    }

    public DocumentRepositoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
