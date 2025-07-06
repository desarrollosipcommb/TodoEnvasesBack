package com.sipcommb.envases.dto;

import java.math.BigDecimal;

import com.sipcommb.envases.entity.Cap;

public class CapDTO {
    private String name;
    private String color;
    private int quantity;
    private BigDecimal unit_price;

    // Default constructor
    public CapDTO() {}

    // Parameterized constructor
    public CapDTO(Cap cap) {
        this.name = cap.getName();
        this.color = cap.getColor();
        this.quantity = cap.getQuantity();
        this.unit_price = cap.getUnitPrice();
    }

    // Getters and Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unit_price;
    }
    public void setUnitPrice(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }
}
