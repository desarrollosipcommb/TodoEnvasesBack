package com.sipcommb.envases.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.sipcommb.envases.entity.BodegaSale;

@Repository
public interface BodegaSaleRepository extends JpaRepository<BodegaSale, Long> {
    
    List<BodegaSale> findBySaleId(long saleId);
}
