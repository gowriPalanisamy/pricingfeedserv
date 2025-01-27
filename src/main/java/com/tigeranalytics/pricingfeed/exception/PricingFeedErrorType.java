package com.tigeranalytics.pricingfeed.exception;

public enum PricingFeedErrorType {

    DATA_NOT_FOUND("Data not found"),
    INVALID_TOKEN("Invalid Token "),
    USER_NOT_FOUND("User not found"),
    EXPIRED_TOKEN("Token expired"),
    ACCESS_FORBIDDEN("User Access Denied");
    private String message;
    PricingFeedErrorType(String message) {
        this.message=message;
    }
}
