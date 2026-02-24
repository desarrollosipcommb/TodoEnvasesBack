package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Transaction;
import java.time.LocalDateTime;

public class TransactionResponseDTO {

    String itemType;

    String quantity;

    String transactionType;

    String notes;

    LocalDateTime transactionDate;

    String userName;

    String userEmail;

    public TransactionResponseDTO() {
    }

    public TransactionResponseDTO(Transaction transaction, String userName, String userEmail) {
        this.itemType = transaction.getItemType().name();
        this.quantity = transaction.getQuantityChange().toString();
        this.transactionType = transaction.getTransactionType().name();
        this.notes = transaction.getNotes();
        this.transactionDate = transaction.getTransactionDate();
        this.userName = userName;
        this.userEmail = userEmail;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
}
