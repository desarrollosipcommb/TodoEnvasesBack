package com.sipcommb.envases.dto;

import javax.validation.constraints.NotBlank;

public class JarRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    private String diameter;

    private Integer quantity;

    private Double unitPrice;

    private Double docenaPrice;

    private Double cienPrice;

    private Double pacaPrice;

    private Integer unitsInPaca;

    private String[] compatibleCaps;

    private String[] unCompatibleCaps;

    public JarRequestDTO() {
        // Default constructor
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

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
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

    public String[] getCompatibleCaps() {
        return compatibleCaps;
    }

    public void setCompatibleCaps(String[] compatibleCaps) {
        this.compatibleCaps = compatibleCaps;
    }

    public String[] getUnCompatibleCaps() {
        return unCompatibleCaps;
    }

    public void setUnCompatibleCaps(String[] unCompatibleCaps) {
        this.unCompatibleCaps = unCompatibleCaps;
    }


}
