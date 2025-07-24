package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sipcommb.envases.entity.Extractos;

public interface ExtractosRepository extends JpaRepository<Extractos, Integer> {
    
    Optional<Extractos> findByName(String name);

    List<Extractos> findAllByActiveTrue();

    Page<Extractos> findAllByActiveTrue(Pageable pageable);

    List<Extractos> findAllByActiveFalse();

    Page<Extractos> findAllByActiveFalse(Pageable pageable);

    @Query("SELECT e FROM Extractos e WHERE e.active = 1 AND e.name LIKE %:name%")
    Page<Extractos> findLikeName(@Param("name") String name, Pageable pageable);
}
