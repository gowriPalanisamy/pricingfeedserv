package com.tigeranalytics.pricingfeed.repository;

import com.tigeranalytics.pricingfeed.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAllBySkuIn(Set<String> sku);
}
