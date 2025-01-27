package com.tigeranalytics.pricingfeed.serviceImpl;

import com.tigeranalytics.pricingfeed.model.Store;
import com.tigeranalytics.pricingfeed.repository.StoreRepository;
import com.tigeranalytics.pricingfeed.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    private StoreRepository storeRepository;

    @Override
    public List<Store> saveStores(List<Store> stores) {
        return storeRepository.saveAll(stores);
    }

    @Override
    public Store findById(Store store) {
        return storeRepository.findById(store.getStoreId()).get();
    }
}
