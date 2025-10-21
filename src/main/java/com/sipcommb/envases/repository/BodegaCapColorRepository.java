package com.sipcommb.envases.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.BodegaCapColor;

@Repository
public interface BodegaCapColorRepository extends JpaRepository<BodegaCapColor, Long> {

    @Query("SELECT bc FROM BodegaCapColor bc WHERE bc.id = :bodegaId AND bc.capColor.id = :capColorId")
    Optional<BodegaCapColor> findByBodegaIdAndCapColorId(Long bodegaId, Long capColorId);

    @Query("SELECT bc.bodega.name, bc.capColor.cap.name, bc.capColor.color, bc.quantity FROM BodegaCapColor bc")
    List<Object[]> getGroupedByBodega();
}
