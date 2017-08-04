package org.justvit.docsearch.service;

/**
 * Exception might occurr while parsing a search request
 */
public class SearchRequestParsingException extends Exception {
    public SearchRequestParsingException() {
        super();
    }

    public SearchRequestParsingException(String message) {
        super(message);
    }

    public SearchRequestParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchRequestParsingException(Throwable cause) {
        super(cause);
    }

    protected SearchRequestParsingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
