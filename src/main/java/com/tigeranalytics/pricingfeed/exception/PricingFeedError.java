package com.tigeranalytics.pricingfeed.exception;

public class PricingFeedError extends RuntimeException{
    private PricingFeedErrorType pricingFeedErrorType;

    private String message;

    public PricingFeedError(PricingFeedErrorType pricingFeedErrorType,String message) {
        this.pricingFeedErrorType = pricingFeedErrorType;
        this.message=message;
    }

    public PricingFeedErrorType getPricingFeedErrorType() {
        return pricingFeedErrorType;
    }

    public void setPricingFeedErrorType(PricingFeedErrorType pricingFeedErrorType) {
        this.pricingFeedErrorType = pricingFeedErrorType;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
