package com.sipcommb.envases.service;

import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Transaction;
import com.sipcommb.envases.entity.TransactionType;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.InventoryRepository;
import com.sipcommb.envases.repository.JarTypeRepository;
import com.sipcommb.envases.repository.SaleItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

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

}