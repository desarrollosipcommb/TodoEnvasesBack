package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarCapCompatibility;

@Repository
public interface JarCapCompatibilityRepository extends JpaRepository<JarCapCompatibility, Long> {

    @Query("SELECT jcc.cap FROM JarCapCompatibility jcc WHERE jcc.jar.id = :jarId")
    Optional<List<Cap>> findByJarId(@Param("jarId") Long jarId);

    @Query("SELECT jcc FROM JarCapCompatibility jcc WHERE jcc.jar.id = :jarId AND jcc.cap.id = :capId")
    Optional<JarCapCompatibility> findByJarAndCap(@Param("jarId") Long jarId, @Param("capId") Long capId);

}
