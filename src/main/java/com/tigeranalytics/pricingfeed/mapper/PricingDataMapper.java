package com.tigeranalytics.pricingfeed.mapper;

import com.tigeranalytics.pricingfeed.dataobj.PricingDataDO;
import com.tigeranalytics.pricingfeed.model.PricingData;
import org.slf4j.LoggerFactory;

import java.util.List;
import org.slf4j.Logger;
import java.util.stream.Collectors;

public class PricingDataMapper {

    private static final Logger logger = LoggerFactory.getLogger(PricingDataMapper.class);

    private PricingDataDO populatePricingDataDO(PricingData pricingData) {
        logger.info("Mapping PricingData to PricingDataDO : {}", pricingData);
        PricingDataDO pricingDataDO = new PricingDataDO();
        pricingDataDO.setStoreId(pricingData.getStoreId());
        pricingDataDO.setSku(pricingData.getSku());
        pricingDataDO.setProductName(pricingData.getProductName());
        pricingDataDO.setPrice(pricingData.getPrice());
        pricingDataDO.setDate(pricingData.getDate());
        return pricingDataDO;
    }

    public List<PricingDataDO> populatePricingDataDOList(List<PricingData> pricingDataList){
        logger.info("Mapping a list of {} PricingData records to PricingDataDO.", pricingDataList.size());
        return pricingDataList.stream().map(this::populatePricingDataDO).collect(Collectors.toList());
    }

    private PricingData populatepricingData(PricingDataDO pricingDataDO){
        logger.info("Mapping PricingDataDo to PricingData : {}", pricingDataDO);
        PricingData pricingData=new PricingData();
        pricingData.setStoreId(pricingDataDO.getStoreId());
        pricingData.setSku(pricingDataDO.getSku());
        pricingData.setProductName(pricingDataDO.getProductName());
        pricingData.setPrice(pricingDataDO.getPrice());
        pricingData.setDate(pricingDataDO.getDate());
        return  pricingData;
    }

    public List<PricingData> populatepricingDataList(List<PricingDataDO> pricingDataDOS){
        logger.info("Mapping a list of {} PricingDataDo records to PricingData.", pricingDataDOS.size());
        return pricingDataDOS.stream().map(this::populatepricingData).collect(Collectors.toList());
    }
}
