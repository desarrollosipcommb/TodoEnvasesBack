package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Jar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JarRepository extends JpaRepository<Jar, Long> {

    Optional<String> getByName(String name);

    @Query("SELECT j FROM Jar j WHERE j.name LIKE %:name%")
    Optional<List<Jar>> getFromNameLike(String name);

    @Query("SELECT j FROM Jar j WHERE j.jarType.diameter = :diameter")
    Optional<List<Jar>> getFromDiameter(@Param("diameter") String diameter);

}
