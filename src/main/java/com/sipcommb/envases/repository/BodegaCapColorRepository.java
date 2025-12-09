package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.BodegaCapColor;

@Repository
public interface BodegaCapColorRepository extends JpaRepository<BodegaCapColor, Long> {

    @Query("SELECT bc FROM BodegaCapColor bc WHERE bc.bodega.id = :bodegaId AND bc.capColor.id = :capColorId")
    Optional<BodegaCapColor> findByBodegaIdAndCapColorId(@Param("bodegaId") Long bodegaId, @Param("capColorId") Long capColorId);

    @Query("SELECT bc.bodega.name, bc.capColor.cap.name, bc.capColor.color, bc.quantity FROM BodegaCapColor bc")
    List<Object[]> getGroupedByBodega();

    @Query("SELECT bc FROM BodegaCapColor bc WHERE bc.bodega.name LIKE %:bodegaName% AND bc.capColor.cap.name LIKE %:itemName%")
    List<BodegaCapColor> findByBodegaNameContaining(@Param("bodegaName") String bodegaName, @Param("itemName") String itemName);

    @Query("SELECT bc FROM BodegaCapColor bc WHERE bc.bodega.name LIKE %:bodegaName% AND CONCAT(bc.capColor.cap.name, ' ', bc.capColor.color) LIKE %:itemName%")
    Optional<BodegaCapColor> findBodegaItem(@Param("bodegaName") String bodegaName, @Param("itemName") String itemName);

}
