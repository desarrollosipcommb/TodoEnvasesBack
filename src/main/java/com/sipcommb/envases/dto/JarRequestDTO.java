package com.sipcommb.envases.dto;

import java.util.List;

import javax.validation.constraints.NotBlank;

public class JarRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    private String diameter;

    private List<BodegaDTO> quantity;

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

    public JarRequestDTO(String name, String description, String diameter, List<BodegaDTO> quantity, Double unitPrice, Double docenaPrice, Double cienPrice, Double pacaPrice, Integer unitsInPaca, String[] compatibleCaps, String[] unCompatibleCaps) {
        this.name = name;
        this.description = description;
        this.diameter = diameter;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.docenaPrice = docenaPrice;
        this.cienPrice = cienPrice;
        this.pacaPrice = pacaPrice;
        this.unitsInPaca = unitsInPaca;
        this.compatibleCaps = compatibleCaps;
        this.unCompatibleCaps = unCompatibleCaps;
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

    public List<BodegaDTO> getBodega() {
        return quantity;
    }

    public void setBodega(List<BodegaDTO> quantity) {
        this.quantity = quantity;
    }

}
