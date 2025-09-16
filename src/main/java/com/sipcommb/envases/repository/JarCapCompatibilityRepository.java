package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.JarCapCompatibility;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JarCapCompatibilityRepository extends JpaRepository<JarCapCompatibility, Long> {

    @Query("SELECT jcc.cap FROM JarCapCompatibility jcc WHERE jcc.jar.id = :jarId")
    Optional<List<Cap>> findByJarId(@Param("jarId") Long jarId);

    @Query("SELECT jcc FROM JarCapCompatibility jcc WHERE jcc.jar.id = :jarId AND jcc.cap.id = :capId")
    Optional<JarCapCompatibility> findByJarAndCap(@Param("jarId") Long jarId, @Param("capId") Long capId);

    @Query("SELECT jcc.cap FROM JarCapCompatibility jcc WHERE jcc.jar.id = :jarId AND jcc.isCompatible = :isCompatible")
    Optional<Page<Cap>> findByJarIdAndIsCompatible(@Param("jarId") Long jarId, @Param("isCompatible") boolean isCompatible, Pageable pageable);


        @Query("SELECT jcc.cap FROM JarCapCompatibility jcc WHERE jcc.jar.id = :jarId AND jcc.isCompatible = :isCompatible")
    Optional<List<Cap>> findByJarIdAndIsCompatibleList(@Param("jarId") Long jarId, @Param("isCompatible") boolean isCompatible);
}


