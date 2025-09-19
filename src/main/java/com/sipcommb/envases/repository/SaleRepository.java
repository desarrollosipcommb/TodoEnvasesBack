package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Sale;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT s FROM Sale s WHERE s.seller.id = :sellerId")
    List<Sale> findBySeller(@Param("sellerId") Long sellerId);

    @Query("SELECT s FROM Sale s WHERE s.totalAmount = :exactPrice")
    Page<Sale> findByExactPrice(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE s.totalAmount BETWEEN :minPrice AND :maxPrice")
    Page<Sale> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

  @Query("SELECT s FROM Sale s "+
        "WHERE (s.saleDate BETWEEN :fechaInicio AND :fechaFin) "+
        "AND (:nombreVendedor IS NULL OR LOWER(s.seller.username) LIKE LOWER(CONCAT('%', :nombreVendedor, '%'))) ")
  Page<Sale> findByFechaAndVendedor(
      @Param("fechaInicio") LocalDate fechaInicio,
      @Param("fechaFin") LocalDate fechaFin,
      @Param("nombreVendedor") String nombreVendedor,
      Pageable pageable
  );

}