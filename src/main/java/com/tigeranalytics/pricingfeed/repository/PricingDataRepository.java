package com.tigeranalytics.pricingfeed.repository;

import com.tigeranalytics.pricingfeed.dataobj.PricingDataDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingDataRepository extends JpaRepository<PricingDataDO,Integer> {
    /*@Query("SELECT p FROM PricingData p WHERE " +
            "(:storeId IS NULL OR p.storeId = :storeId) AND " +
            "(:sku IS NULL OR p.sku = :sku) AND " +
            "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
            "(:price IS NULL OR p.price = :price) AND " +
            "(:date IS NULL OR p.date = :date)")
    List<PricingData> searchPricingData(
            @Param("store_id") Long storeId,
            @Param("sku") String sku,
            @Param("product_name") String productName,
            @Param("price") Double price,
            @Param("date") String date
    );
*/
    List<PricingDataDO> findByStoreIdAndSku(Long storeId, String sku);
    Optional<PricingDataDO> findById(Long id);
}
