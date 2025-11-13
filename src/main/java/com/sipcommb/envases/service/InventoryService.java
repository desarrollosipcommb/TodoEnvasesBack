package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.TransactionResponseDTO;
import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Transaction;
import com.sipcommb.envases.entity.TransactionType;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.InventoryRepository;
import com.sipcommb.envases.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase().trim());
        TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType.toLowerCase().trim());

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

    public void newSaleItem(Long id, String itemType, int quantity, String transactionType, int userId, String notes,
            int saleId) {
        // Convert strings to enums
        ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase().trim());
        TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType.toLowerCase().trim());

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

    public Page<TransactionResponseDTO> getAll(Pageable pageable) {
        Page<Transaction> transactions = inventoryRepository.findAll(pageable);
        List<TransactionResponseDTO> responseDTOs = getResponseDTOs(transactions.getContent());
        return new PageImpl<>(responseDTOs, pageable, transactions.getTotalElements());
    }

    public Page<TransactionResponseDTO> getByItemType(Pageable pageable, String itemType) {
        try {
            ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase());
            Page<Transaction> transactions = inventoryRepository.findByItemType(itemTypeEnum, pageable);
            List<TransactionResponseDTO> responseDTOs = getResponseDTOs(transactions.getContent());
            return new PageImpl<>(responseDTOs, pageable, transactions.getTotalElements());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de item no válido: " + itemType);
        }

    }

    public Page<TransactionResponseDTO> getByTransactionType(Pageable pageable, String transactionType) {
        TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType);
        Page<Transaction> transactions = inventoryRepository.findByTransactionType(transactionTypeEnum, pageable);
        return new PageImpl<>(getResponseDTOs(transactions.getContent()), pageable, transactions.getTotalElements());
    }

    // TODO posiblemente cambiar de email a username
    public Page<TransactionResponseDTO> getByEmail(Pageable pageable, String email) {
        Optional<User> userOPT = userRepository.findByEmail(email);
        if (!userOPT.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado: " + email);
        }
        int userId = userOPT.get().getId().intValue();
        Page<Transaction> transactions = inventoryRepository.findByUser(userId, pageable);
        return transactions.map(tx -> new TransactionResponseDTO(tx,
                userOPT.get().getFirstName() + " " + userOPT.get().getLastName(), userOPT.get().getEmail()));
    }

    private List<TransactionResponseDTO> getResponseDTOs(List<Transaction> transactions) {
        List<TransactionResponseDTO> response = new ArrayList<>();
        for (Transaction tx : transactions) {
            Optional<User> userOPT = userRepository.findById(tx.getPerformedBy().longValue());
            if (userOPT.isPresent()) {
                User user = userOPT.get();
                response.add(new TransactionResponseDTO(tx, user.getFirstName() + " " + user.getLastName(),
                        user.getEmail()));
            }
        }
        return response;
    }

    public Page<TransactionResponseDTO> getByUsername(Pageable pageable, String username, String itemType,
            String transactionType) {

        Optional<List<User>> users = userRepository.findLikeUsername(username);
        if (!users.isPresent()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<Transaction> allTransactions = filterTransactions(users.get(), itemType, transactionType);

        // Pagina manualmente la lista
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allTransactions.size());
        List<Transaction> pagedList = allTransactions.subList(start, end);

        List<TransactionResponseDTO> responseDTOs = getResponseDTOs(pagedList);
        return new PageImpl<>(responseDTOs, pageable, allTransactions.size());
    }

    private List<Transaction> filterTransactions(List<User> users, String itemType, String transactionType) {
        List<Transaction> filtered = new ArrayList<>();

        for (User user : users) {
            int userId = user.getId().intValue();

            if (!itemType.isEmpty() && !transactionType.isEmpty()) {
                ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase().trim());
                TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType.toLowerCase().trim());
                filtered.addAll(inventoryRepository.findByUserAndItemTypeAndTransactionType(userId, itemTypeEnum,
                        transactionTypeEnum));
            } else if (!itemType.isEmpty()) {
                ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase().trim());
                filtered.addAll(inventoryRepository.findByUserAndItemType(userId, itemTypeEnum));
            } else if (!transactionType.isEmpty()) {
                TransactionType transactionTypeEnum = TransactionType.valueOf(transactionType.toLowerCase().trim());
                filtered.addAll(inventoryRepository.findByUserAndTransactionType(userId, transactionTypeEnum));
            } else {
                filtered.addAll(inventoryRepository.findByUser(userId));
            }

        }

        return filtered;
    }
}
