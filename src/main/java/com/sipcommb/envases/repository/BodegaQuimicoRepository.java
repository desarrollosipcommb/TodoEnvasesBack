package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaQuimicos;
import com.sipcommb.envases.entity.Quimicos;

@Repository
public interface BodegaQuimicoRepository extends JpaRepository<BodegaQuimicos, Long> {

    Optional<BodegaQuimicos> findByBodegaAndQuimico(Bodega bodega, Quimicos quimico);

    @Query("SELECT bq.bodega.name, bq.quimico.name, bq.quantity FROM BodegaQuimicos bq")
    List<Object[]> getGroupedByBodega();

    @Query("SELECT bq FROM BodegaQuimicos bq WHERE bq.bodega.name LIKE %:bodegaName% AND bq.quimico.name LIKE %:itemName%")
    List<BodegaQuimicos> findByBodegaNameContaining(@Param("bodegaName") String bodegaName, @Param("itemName") String itemName);

    @Query("SELECT bq FROM BodegaQuimicos bq WHERE bq.bodega.name LIKE %:bodegaName% AND bq.quimico.name LIKE %:itemName%")
    Optional<BodegaQuimicos> findBodegaItem(@Param("bodegaName") String bodegaName, @Param("itemName") String itemName);

}
