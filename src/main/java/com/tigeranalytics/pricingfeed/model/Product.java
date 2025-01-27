package com.tigeranalytics.pricingfeed.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Entity
@Table(name="PRODUCTS")
public class Product {
    @Id
    @Column(name = "SKU", length = 50)
    @NotBlank(message = "SKU cannot be blank")
    private String sku;

    @Column(name = "PRODUCT_NAME", nullable = false)
    @NotBlank(message = "Product name cannot be blank")
    private String productName;

    @Column(name = "PRICE", nullable = false)
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @Column(name = "MANUFACTURING_DATE")
    private String manufacturingDate;

    @Column(name="EXPIRY_DATE",nullable = false)
    private LocalDate expiryDate;

    /*@OneToMany(mappedBy = "products", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricingData> pricingData;*/

    public @NotBlank(message = "SKU cannot be blank") String getSku() {
        return sku;
    }

    public void setSku(@NotBlank(message = "SKU cannot be blank") String sku) {
        this.sku = sku;
    }

    public @NotBlank(message = "Product name cannot be blank") String getProductName() {
        return productName;
    }

    public void setProductName(@NotBlank(message = "Product name cannot be blank") String productName) {
        this.productName = productName;
    }

    public @NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") Double getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") Double price) {
        this.price = price;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

   /* public List<PricingData> getPricingData() {
        return pricingData;
    }

    public void setPricingData(List<PricingData> pricingData) {
        this.pricingData = pricingData;
    }*/

}
