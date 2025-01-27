package com.tigeranalytics.pricingfeed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDate;


public class PricingData {

    private Long storeId;
    private String sku;
    private String productName;
    private BigDecimal price;
    private LocalDate date;
    private String errorMessage;

    public PricingData() {
    }

    public PricingData(Long storeId, String sku, String productName, BigDecimal price, LocalDate date, String errorMessage) {
        this.storeId = storeId;
        this.sku = sku;
        this.productName = productName;
        this.price = price;
        this.date = date;
        this.errorMessage = errorMessage;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
