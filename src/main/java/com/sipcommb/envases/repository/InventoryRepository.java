package com.sipcommb.envases.repository;

import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Transaction;
import com.sipcommb.envases.entity.TransactionType;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;


@Repository
public interface InventoryRepository extends JpaRepository<Transaction, Long>{

    @NonNull Page<Transaction> findAll(@NonNull Pageable pageable);

    Page<Transaction> findByItemType(ItemType itemType, Pageable pageable);

    Page<Transaction> findByTransactionType(TransactionType transactionType, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.performedBy = :userId")
    Page<Transaction> findByUser(@Param("userId") int userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.performedBy = :userId")
    List<Transaction> findByUser(@Param("userId") int userId);

    @Query("SELECT t FROM Transaction t WHERE t.itemType = :itemType AND t.transactionType = :transactionType AND t.performedBy = :userId")
    List<Transaction> findByUserAndItemTypeAndTransactionType(@Param("userId") int userId, @Param("itemType") ItemType itemType, @Param("transactionType") TransactionType transactionType);


    @Query("SELECT t FROM Transaction t WHERE t.transactionType = :transactionType AND t.performedBy = :userId")
    List<Transaction> findByUserAndTransactionType(@Param("userId") int userId, @Param("transactionType") TransactionType transactionType);

    @Query("SELECT t FROM Transaction t WHERE t.itemType = :itemType AND t.performedBy = :userId")
    List<Transaction> findByUserAndItemType(@Param("userId") int userId, @Param("itemType") ItemType itemType);
}
