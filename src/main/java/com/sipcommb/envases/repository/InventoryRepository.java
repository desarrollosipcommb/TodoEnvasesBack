package com.sipcommb.envases.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Transaction;


@Repository
public interface InventoryRepository extends JpaRepository<Transaction, Long>{

    

}
