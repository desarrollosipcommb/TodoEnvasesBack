package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Extractos;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExtractosRepository extends JpaRepository<Extractos, Integer> {
    
    Optional<Extractos> findByName(String name);

    List<Extractos> findAllByActiveTrue();

    Page<Extractos> findAllByActiveTrue(Pageable pageable);

    List<Extractos> findAllByActiveFalse();

    Page<Extractos> findAllByActiveFalse(Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.name LIKE %:name%")
    Page<Extractos> findLikeName(@Param("name") String name, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.name LIKE %:name% AND e.active = 1")
    Page<Extractos> findLikeNameActive(@Param("name") String name, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price22ml BETWEEN :minPrice AND :maxPrice")
    Page<Extractos> findByPrice22mlBetween(@Param("minPrice") BigDecimal minPrice,
                                           @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price60ml BETWEEN :minPrice AND :maxPrice")
    Page<Extractos> findByPrice60mlBetween(@Param("minPrice") BigDecimal minPrice,
                                           @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price125ml BETWEEN :minPrice AND :maxPrice")
    Page<Extractos> findByPrice125mlBetween(@Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price250ml BETWEEN :minPrice AND :maxPrice")
    Page<Extractos> findByPrice250mlBetween(@Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price500ml BETWEEN :minPrice AND :maxPrice")
    Page<Extractos> findByPrice500mlBetween(@Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price1000ml BETWEEN :minPrice AND :maxPrice")
    Page<Extractos> findByPrice1000mlBetween(@Param("minPrice") BigDecimal minPrice,
                                             @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price22ml = :exactPrice")
    Page<Extractos> findByPrice22ml(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price60ml = :exactPrice")
    Page<Extractos> findByPrice60ml(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price125ml = :exactPrice")
    Page<Extractos> findByPrice125ml(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price250ml = :exactPrice")
    Page<Extractos> findByPrice250ml(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price500ml = :exactPrice")
    Page<Extractos> findByPrice500ml(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.price1000ml = :exactPrice")
    Page<Extractos> findByPrice1000ml(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);
}
