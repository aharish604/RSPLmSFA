package com.rspl.sf.msfa.pushNotification;

/**
 * Created by e10769 on 07-08-2017.
 */

public class GenericException extends Exception {
    private static final long serialVersionUID=1L;

    public GenericException(String errorMessage) {
        super(errorMessage);
    }

    public GenericException(Throwable throwable) {
        super(throwable);
    }
}