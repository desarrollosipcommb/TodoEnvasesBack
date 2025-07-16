package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.JarType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JarTypeRepository extends JpaRepository<JarType, Long> {

    Optional<JarType> getTypeByName(String name);

    Optional<JarType> getTypeByDiameter(String diameter);



}
