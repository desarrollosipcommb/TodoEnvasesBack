package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Cap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    
    // Find all items for a specific sale
    List<SaleItem> findBySale(Sale sale);
    
    // Find all items for a sale by sale ID
    List<SaleItem> findBySaleId(Long saleId);
    
    // Find all sales containing a specific jar
    List<SaleItem> findByJar(Jar jar);
    
    // Find all sales containing a specific cap
    List<SaleItem> findByCap(Cap cap);
    
    // Get total quantity sold for a specific jar
    @Query("SELECT COALESCE(SUM(si.quantity), 0) FROM SaleItem si WHERE si.jar = :jar")
    Integer getTotalQuantitySoldForJar(@Param("jar") Jar jar);
    
    // Get total quantity sold for a specific cap
    @Query("SELECT COALESCE(SUM(si.quantity), 0) FROM SaleItem si WHERE si.cap = :cap")
    Integer getTotalQuantitySoldForCap(@Param("cap") Cap cap);
    
    // Find items by jar ID
    List<SaleItem> findByJarId(Long jarId);
    
    // Find items by cap ID
    List<SaleItem> findByCapId(Long capId);
    
    // Count items for a specific sale
    @Query("SELECT COUNT(si) FROM SaleItem si WHERE si.sale = :sale")
    Long countItemsInSale(@Param("sale") Sale sale);
}
