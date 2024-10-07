package com.pasc.lib.lbs.location;

public class LocationException extends RuntimeException {

    private String mMesssage;
    private static final String DEFAULT_MESSAGE = "Location Error";

    public LocationException() {
    }

    public LocationException(String message) {
        super(message);
        mMesssage = message;
    }

    @Override
    public String getMessage() {
        return (mMesssage == null) ? DEFAULT_MESSAGE : mMesssage;
    }

}
