package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Sale;
import java.math.BigDecimal;
import java.util.List;

public class SaleDTO {

    private Long id;

    private String sellerName;

    private String clientName;

    private String clientPhone;

    private String clientAddress;

    private BigDecimal totalPrice;

    private String saleDate;

    private List<SaleItemDTO> saleItems;

    private List<BodegaSaleDTO> bodegaSales;

    private String type;

    private String notes;

    private boolean active;

    public SaleDTO() {
    }

    public SaleDTO(String clientName, String clientPhone, BigDecimal totalPrice, List<SaleItemDTO> saleItems, String type, String notes, Long id) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.totalPrice = totalPrice;
        this.saleItems = saleItems;
        this.id = id;
        this.type = type;
        this.notes = notes;
    }

    public SaleDTO(Sale sale, List<SaleItemDTO> saleItems, List<BodegaSaleDTO> bodegaSales) {
        this.id = sale.getId();
        this.clientName = sale.getClient().getName();
        this.clientPhone = sale.getClient().getPhone();
        this.clientAddress = sale.getClient().getAddress();
        this.totalPrice = sale.getTotalAmount();
        this.saleDate = sale.getSaleDate().toString();
        this.sellerName = sale.getSeller() != null ? sale.getSeller().getFirstName() + " " + sale.getSeller().getLastName() : "N/A";
        this.type = sale.getType().name();
        this.notes = sale.getNotes();
        this.saleItems = saleItems;
        this.bodegaSales = bodegaSales;
        this.active = sale.isActive();
        
    }

    // Getters and Setters

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<SaleItemDTO> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItemDTO> saleItems) {
        this.saleItems = saleItems;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<BodegaSaleDTO> getBodegaSales() {
        return bodegaSales;
    }

    public void setBodegaSales(List<BodegaSaleDTO> bodegaSales) {
        this.bodegaSales = bodegaSales;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
