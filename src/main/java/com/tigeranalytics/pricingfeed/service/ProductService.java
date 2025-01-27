package com.tigeranalytics.pricingfeed.service;

import com.tigeranalytics.pricingfeed.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    List<Product> saveProducts(List<Product> products);
}
