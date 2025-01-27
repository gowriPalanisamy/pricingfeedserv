package com.tigeranalytics.pricingfeed.repository;

import com.tigeranalytics.pricingfeed.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store,Long> {
}
