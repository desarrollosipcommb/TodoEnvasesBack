package com.sipcommb.envases.dto;

import java.math.BigDecimal;

import com.sipcommb.envases.entity.Cap;

public class CapDTO {

    //TOOD: añadir los cambios que se le hicieron a la tabla cap

    private String name;
    private String color;
    private int quantity;
    private BigDecimal unit_price;
    private String diameter;
    private String description;
    private double docenaPrice;
    private double cienPrice;
    private double pacaPrice;
    private int unitsInPaca;



    // Default constructor
    public CapDTO() {}

    // Parameterized constructor
    public CapDTO(Cap cap) {
        this.name = cap.getName();
        this.color = cap.getColor();
        this.quantity = cap.getQuantity();
        this.unit_price = cap.getUnitPrice();
        this.diameter = cap.getJarType() != null ? cap.getJarType().getDiameter() : "";
        this.description = cap.getDescription();
        this.docenaPrice = cap.getDocenaPrice().doubleValue();
        this.cienPrice = cap.getCienPrice().doubleValue();
        this.pacaPrice = cap.getPacaPrice().doubleValue();
        this.unitsInPaca = cap.getUnitsInPaca();


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

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDocenaPrice() {
        return docenaPrice;
    }

    public void setDocenaPrice(double docenaPrice) {
        this.docenaPrice = docenaPrice;
    }

    public double getCienPrice() {
        return cienPrice;
    }

    public void setCienPrice(double cienPrice) {
        this.cienPrice = cienPrice;
    }

    public double getPacaPrice() {
        return pacaPrice;
    }

    public void setPacaPrice(double pacaPrice) {
        this.pacaPrice = pacaPrice;
    }

    public int getUnitsInPaca() {
        return unitsInPaca;
    }

    public void setUnitsInPaca(int unitsInPaca) {
        this.unitsInPaca = unitsInPaca;
    }

    
}
