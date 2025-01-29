package com.tigeranalytics.pricingfeed.service;

import com.tigeranalytics.pricingfeed.model.PricingData;
import com.tigeranalytics.pricingfeed.model.Response;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public interface PricingDataService {
    List<PricingData> findByStoreIdAndSku(Long storeId, String sku);
    PricingData updatePricingData(Long id, PricingData updatedData);
    CompletableFuture<Response> validateAndSaveFile(MultipartFile pricingFeedCSVFile, String extension);
}
