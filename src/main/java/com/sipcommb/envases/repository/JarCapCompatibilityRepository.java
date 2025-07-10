package com.sipcommb.envases.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.JarCapCompatibility;

@Repository
public interface JarCapCompatibilityRepository extends JpaRepository<JarCapCompatibility, Long> {


    
}
