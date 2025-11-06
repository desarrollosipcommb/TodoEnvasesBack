package com.sipcommb.envases.dto;

public class SaleItemBodegaRequest {
    
    private String bodegaName;

    private Integer quantity;

    public SaleItemBodegaRequest() {
    }

    public SaleItemBodegaRequest(String bodegaName, Integer quantity) {
        this.bodegaName = bodegaName;
        this.quantity = quantity;
    }

    public String getBodegaName() {
        return bodegaName;
    }

    public void setBodegaName(String bodegaName) {
        this.bodegaName = bodegaName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
