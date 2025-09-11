package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Sale;
import java.math.BigDecimal;
import java.util.List;

public class SaleDTO {

    private String clientName;

    private String clientEmail;

    private String clientPhone;

    private BigDecimal totalPrice;

    private List<SaleItemDTO> saleItems;

    public SaleDTO() {
    }

    public SaleDTO(String clientName, String clientEmail, String clientPhone, BigDecimal totalPrice, List<SaleItemDTO> saleItems) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.totalPrice = totalPrice;
        this.saleItems = saleItems;
    }

    public SaleDTO(Sale sale, List<SaleItemDTO> saleItems) {
        this.clientName = sale.getClientName();
        this.clientEmail = sale.getClientEmail();
        this.clientPhone = sale.getClientPhone();
        this.totalPrice = sale.getTotalAmount();
        this.saleItems = saleItems;
    }

    // Getters and Setters

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

    
}
