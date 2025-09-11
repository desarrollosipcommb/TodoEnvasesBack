package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Transaction;
import com.sipcommb.envases.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface InventoryRepository extends JpaRepository<Transaction, Long>{

    Page<Transaction> findAll(Pageable pageable);

    Page<Transaction> findByItemType(ItemType itemType, Pageable pageable);

    Page<Transaction> findByTransactionType(TransactionType transactionType, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.performedBy = :userId")
    Page<Transaction> findByUser(@Param("userId") int userId, Pageable pageable);

}
