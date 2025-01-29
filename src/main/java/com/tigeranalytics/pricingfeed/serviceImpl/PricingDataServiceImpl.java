package com.tigeranalytics.pricingfeed.serviceImpl;

import com.tigeranalytics.pricingfeed.constant.AppConstant;
import com.tigeranalytics.pricingfeed.dataobj.PricingDataDO;
import com.tigeranalytics.pricingfeed.exception.PricingFeedError;
import com.tigeranalytics.pricingfeed.exception.PricingFeedErrorType;
import com.tigeranalytics.pricingfeed.helper.CSVHelper;
import com.tigeranalytics.pricingfeed.helper.FileValidationUtility;
import com.tigeranalytics.pricingfeed.mapper.PricingDataMapper;
import com.tigeranalytics.pricingfeed.model.PricingData;
import com.tigeranalytics.pricingfeed.model.Response;
import com.tigeranalytics.pricingfeed.repository.PricingDataRepository;
import com.tigeranalytics.pricingfeed.service.PricingDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class PricingDataServiceImpl implements PricingDataService {

    private static final Logger logger = LoggerFactory.getLogger(PricingDataServiceImpl.class);
    private final PricingDataMapper pricingDataMapper = new PricingDataMapper();
    private final String successFilepath =AppConstant.SUCCESSFILEPATH;
    private final String failureFilepath=AppConstant.FAILUREFILEPATH;

    @Autowired
    private PricingDataRepository pricingDataRepository;

    @Autowired
    private FileValidationUtility fileValidationUtility;

    @Override
    public CompletableFuture<Response> validateAndSaveFile(MultipartFile pricingFeedCSVFile, String extension) {
        logger.info("Validate and process the file.");
        fileValidationUtility.validateFile(pricingFeedCSVFile, AppConstant.EXTENSION);
        CompletableFuture<Response> response=processAndSave(pricingFeedCSVFile);
        return response;
    }

    @Override
    public List<PricingData> findByStoreIdAndSku(Long storeId, String sku) {
        List<PricingDataDO> pricingDataDOList = pricingDataRepository.findByStoreIdAndSku(storeId,sku);

        List<PricingData> pricingDataList = new ArrayList<>();
        for(int i=0; i<pricingDataDOList.size(); i++){
            pricingDataList.add(populatePricingData(pricingDataDOList.get(i)));
        }
        return pricingDataList;
    }

    private PricingData populatePricingData(PricingDataDO pricingDataDO) {
        return new PricingData(pricingDataDO.getStoreId(),pricingDataDO.getSku(),pricingDataDO.getProductName(),
                pricingDataDO.getPrice(),pricingDataDO.getDate(),null);
    }

    @Override
    public PricingData updatePricingData(Long id, PricingData updatedData) {
        Optional<PricingDataDO> existingData =pricingDataRepository.findById(id);
        if (existingData.isPresent()) {
            PricingDataDO pricingData = existingData.get();
            pricingData.setStoreId(updatedData.getStoreId());
            pricingData.setSku(updatedData.getSku());
            pricingData.setProductName(updatedData.getProductName());
            pricingData.setPrice(updatedData.getPrice());
            pricingData.setDate(updatedData.getDate());
            return populatePricingDataVO(pricingDataRepository.save(pricingData));
        } else {
            throw new PricingFeedError(PricingFeedErrorType.DATA_NOT_FOUND,"PricingData with id " + id +
                    " not found");
        }
    }

    private PricingData populatePricingDataVO(PricingDataDO pricingDataDO) {
        return new PricingData(pricingDataDO.getStoreId(),pricingDataDO.getSku(),pricingDataDO.getProductName(),
                pricingDataDO.getPrice(),pricingDataDO.getDate(),null);
    }

    @Async
    public CompletableFuture<Response> processAndSave(MultipartFile file) {
        Response response=new Response();
        try {
            Map<String, List<PricingData>> finalPricingDataList=fileValidationUtility.validateRecords(file);
            logger.info("Completed the Validation for the Pricing Data records..");
            List<PricingData> pricingDataList = finalPricingDataList.get("validData");
            List<PricingDataDO> finalPricingDataDOList = pricingDataMapper.populatePricingDataDOList(pricingDataList);
            List<PricingData> InvalidDataList = prepareInValidDataToWriteInCsv(finalPricingDataList);
            pricingDataRepository.saveAll(finalPricingDataDOList);
            logger.info("Successfully persisted {} records.", finalPricingDataDOList.size());
            CSVHelper.writeSuccessDataToCsv(pricingDataMapper.populatepricingDataList(finalPricingDataDOList), successFilepath);
            CSVHelper.writeInvalidDataToCsv(InvalidDataList,failureFilepath);
            logger.info("CSV files generated for success and failure data.");
            response.setTotalNumberOfRecords(finalPricingDataList.get("TotalRecordCount").size());
            response.setSuccessRecordCount(pricingDataList.size());
            response.setFailureRecordCount(InvalidDataList.size());
            response.setSuccessFilePath(successFilepath);
            response.setFailureFilePath(failureFilepath);
            return CompletableFuture.completedFuture(response);
        }catch (Exception e) {
            logger.error("Error occurred while saving pricing data: {}", e.getMessage());
            response.setTotalNumberOfRecords(0);
            response.setSuccessRecordCount(0);
            response.setFailureRecordCount(0);
            response.setSuccessFilePath("");
            response.setFailureFilePath("");
            return CompletableFuture.completedFuture(response);
        }
    }

    private List<PricingData> prepareInValidDataToWriteInCsv(Map<String, List<PricingData>> finalPricingDataList) {
        List<PricingData> invalidDatas = finalPricingDataList.get("invalidData");
        List<PricingData> storeSKUNotFoundList = finalPricingDataList.get("storeSKUNotFound");
        List<PricingData> pricingDatas=new ArrayList<>();
        pricingDatas.addAll(invalidDatas);
        pricingDatas.addAll(storeSKUNotFoundList);
        return pricingDatas;
    }

}
