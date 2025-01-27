package com.tigeranalytics.pricingfeed.helper;

import com.tigeranalytics.pricingfeed.model.PricingData;
import com.tigeranalytics.pricingfeed.model.Product;
import com.tigeranalytics.pricingfeed.model.Response;
import com.tigeranalytics.pricingfeed.model.Store;
import com.tigeranalytics.pricingfeed.repository.ProductRepository;
import com.tigeranalytics.pricingfeed.repository.StoreRepository;
import com.tigeranalytics.pricingfeed.serviceImpl.PricingDataServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FileValidationUtility {

    private static final Logger logger = LoggerFactory.getLogger(FileValidationUtility.class);

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductRepository productRepository;
    
    public void validateFile(MultipartFile file, String fileExtension){
        logger.info("started the validation for the file:{} ",fileExtension);
        if(Objects.isNull(file))
            throw new IllegalArgumentException("No file uploaded. Please provide the file name");

        if(file.isEmpty())
            throw new IllegalArgumentException("File is empty. Please upload the file with data");

        if(file.getOriginalFilename()==null || !file.getOriginalFilename().toLowerCase().
                endsWith(fileExtension.toLowerCase())){
            throw new IllegalArgumentException("Invalid File Type. Only files with extension "+fileExtension+" " +
                    "are supported");
        }
    }

    public Map<String, List<PricingData>> validateRecords(MultipartFile file) throws IOException {
        logger.info("Starting process to validate and persist PricingData records.");
        List<PricingData> oriPricingDataList = CSVHelper.readDataFromCSVFile(file.getInputStream());
        Map<String,List<PricingData>> processedPricingDataMap=processPricingData(oriPricingDataList);
        processedPricingDataMap.put("TotalRecordCount",oriPricingDataList);
        return processedPricingDataMap;
    }

    private Map<String, List<PricingData>> processPricingData(List<PricingData> pricingDataList) {
        Map<String,List<PricingData>> processedPricingDataMap=new HashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<PricingData> validData = new ArrayList<>();
        List<PricingData> invalidData = new ArrayList<>();

        //validate the fields one by one
        for (PricingData data : pricingDataList) {
            String validationError = validatePricingData(data, dateFormatter);

            if (validationError == null) {
                validData.add(data);
            } else {
                data.setErrorMessage(validationError);
                invalidData.add(data);
            }
        }
        logger.info("Validation completed. Valid: {}, Invalid: {}", validData.size(), invalidData.size());
        Map<String, List<PricingData>> filteredPricingDataByIDAndSku = filterByStoreIdAndSku(validData);
        processedPricingDataMap.put("successData",validData);
        processedPricingDataMap.put("invalidData",invalidData);
        processedPricingDataMap.put("validData",filteredPricingDataByIDAndSku.get("validData"));
        processedPricingDataMap.put("storeSKUNotFound",filteredPricingDataByIDAndSku.get("storeIdOrSkuNotFound"));
        return processedPricingDataMap;
    }

    private Map<String, List<PricingData>>  filterByStoreIdAndSku(List<PricingData> validData) {
        Set<Long> uniqueStoreIds = CSVHelper.populateStoreDetailsFromList(validData);
        Set<String> uniqueSKUs = CSVHelper.populateSKUFromList(validData);

        List<Store> storeList = storeRepository.findAllById(uniqueStoreIds);
        List<Product> productList = productRepository.findAllBySkuIn(uniqueSKUs);

        List<Long> storeIDs = storeList.stream().map(Store::getStoreId).collect(Collectors.toList());
        List<String> productIds = productList.stream().map(Product::getSku).collect(Collectors.toList());

        return CSVHelper.filterPricingDataByStoreIdAndSku(validData, storeIDs, productIds);
    }

    private String validatePricingData(PricingData data, DateTimeFormatter dateFormatter) {
        if (isNullOrEmpty(String.valueOf(data.getStoreId()))) {
            logger.warn("Invalid record: Missing SKU for Store ID: {}", data.getStoreId());
            return "Store ID is required";
        }

        if (isNullOrEmpty(data.getSku())) {
            logger.warn("Invalid record: Missing Store ID for SKU: {} ",data.getSku());
            return "SKU is required";
        }

        if (isPriceValid(data.getPrice())) {
            logger.warn("Invalid record: Invalid Price for Store ID: {}, SKU: {}", data.getStoreId(), data.getSku());
            return "Price must be a valid number greater than zero";
        }

        if (Objects.isNull(data.getDate()) || !isValidDate(String.valueOf(data.getDate()), dateFormatter)) {
            logger.warn("Date format is invalid for Date:{} ",data.getDate());
            return "Date must be in the format YYYY-MM-DD";
        }
        return null;
    }

    private boolean isPriceValid(BigDecimal price) {
        return Objects.nonNull(price) && price.compareTo(BigDecimal.ZERO)  <= 0;
    }

    private boolean isNullOrEmpty(String value) {
        return Objects.isNull(value) && value.trim().isEmpty();
    }

    private boolean isValidDate(String date, DateTimeFormatter formatter) {
        try {
            LocalDate.parse(date, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
