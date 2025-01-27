package com.tigeranalytics.pricingfeed.serviceImpl;

import com.tigeranalytics.pricingfeed.model.Product;
import com.tigeranalytics.pricingfeed.repository.ProductRepository;
import com.tigeranalytics.pricingfeed.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> saveProducts(List<Product> products) {
        return productRepository.saveAll(products);
    }

}
