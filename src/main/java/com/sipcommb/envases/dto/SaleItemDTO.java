package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.SaleItem;
import java.math.BigDecimal;

public class SaleItemDTO {

    private String name;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;

    public SaleItemDTO() {
    }

    public SaleItemDTO(String name, Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }

    public SaleItemDTO(String name, SaleItem saleItem) {
        this.name = name;
        this.quantity = saleItem.getQuantity();
        this.unitPrice = saleItem.getUnitPrice();
        this.subtotal = saleItem.getSubtotal();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }   
    
}