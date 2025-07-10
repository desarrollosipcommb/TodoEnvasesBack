package com.sipcommb.envases.dto;

import javax.validation.constraints.NotBlank;

public class CapRequest {


    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotBlank(message = "El diámetro es obligatorio")
    private String diameter;

    @NotBlank(message = "La cantidad es obligatoria")
    private int quantity;

    @NotBlank(message = "El precio unitario es obligatorio")
    private double unitPrice;

    private double docenaPrice;

    private double cienPrice;

    private double pacaPrice;


    private int unitsInPaca;

    // Getters and Setters

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
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

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }


}
