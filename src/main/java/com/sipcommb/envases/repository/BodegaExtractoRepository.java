package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaExtractos;
import com.sipcommb.envases.entity.Extractos;

@Repository
public interface BodegaExtractoRepository extends JpaRepository<BodegaExtractos, Long> {

    Optional<BodegaExtractos> findByBodegaAndExtracto(Bodega bodega, Extractos extracto);
    
    @Query("SELECT be.bodega.name, be.extracto.name, be.quantity FROM BodegaExtractos be")
    List<Object[]> getGroupedByBodega();

    @Query("SELECT be FROM BodegaExtractos be WHERE be.bodega.name LIKE %:bodegaName% AND be.extracto.name LIKE %:itemName%")
    List<BodegaExtractos> findByBodegaNameContaining(@Param("bodegaName") String bodegaName, @Param("itemName") String itemName);

    @Query("SELECT be FROM BodegaExtractos be WHERE be.bodega.name LIKE %:bodegaName% AND be.extracto.name LIKE %:itemName%")
    Optional<BodegaExtractos> findBodegaItem(@Param("bodegaName") String bodegaName, @Param("itemName") String itemName);

}

