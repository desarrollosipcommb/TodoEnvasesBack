package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaExtractos;
import com.sipcommb.envases.entity.Extractos;

@Repository
public interface BodegaExtractoRepository extends JpaRepository<BodegaExtractos, Long> {

    Optional<BodegaExtractos> findByBodegaAndExtracto(Bodega bodega, Extractos extracto);
    
    @Query("SELECT be.bodega.name, be.extracto.name, be.quantity FROM BodegaExtractos be")
    List<Object[]> getGroupedByBodega();
}
