package com.sipcommb.envases.repository;


import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sipcommb.envases.entity.Combo;

public interface ComboRepository extends JpaRepository<Combo, Long> {

    @Query("SELECT c FROM Combo c WHERE c.name = :name AND c.active = 1")
    Optional<Combo> findByName(@Param("name") String name);   
    
    @Query("SELECT c FROM Combo c WHERE c.active = 1")
    List<Combo> findAllActiveCombos();

    @Query("SELECT c FROM Combo c WHERE c.active = 1")
    Page<Combo> findAllActiveCombos(Pageable pageable);

    @Query("SELECT c FROM Combo c WHERE c.active = 0")
    List<Combo> findAllInactiveCombos();

    @Query("SELECT c FROM Combo c WHERE c.active = 0")
    Page<Combo> findAllInactiveCombos(Pageable pageable);

    @Query("SELECT c FROM Combo c WHERE c.jar.id = :jarId AND c.cap.id = :capId AND c.active = 1")
    Optional<Combo> findByJarAndCap(@Param("jarId") Long jarId, @Param("capId") Long capId);

    @Query("SELECT c FROM Combo c WHERE c.name LIKE %:name% AND c.active = 1")
    Page<Combo> findByNameContaining(@Param("name") String name, Pageable pageable);

    Page<Combo> findAll(Pageable pageable);

    @Query("SELECT c FROM Combo c WHERE c.active = 1 AND c.cienPrice = :exactPrice")
    Page<Combo> findByCienPrice(@Param("exactPrice") Double exactPrice, Pageable pageable);

    @Query("SELECT c FROM Combo c WHERE c.active = 1 AND c.cienPrice BETWEEN :minPrice AND :maxPrice")
    Page<Combo> findByCienPriceBetween(@Param("minPrice") Double minPrice,
                                       @Param("maxPrice") Double maxPrice, Pageable pageable);

    @Query("SELECT c FROM Combo c WHERE c.active = 1 AND c.docenaPrice = :exactPrice")
    Page<Combo> findByDocenaPrice(@Param("exactPrice") Double exactPrice, Pageable pageable);

    @Query("SELECT c FROM Combo c WHERE c.active = 1 AND c.docenaPrice BETWEEN :minPrice AND :maxPrice")
    Page<Combo> findByDocenaPriceBetween(@Param("minPrice") Double minPrice,
                                       @Param("maxPrice") Double maxPrice, Pageable pageable);

    @Query("SELECT c FROM Combo c WHERE c.active = 1 AND c.unitPrice = :exactPrice")
    Page<Combo> findByUnidadPrice(@Param("exactPrice") Double exactPrice,
                                   Pageable pageable);

    @Query("SELECT c FROM Combo c WHERE c.active = 1 AND c.unitPrice BETWEEN :minPrice AND :maxPrice")
    Page<Combo> findByUnidadPriceBetween(@Param("minPrice") Double minPrice,
                                       @Param("maxPrice") Double maxPrice, Pageable pageable);


}
