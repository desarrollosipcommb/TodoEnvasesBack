package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    // Find sales by user
    List<Sale> findBySeller(User seller);
    
    // Find sales by user ID
    List<Sale> findBySellerId(Long userId);
    
    // Find sales within a date range
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate ORDER BY s.saleDate DESC")
    List<Sale> findSalesBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    // Find sales by user within a date range
    @Query("SELECT s FROM Sale s WHERE s.seller = :seller AND s.saleDate BETWEEN :startDate AND :endDate ORDER BY s.saleDate DESC")
    List<Sale> findSalesBySellerBetweenDates(@Param("seller") User seller, 
                                         @Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    // Find recent sales (last N sales)
    List<Sale> findTop10ByOrderBySaleDateDesc();
    
    // Get total sales count for a user
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.seller = :seller")
    Long countSalesBySeller(@Param("seller") User seller);
    
    // Get sales ordered by date (newest first)
    List<Sale> findAllByOrderBySaleDateDesc();
}