package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Transaction;
import java.time.LocalDateTime;


public class TransactionDTO {
    String itemType;

    String quantity;

    String transactionType;

    String notes;

    LocalDateTime transactionDate;

    public TransactionDTO() {
    }

    public TransactionDTO(Transaction transaction){
        this.itemType = transaction.getItemType().name();
        this.quantity = transaction.getQuantityChange().toString();
        this.transactionType = transaction.getTransactionType().name();
        this.notes = transaction.getNotes();
        this.transactionDate = transaction.getTransactionDate();
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    
    
}
