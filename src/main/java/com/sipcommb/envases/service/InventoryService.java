package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.TransactionDTO;
import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Transaction;
import com.sipcommb.envases.entity.TransactionType;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.InventoryRepository;
import com.sipcommb.envases.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private UserRepository userRepository;

    public void newItem(Long id, String itemType, int quantity, String transactionType, int userId, String notes) {
        // Convert strings to enums
        ItemType itemTypeEnum = ItemType.valueOf(itemType);
        TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType);
    
        // Create and populate Transaction entity
        Transaction tx = new Transaction();
        tx.setItemId(id.intValue());
        tx.setItemType(itemTypeEnum);
        tx.setQuantityChange(quantity);
        tx.setTransactionType(transactionTypeEnum);
        tx.setPerformedBy(userId);
        tx.setNotes(notes);
        tx.setTransactionDate(java.time.LocalDateTime.now());
    
        // Save using standard JPA save method
        inventoryRepository.save(tx);
    }

    public void newSaleItem(Long id, String itemType, int quantity, String transactionType, int userId, String notes, int saleId) {
        // Convert strings to enums
        ItemType itemTypeEnum = ItemType.valueOf(itemType);
        TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType);

        // Create and populate Transaction entity
        Transaction tx = new Transaction();
        tx.setItemId(id.intValue());
        tx.setItemType(itemTypeEnum);
        tx.setQuantityChange(quantity);
        tx.setTransactionType(transactionTypeEnum);
        tx.setPerformedBy(userId);
        tx.setNotes(notes);
        tx.setReferenceId(saleId);
        tx.setTransactionDate(java.time.LocalDateTime.now());

        // Save using standard JPA save method
        inventoryRepository.save(tx);
    }

    public List<TransactionDTO> getAll() {
        List<Transaction> transactions = inventoryRepository.findAll();
        return transactions.stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getByItemType(String itemType) {
        ItemType itemTypeEnum = ItemType.valueOf(itemType);
        List<Transaction> transactions = inventoryRepository.findByItemType(itemTypeEnum);
        return transactions.stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getByTransactionType(String transactionType) {
        TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType);
        List<Transaction> transactions = inventoryRepository.findByTransactionType(transactionTypeEnum);
        return transactions.stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }

    public List<TransactionDTO> getByUser(String email){
        Optional<User> userOPT = userRepository.findByEmail(email);
        if (!userOPT.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado: " + email);
        }
        int userId = userOPT.get().getId().intValue();
        List<Transaction> transactions = inventoryRepository.findByUser(userId);
        return transactions.stream()
                .map(TransactionDTO::new)
                .collect(Collectors.toList());
    }




}