package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaJar;
import com.sipcommb.envases.entity.Jar;

@Repository
public interface BodegaJarRepository extends JpaRepository<BodegaJar, Long> {

    Optional<BodegaJar> findByBodegaAndJar(Bodega bodega, Jar jar);

    @Query("SELECT bj.bodega.name, bj.jar.name, bj.quantity FROM BodegaJar bj")
    List<Object[]> getGroupedByBodega();
}
