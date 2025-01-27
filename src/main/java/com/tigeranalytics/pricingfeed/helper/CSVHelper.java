package com.tigeranalytics.pricingfeed.helper;

import com.tigeranalytics.pricingfeed.dataobj.PricingDataDO;
import com.tigeranalytics.pricingfeed.model.PricingData;
import com.tigeranalytics.pricingfeed.repository.StoreRepository;
import com.tigeranalytics.pricingfeed.service.PricingDataService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CSVHelper {
    private static final Logger logger = LoggerFactory.getLogger(CSVHelper.class);
    public static final String headers[]={"Store ID","SKU","Product Name","Price","Date"};

    @Autowired
    private StoreRepository storeRepository;
    public static List<PricingData> readDataFromCSVFile(InputStream inputStream) {
        logger.info("read the data from thr csv file {}");
        List<PricingData> pricingDataList;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             CSVParser csvParser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader))
        {
            pricingDataList = new ArrayList<>();
            List<CSVRecord> records = csvParser.getRecords();
            for (CSVRecord record : records) {
                PricingData pricingData = new PricingData();

                try {
                    pricingData.setStoreId(Long.valueOf(record.get("Store ID")));
                    pricingData.setSku(record.get("SKU"));
                    pricingData.setProductName(record.get("Product Name"));
                    pricingData.setPrice(new BigDecimal(record.get("Price")));
                    pricingData.setDate(LocalDate.parse(record.get("Date")));
                    pricingDataList.add(pricingData);
                } catch (Exception e) {
                    logger.error("Unable to read the records from the csv file { }",e.getMessage());
                    throw new IllegalArgumentException("Invalid data format in a row: " + record.toString()+
                            e.getMessage());
                }
            }
            return pricingDataList;
        } catch (Exception e) {
            throw new RuntimeException("Error while processing the file : " + e.getMessage());
        }
    }

    public static Set<Long> populateStoreDetailsFromList(List<PricingData> pricingData) {
        return pricingData.stream().map(PricingData::getStoreId).collect(Collectors.toSet());
    }

    public static Set<String> populateSKUFromList(List<PricingData> pricingData) {
        return pricingData.stream().map(PricingData::getSku).collect(Collectors.toSet());
    }

    public static  Map<String,List<PricingData>> filterPricingDataByStoreIdAndSku(List<PricingData> pricingData,
    List<Long> storeIDs, List<String> productIds) {
        logger.info("filter the pricing data by StoreId and Sku ");
        Map<String,List<PricingData>> readyToProcessData=new HashMap<>();

        List<PricingData> dataExistToProcess = pricingData.stream().filter(data -> storeIDs.contains(data.getStoreId()))
                .filter(d -> productIds.contains(d.getSku())).collect(Collectors.toList());
        Predicate<PricingData> dataExistInDB = data -> !storeIDs.contains(data.getStoreId()) ||
                !productIds.contains(data.getSku());
        List<PricingData> storeIdOrSkuNotExistList  = pricingData.stream().filter(dataExistInDB).map(e -> {
            e.setErrorMessage("Store ID or SKU not found in the database");
            return e;
        }).collect(Collectors.toList());
        logger.info("Valid Data to process the records{} ",dataExistToProcess);
        readyToProcessData.put("validData",dataExistToProcess);
        logger.info("Data not exist in db unable to process the records{} ",storeIdOrSkuNotExistList);
        readyToProcessData.put("storeIdOrSkuNotFound",storeIdOrSkuNotExistList);

        return readyToProcessData;
    }


    public static void writeSuccessDataToCsv(List<PricingData> pricingData,  String filePath) throws IOException {
        logger.info("Generating CSV file for success cases : {}", pricingData.size());
        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.
                     withHeader("Store ID", "SKU", "Price", "Date"))) {

            for (PricingData data : pricingData) {
                csvPrinter.printRecord(
                        data.getStoreId(),
                        data.getSku(),
                        data.getPrice(),
                        data.getDate());
            }
        }
    }

    public static void writeInvalidDataToCsv(List<PricingData> invalidData, String filePath) throws IOException {
        logger.info("Generating CSV file for success cases : {}", invalidData.size());
        try (FileWriter writer = new FileWriter(filePath);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.
                     withHeader("Store ID", "SKU", "Price", "Date", "Error Message"))) {

            for (PricingData data : invalidData) {
                csvPrinter.printRecord(
                        data.getStoreId(),
                        data.getSku(),
                        data.getPrice(),
                        data.getDate(),
                        data.getErrorMessage()
                );
            }
        }
    }
}
