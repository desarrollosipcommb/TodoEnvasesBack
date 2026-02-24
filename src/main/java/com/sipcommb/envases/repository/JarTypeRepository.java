package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.JarType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JarTypeRepository extends JpaRepository<JarType, Long> {

    Optional<JarType> getTypeByName(String name);

    Optional<JarType> getTypeByDiameter(String diameter);

    @Query("SELECT j FROM JarType j WHERE LOWER(j.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<JarType> findLikeName(@Param("name") String name, Pageable pageable);





}
