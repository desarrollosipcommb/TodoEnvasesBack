package com.sipcommb.envases.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sipcommb.envases.entity.Transaction;


@Repository
public interface InventoryRepository extends JpaRepository<Transaction, Long>{

    List<Transaction> findByItemType(com.sipcommb.envases.entity.ItemType itemType);

    List<Transaction> findByTransactionType(com.sipcommb.envases.entity.TransactionType transactionType);

    @Query("SELECT t FROM Transaction t WHERE t.performedBy = :userId")
    List<Transaction> findByUser(@Param("userId") int userId);

}
