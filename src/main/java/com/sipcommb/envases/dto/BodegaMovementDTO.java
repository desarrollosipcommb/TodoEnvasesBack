package com.sipcommb.envases.dto;

public class BodegaMovementDTO {
    private String bodegaFrom;

    private String bodegaTo;

    private Integer quantity;

    private String itemName;

    private String capColor;

    public BodegaMovementDTO() {
    }

    public String getBodegaFrom() {
        return bodegaFrom;
    }

    public void setBodegaFrom(String bodegaFrom) {
        this.bodegaFrom = bodegaFrom;
    }

    public String getBodegaTo() {
        return bodegaTo;
    }

    public void setBodegaTo(String bodegaTo) {
        this.bodegaTo = bodegaTo;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCapColor() {
        return capColor;
    }

    public void setCapColor(String capColor) {
        this.capColor = capColor;
    }


}
