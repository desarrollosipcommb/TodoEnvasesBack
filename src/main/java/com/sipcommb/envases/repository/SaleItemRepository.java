package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    
    
}
