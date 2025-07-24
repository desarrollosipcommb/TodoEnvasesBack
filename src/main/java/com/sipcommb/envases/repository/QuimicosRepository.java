package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sipcommb.envases.entity.Quimicos;

public interface QuimicosRepository extends JpaRepository<Quimicos, Integer> {
    // Additional query methods can be defined here if needed
    
    @Query("SELECT q FROM Quimicos q WHERE q.name = :name")
    Optional<Quimicos> findByName(@Param("name") String name);

    List<Quimicos> findByActiveTrue();

    Page<Quimicos> findByActiveTrue(Pageable pageable);

    List<Quimicos> findByActiveFalse();

    Page<Quimicos> findByActiveFalse(Pageable pageable);

}
