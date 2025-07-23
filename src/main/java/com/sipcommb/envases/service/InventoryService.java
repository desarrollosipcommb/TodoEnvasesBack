package com.sipcommb.envases.service;


import com.sipcommb.envases.dto.TransactionResponseDTO;
import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Transaction;
import com.sipcommb.envases.entity.TransactionType;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.InventoryRepository;
import com.sipcommb.envases.repository.UserRepository;

import java.util.ArrayList;
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

    public List<TransactionResponseDTO> getAll() {
        List<Transaction> transactions = inventoryRepository.findAll();
        return getResponseDTOs(transactions);
    }

    public List<TransactionResponseDTO> getByItemType(String itemType) {
        try{
            ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase());
            List<Transaction> transactions = inventoryRepository.findByItemType(itemTypeEnum);
            return getResponseDTOs(transactions);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de item no válido: " + itemType);
        }
       
    }

    public List<TransactionResponseDTO> getByTransactionType(String transactionType) {
        TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType);
        List<Transaction> transactions = inventoryRepository.findByTransactionType(transactionTypeEnum);
        return getResponseDTOs(transactions);
    }

    public List<TransactionResponseDTO> getByUser(String email){
        Optional<User> userOPT = userRepository.findByEmail(email);
        if (!userOPT.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado: " + email);
        }
        int userId = userOPT.get().getId().intValue();
        List<Transaction> transactions = inventoryRepository.findByUser(userId);
        return transactions.stream()
                .map(tx -> new TransactionResponseDTO(tx, userOPT.get().getFirstName() +" "+ userOPT.get().getLastName(), userOPT.get().getEmail()))
                .collect(Collectors.toList());
    }

    private List<TransactionResponseDTO> getResponseDTOs(List<Transaction> transactions) {
        List<TransactionResponseDTO> response = new ArrayList<>();
        for (Transaction tx : transactions) {
            Optional<User> userOPT = userRepository.findById(tx.getPerformedBy().longValue());
            if (userOPT.isPresent()) {
                User user = userOPT.get();
                response.add(new TransactionResponseDTO(tx, user.getFirstName() + " " + user.getLastName(), user.getEmail()));
            }
        }
        return response;
    }




}