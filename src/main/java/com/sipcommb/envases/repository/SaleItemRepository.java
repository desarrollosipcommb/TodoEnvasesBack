package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Cap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    
    
}
