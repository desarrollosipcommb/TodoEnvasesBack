package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sipcommb.envases.entity.Extractos;

public interface ExtractosRepository extends JpaRepository<Extractos, Integer> {
    
    Optional<Extractos> findByName(String name);

    List<Extractos> findAllByActiveTrue();

    List<Extractos> findAllByActiveFalse();
    
}
