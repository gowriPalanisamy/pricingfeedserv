package com.tigeranalytics.pricingfeed.controller;

import com.tigeranalytics.pricingfeed.constant.AppConstant;
import com.tigeranalytics.pricingfeed.model.PricingData;
import com.tigeranalytics.pricingfeed.model.Response;
import com.tigeranalytics.pricingfeed.service.PricingDataService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/v1/pricingfeed")
public class PricingDataController {

    @Autowired
    private PricingDataService pricingDataService;

    @PostMapping("/upload")
    @PFAuthPolicy(roles={"ROLE_ADMIN"})
    public CompletableFuture<ResponseEntity<?>> uploadCSVFile(@RequestParam("file") MultipartFile pricingFeedCSVFile){
        CompletableFuture<Response> responseFuture = pricingDataService.validateAndSaveFile(pricingFeedCSVFile,
                AppConstant.EXTENSION);
        return responseFuture.thenApply(response->ResponseEntity.status(HttpStatus.OK)
                    .body("File uploaded and processed successfully.\n"+
                            "Total records: " + response.getTotalNumberOfRecords()+"\n"+
                            "Success Count: "+response.getSuccessRecordCount()+"\n"+
                            "Failure Count: "+response.getFailureRecordCount()+"\n"+
                            "successFilePath: "+response.getSuccessFilePath()+"\n"+
                            "FailureFilePath: "+response.getFailureFilePath()));

    }

    @GetMapping("/search")
    @PFAuthPolicy(roles={"ROLE_ADMIN","ROLE_USER"})
    public ResponseEntity<List<PricingData>> searchPricingData(
            @RequestParam @NotNull Long storeId,
            @RequestParam @NotNull String sku) {
        List<PricingData> result = pricingDataService.findByStoreIdAndSku(storeId, sku);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }


    @PutMapping("/{id}")
    @PFAuthPolicy(roles={"ROLE_ADMIN"})
    public ResponseEntity<PricingData> updatePricingData(
            @PathVariable Long id,
            @RequestBody PricingData updatedData) {
        PricingData result = pricingDataService.updatePricingData(id, updatedData);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
