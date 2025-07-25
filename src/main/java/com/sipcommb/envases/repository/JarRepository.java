package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.Jar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface JarRepository extends JpaRepository<Jar, Long> {

    Optional<Jar> getByName(String name);

    @Query("SELECT j FROM Jar j WHERE j.name LIKE %:name%")
    Optional<Page<Jar>> getFromNameLike(String name, Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.jarType.diameter = :diameter")
    Optional<List<Jar>> getFromDiameter(@Param("diameter") String diameter);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1")
    Optional<List<Jar>> getAllActiveJars();

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1")
    Optional<Page<Jar>> getAllActiveJars(Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 0")
    Optional<List<Jar>> getAllInactiveJars();

    @Query("SELECT j FROM Jar j WHERE j.isActive = 0")
    Optional<Page<Jar>> getAllInactiveJars(Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1 AND j.cienPrice = :exactPrice")
    Page<Jar> findByCienPrice(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1 AND j.cienPrice BETWEEN :minPrice AND :maxPrice")
    Page<Jar> findByCienPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1 AND j.docenaPrice = :exactPrice")
    Page<Jar> findByDocenaPrice(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1 AND j.docenaPrice BETWEEN :minPrice AND :maxPrice")
    Page<Jar> findByDocenaPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1 AND j.unitPrice = :exactPrice")
    Page<Jar> findByUnidadPrice(@Param("exactPrice") BigDecimal exactPrice,
                                Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1 AND j.unitPrice BETWEEN :minPrice AND :maxPrice")
    Page<Jar> findByUnidadPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1 AND j.pacaPrice = :exactPrice")
    Page<Jar> findByPacaPrice(@Param("exactPrice") BigDecimal exactPrice,
                              Pageable pageable);

    @Query("SELECT j FROM Jar j WHERE j.isActive = 1 AND j.pacaPrice BETWEEN :minPrice AND :maxPrice")
    Page<Jar> findByPacaPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);



}
