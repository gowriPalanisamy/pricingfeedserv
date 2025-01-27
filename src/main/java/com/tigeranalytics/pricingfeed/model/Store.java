package com.tigeranalytics.pricingfeed.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Entity
@Table(name="Stores")
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @NotBlank(message = "store name cannot be blank")
    @Column(name = "store_Name", nullable = false)
    private String storeName;

    @NotBlank(message = "Store Location cannot be blank")
    @Column(name = "store_Location")
    private String storeLocation;

/*    @OneToMany(mappedBy = "stores", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricingData> pricingData;*/

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public @NotBlank(message = "store name cannot be blank") String getStoreName() {
        return storeName;
    }

    public void setStoreName(@NotBlank(message = "store name cannot be blank") String storeName) {
        this.storeName = storeName;
    }

    public @NotBlank(message = "Store Location cannot be blank") String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(@NotBlank(message = "Store Location cannot be blank") String storeLocation) {
        this.storeLocation = storeLocation;
    }

    /*public List<PricingData> getPricingData() {
        return pricingData;
    }

    public void setPricingData(List<PricingData> pricingData) {
        this.pricingData = pricingData;
    }*/
}
