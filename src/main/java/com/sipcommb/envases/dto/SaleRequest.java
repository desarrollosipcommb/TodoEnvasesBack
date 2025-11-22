package com.sipcommb.envases.dto;

import java.util.List;

public class SaleRequest {
    
    private String clientDocument;

    private String description;

    private String paymentMethod;

    private String saleDate;

    private String type;

    private List<SaleItemRequest> items;

    public SaleRequest() {
    }
    public SaleRequest(String clientDocument, String description, String paymentMethod, String saleDate, List<SaleItemRequest> items, String type) {
        this.clientDocument = clientDocument;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.saleDate = saleDate;
        this.items = items;
        this.type = type;
    }

    public String getClientDocument() {
        return clientDocument;
    }

    public void setClientDocument(String clientDocument) {
        this.clientDocument = clientDocument;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<SaleItemRequest> getItems() {
        return items;
    }

    public void setItems(List<SaleItemRequest> items) {
        this.items = items;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
