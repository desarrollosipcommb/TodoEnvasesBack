package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Cap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CapRepository extends JpaRepository<Cap, Long> {

    boolean existsByName(String name);
    
    @Query("SELECT c FROM Cap c WHERE c.name = :name and c.jarType.diameter = :diameter and c.color = :color")
    Optional<Cap> findByNameAndDiameterAndColor(@Param("name") String name,
                                                @Param("diameter") String diameter,
                                                @Param("color") String color);

    @Query("SELECT c FROM Cap c WHERE c.name LIKE %:name% AND c.jarType.diameter = :diameter")
    Optional<List<Cap>> getFromNameAndDiameter(String name, String diameter);

    @Query("SELECT c FROM Cap c WHERE c.jarType.diameter = :diameter")
    Optional<Page<Cap>> getFromCapDiameter(@Param("diameter") String diameter, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.jarType.diameter = :diameter")
    Optional<List<Cap>> getFromCapDiameter(@Param("diameter") String diameter);

    @Query("SELECT c FROM Cap c WHERE c.name LIKE %:name%")
    Optional<Page<Cap>> getFromNameLike(@Param("name") String name, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.color LIKE %:color%")
    Optional<Page<Cap>> getFromColor(@Param("color") String color, Pageable pageable);

    @Query("SELECT c FROM Cap c WHERE c.isActive = 1")
    List<Cap> findAllByIsActive();

    @Query("SELECT c FROM Cap c WHERE c.isActive = 0")
    List<Cap> findAllByIsActiveFalse();

    Page<Cap> findAll(Pageable pageable);

    Page<Cap> findAllByIsActiveTrue(Pageable pageable);

    Page<Cap> findAllByIsActiveFalse(Pageable pageable);

}
