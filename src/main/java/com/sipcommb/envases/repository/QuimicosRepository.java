package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Quimicos;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuimicosRepository extends JpaRepository<Quimicos, Integer> {
    // Additional query methods can be defined here if needed
    
    @Query("SELECT q FROM Quimicos q WHERE q.name = :name")
    Optional<Quimicos> findByName(@Param("name") String name);

    List<Quimicos> findByActiveTrue();

    Page<Quimicos> findByActiveTrue(Pageable pageable);

    List<Quimicos> findByActiveFalse();

    Page<Quimicos> findByActiveFalse(Pageable pageable);

    @Query("SELECT q FROM Quimicos q WHERE q.active = 1 AND q.unitPrice = :exactPrice")
    Page<Quimicos> findByExactPrice(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT q FROM Quimicos q WHERE q.active = 1 AND q.unitPrice BETWEEN :minPrice AND :maxPrice")
    Page<Quimicos> findByPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    Page<Quimicos> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT q FROM Quimicos q WHERE q.active = 1 AND q.name LIKE %:name% AND q.active = 1")
    Page<Quimicos> findByNameContainingIgnoreCaseAndActiveTrue(@Param("name") String name, Pageable pageable);

}
