package com.tigeranalytics.pricingfeed.controller;

import com.tigeranalytics.pricingfeed.model.Product;
import com.tigeranalytics.pricingfeed.model.Store;
import com.tigeranalytics.pricingfeed.service.ProductService;
import com.tigeranalytics.pricingfeed.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/add")
public class StaticDataController {
    @Autowired
    private StoreService storeService;

    @Autowired
    private ProductService productService;

    // API to add multiple stores
    @PostMapping("/stores")
    public ResponseEntity<?> addStores(@Valid @RequestBody List<Store> stores, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessages = new StringBuilder();
            for (ObjectError error : result.getAllErrors()) {
                errorMessages.append(error.getDefaultMessage()).append("\n");
            }
            return ResponseEntity.badRequest().body(errorMessages.toString());
        }
        List<Store> savedStores = storeService.saveStores(stores);
        return ResponseEntity.ok(savedStores);
    }

    // API to add multiple products
    @PostMapping("/products")
    public ResponseEntity<List<Product>> addProducts(@RequestBody List<Product> products) {
        List<Product> savedProducts = productService.saveProducts(products);
        return ResponseEntity.ok(savedProducts);
    }
}
