package com.tigeranalytics.pricingfeed.model;

import java.util.List;

public class Response {

    private Integer totalNumberOfRecords;
    private Integer successRecordCount;
    private Integer failureRecordCount;
    private String successFilePath;
    private String failureFilePath;

    public Response() {
    }

    public Response(Integer totalNumberOfRecords, Integer successRecordCount, Integer failureRecordCount, String successFilePath, String failureFilePath) {
        this.totalNumberOfRecords = totalNumberOfRecords;
        this.successRecordCount = successRecordCount;
        this.failureRecordCount = failureRecordCount;
        this.successFilePath = successFilePath;
        this.failureFilePath = failureFilePath;
    }

    public Integer getTotalNumberOfRecords() {
        return totalNumberOfRecords;
    }

    public void setTotalNumberOfRecords(Integer totalNumberOfRecords) {
        this.totalNumberOfRecords = totalNumberOfRecords;
    }

    public Integer getSuccessRecordCount() {
        return successRecordCount;
    }

    public void setSuccessRecordCount(Integer successRecordCount) {
        this.successRecordCount = successRecordCount;
    }

    public Integer getFailureRecordCount() {
        return failureRecordCount;
    }

    public void setFailureRecordCount(Integer failureRecordCount) {
        this.failureRecordCount = failureRecordCount;
    }

    public String getSuccessFilePath() {
        return successFilePath;
    }

    public void setSuccessFilePath(String successFilePath) {
        this.successFilePath = successFilePath;
    }

    public String getFailureFilePath() {
        return failureFilePath;
    }

    public void setFailureFilePath(String failureFilePath) {
        this.failureFilePath = failureFilePath;
    }
}
