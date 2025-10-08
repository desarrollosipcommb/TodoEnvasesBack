package com.sipcommb.envases.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.CapColor;

@Repository
public interface CapColorRepository extends JpaRepository<CapColor, Long> {

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap = :cap AND cc.color = :color")
    Optional<CapColor> findByCapAndColor(@Param("cap") Cap cap, @Param("color") String color);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.name = :name AND cc.is_active = 1 AND cc.cap.jarType.diameter = :diameter AND cc.color = :color")
    Optional<CapColor> findByCapColor(@Param("name") String name, @Param("diameter") String diameter, @Param("color") String color);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.name = :name AND cc.color = :color")
    Optional<CapColor> findByNameAndColor(@Param("name") String name, @Param("color") String color);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.name = :name AND cc.color = :color AND cc.is_active = :isActive")
    Optional<CapColor> findByNameAndColorAndActive(@Param("name") String name, @Param("color") String color, @Param("isActive") boolean isActive);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap = :cap")
    Page<CapColor> findAllByCap(@Param("cap") Cap cap, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap = :cap AND cc.is_active = 1")
    Page<CapColor> findAllByCapActive(@Param("cap") Cap cap, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap = :cap AND cc.color = :color")
    Page<CapColor> findAllByCapAndColor(@Param("cap") Cap cap, @Param("color") String color, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.cap = :cap AND cc.color = :color")
    Page<CapColor> findAllByCapActiveAndColor(@Param("cap") Cap cap, @Param("color") String color, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.cien_price = :exactPrice")
    Page<CapColor> findByCienPrice(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.cien_price BETWEEN :minPrice AND :maxPrice")
    Page<CapColor> findByCienPriceBetween(@Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.docena_price = :exactPrice")
    Page<CapColor> findByDocenaPrice(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.docena_price BETWEEN :minPrice AND :maxPrice")
    Page<CapColor> findByDocenaPriceBetween(@Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.unit_price = :exactPrice")
    Page<CapColor> findByUnidadPrice(@Param("exactPrice") BigDecimal exactPrice,
            Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.unit_price BETWEEN :minPrice AND :maxPrice")
    Page<CapColor> findByUnidadPriceBetween(@Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.paca_price = :exactPrice")
    Page<CapColor> findByPacaPrice(@Param("exactPrice") BigDecimal exactPrice,
            Pageable pageable);

    @Query("SELECT cc FROM CapColor cc WHERE cc.cap.isActive = 1 AND cc.paca_price BETWEEN :minPrice AND :maxPrice")
    Page<CapColor> findByPacaPriceBetween(@Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

}
