package com.sipcommb.envases.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Transaction;
import com.sipcommb.envases.entity.TransactionType;

@Repository
public interface InventoryRepository extends JpaRepository<Transaction, Long>{

    

}
