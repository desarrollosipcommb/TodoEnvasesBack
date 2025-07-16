package com.sipcommb.envases.dto;

import java.util.List;

public class SaleRequest {
    
    private String clientName;

    private String clientEmail;

    private String clientPhone;

    private String descripion;

    private String paymentMethod;

    private String saleDate;

    private List<SaleItemRequest> items;

    public SaleRequest() {
    }
    public SaleRequest(String clientName, String clientEmail, String clientPhone, String descripion, String paymentMethod, List<SaleItemRequest> items) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.descripion = descripion;
        this.paymentMethod = paymentMethod;
        this.items = items;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getDescripion() {
        return descripion;
    }

    public void setDescripion(String descripion) {
        this.descripion = descripion;
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

}
