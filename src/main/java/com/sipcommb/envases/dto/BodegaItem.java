package com.sipcommb.envases.dto;

public class BodegaItem {

    private String itemName;

    private Integer quantity;

    public BodegaItem() {
    }

    public BodegaItem(String itemName, Integer quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
}
