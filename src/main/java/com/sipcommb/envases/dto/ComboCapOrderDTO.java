package com.sipcommb.envases.dto;

public class ComboCapOrderDTO {
    String capName;

    String color;

    Integer quantity;

    public ComboCapOrderDTO() {
    }

    public ComboCapOrderDTO(String capName, String color, Integer quantity) {
        this.capName = capName;
        this.color = color;
        this.quantity = quantity;
    }

    public String getCapName() {
        return capName;
    }

    public void setCapName(String capName) {
        this.capName = capName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
