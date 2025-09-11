package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Cap;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CapRepository extends JpaRepository<Cap, Long> {

    boolean existsByName(String name);
    
    @Query("SELECT c FROM Cap c WHERE c.name = :name and c.jarType.diameter = :diameter and c.color = :color AND c.isActive = 1")
    Optional<Cap> findByNameAndDiameterAndColor(@Param("name") String name,
                                                @Param("diameter") String diameter,
                                                @Param("color") String color);
  @Query("SELECT c FROM Cap c WHERE c.name = :name and c.jarType.diameter = :diameter and c.color = :color")
  Optional<Cap> findByNameAndDiameterAndColor2(@Param("name") String name,
                                              @Param("diameter") String diameter,
                                              @Param("color") String color);

    @Query("SELECT c FROM Cap c WHERE c.name LIKE %:name% AND c.jarType.diameter = :diameter AND c.isActive = 1")
    Optional<List<Cap>> getFromNameAndDiameter(String name, String diameter);

    @Query("SELECT c FROM Cap c WHERE c.jarType.diameter = :diameter AND c.isActive = 1")
    Optional<Page<Cap>> getFromCapDiameter(@Param("diameter") String diameter, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.jarType.diameter = :diameter AND c.isActive = 1")
    Optional<List<Cap>> getFromCapDiameter(@Param("diameter") String diameter);

    @Query("SELECT c FROM Cap c WHERE c.name LIKE %:name% AND c.isActive = 1")
    Optional<Page<Cap>> getFromNameLike(@Param("name") String name, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.color LIKE %:color% AND c.isActive = 1")
    Optional<Page<Cap>> getFromColor(@Param("color") String color, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1")
    List<Cap> findAllByIsActive();

    @Query("SELECT c FROM Cap c WHERE c.isActive = 0")
    List<Cap> findAllByIsActiveFalse();

    Page<Cap> findAll(Pageable pageable);

    Page<Cap> findAllByIsActiveTrue(Pageable pageable);

    Page<Cap> findAllByIsActiveFalse(Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1 AND c.cienPrice = :exactPrice")
    Page<Cap> findByCienPrice(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1 AND c.cienPrice BETWEEN :minPrice AND :maxPrice")
    Page<Cap> findByCienPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1 AND c.docenaPrice = :exactPrice")
    Page<Cap> findByDocenaPrice(@Param("exactPrice") BigDecimal exactPrice, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1 AND c.docenaPrice BETWEEN :minPrice AND :maxPrice")
    Page<Cap> findByDocenaPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1 AND c.unitPrice = :exactPrice")
    Page<Cap> findByUnidadPrice(@Param("exactPrice") BigDecimal exactPrice,
                                Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1 AND c.unitPrice BETWEEN :minPrice AND :maxPrice")
    Page<Cap> findByUnidadPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                       @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1 AND c.pacaPrice = :exactPrice")
    Page<Cap> findByPacaPrice(@Param("exactPrice") BigDecimal exactPrice,
                              Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1 AND c.pacaPrice BETWEEN :minPrice AND :maxPrice")
    Page<Cap> findByPacaPriceBetween(@Param("minPrice") BigDecimal minPrice,
                                     @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

  @Query("SELECT c FROM Cap c " +
      "JOIN c.jarType jt "+
      "WHERE (:name IS NULL OR c.name LIKE %:name%) " +
      "AND (:color IS NULL OR c.color LIKE %:color%) " +
      "AND (:diameter IS NULL OR jt.diameter LIKE %:diameter%) " )
  Page<Cap> getFromNameLikeAndColorAndDiameter(@Param("name") String name,@Param("color") String color,
                                                   @Param("diameter") String diameter, Pageable pageable);
    
}

