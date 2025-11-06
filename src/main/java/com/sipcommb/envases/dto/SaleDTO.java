package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Sale;
import java.math.BigDecimal;
import java.util.List;

public class SaleDTO {

    private String sellerName;

    private String clientName;

    private String clientPhone;

    private BigDecimal totalPrice;

    private String saleDate;

    private List<SaleItemDTO> saleItems;

    public SaleDTO() {
    }

    public SaleDTO(String clientName, String clientPhone, BigDecimal totalPrice, List<SaleItemDTO> saleItems) {
        this.clientName = clientName;
        this.clientPhone = clientPhone;
        this.totalPrice = totalPrice;
        this.saleItems = saleItems;
    }

    public SaleDTO(Sale sale, List<SaleItemDTO> saleItems) {
        this.clientName = sale.getClient().getName();
        this.clientPhone = sale.getClient().getPhone();
        this.totalPrice = sale.getTotalAmount();
        this.saleDate = sale.getSaleDate().toString();
        this.sellerName = sale.getSeller() != null ? sale.getSeller().getFirstName() + " " + sale.getSeller().getLastName() : "N/A";
        this.saleItems = saleItems;
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

}
