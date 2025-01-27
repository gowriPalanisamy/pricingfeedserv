package com.tigeranalytics.pricingfeed.service;

import com.tigeranalytics.pricingfeed.model.Store;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface StoreService {
    List<Store> saveStores(List<Store> stores);
    Store findById(Store store);
}
