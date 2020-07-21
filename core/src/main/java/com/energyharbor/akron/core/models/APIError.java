package com.energyharbor.akron.core.models;

import org.apache.http.HttpStatus;

public class APIError {
    private String message;

    public APIError(int status) {
        this.message = defaultMessage(status);
    }

    public APIError(int status, String message) {
        if (message != null) {
            this.message = message;
        } else {
            this.message = defaultMessage(status);
        }
    }

    public APIError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String defaultMessage(int status) {
        switch (status) {
            case HttpStatus.SC_BAD_REQUEST:
                return "Invalid request.";
            case HttpStatus.SC_UNAUTHORIZED:
                return "Unauthorized.";
            case HttpStatus.SC_METHOD_NOT_ALLOWED:
                return "Method not allowed.";
            case HttpStatus.SC_UNPROCESSABLE_ENTITY:
                return "Unable to process request.";
            case HttpStatus.SC_INTERNAL_SERVER_ERROR:
            default:
                return "Internal server error.  Contact site administrator.";
        }
    }
}
