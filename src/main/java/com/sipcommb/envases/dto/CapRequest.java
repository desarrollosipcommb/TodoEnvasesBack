package com.sipcommb.envases.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class CapRequest {


    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotBlank(message = "El color es obligatorio")
    private String color;

    @NotBlank(message = "El diámetro es obligatorio")
    private String diameter;

    @JsonSetter(nulls = Nulls.SKIP)
    private Integer quantity = 0;

    private Double unitPrice;

    private Double docenaPrice;

    private Double cienPrice;

    private Double pacaPrice;

    private Integer unitsInPaca;

    public CapRequest() {
    }

    public CapRequest(String name, String description, String color, String diameter, Integer quantity, Double unitPrice, Double docenaPrice, Double cienPrice, Double pacaPrice, Integer unitsInPaca) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.diameter = diameter;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.docenaPrice = docenaPrice;
        this.cienPrice = cienPrice;
        this.pacaPrice = pacaPrice;
        this.unitsInPaca = unitsInPaca;
    }

    // Getters and Setters

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public Double getDocenaPrice() {
        return docenaPrice;
    }

    public void setDocenaPrice(Double docenaPrice) {
        this.docenaPrice = docenaPrice;
    }

    public Double getCienPrice() {
        return cienPrice;
    }

    public void setCienPrice(Double cienPrice) {
        this.cienPrice = cienPrice;
    }

    public Double getPacaPrice() {
        return pacaPrice;
    }

    public void setPacaPrice(Double pacaPrice) {
        this.pacaPrice = pacaPrice;
    }

    public Integer getUnitsInPaca() {
        return unitsInPaca;
    }

    public void setUnitsInPaca(Integer unitsInPaca) {
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }


}
