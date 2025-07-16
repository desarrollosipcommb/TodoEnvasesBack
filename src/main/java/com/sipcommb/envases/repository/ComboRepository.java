package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sipcommb.envases.entity.Combo;

public interface ComboRepository extends JpaRepository<Combo, Long> {

    @Query("SELECT c FROM Combo c WHERE c.name = :name")
    Optional<Combo> findByName(@Param("name") String name);   
    
    @Query("SELECT c FROM Combo c WHERE c.active = 1")
    List<Combo> findAllActiveCombos();

    @Query("SELECT c FROM Combo c WHERE c.active = 0")
    List<Combo> findAllInactiveCombos();
}
