package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Jar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
