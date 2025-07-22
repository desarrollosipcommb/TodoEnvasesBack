package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Sale;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE s.seller.id = :sellerId")
    List<Sale> findBySeller(@Param("sellerId") Long sellerId);
}