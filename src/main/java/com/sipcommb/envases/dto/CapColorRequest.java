package com.sipcommb.envases.dto;

import java.util.List;

public class CapColorRequest {

    private String name;
    private String diameter;
    private String color;
    private Double unitPrice;
    private Double docenaPrice; // Price for a dozen jars
    private Double cienPrice; // Price for a hundred jars
    private Double pacaPrice;
    private Integer unitsInPaca;
    private List<BodegaDTO> quantity;

    public CapColorRequest() {
    }

    public CapColorRequest(String name, String diameter, String color,
            Double unitPrice, Double docenaPrice, Double cienPrice, Double pacaPrice, Integer unitsInPaca, List<BodegaDTO> quantity) {
        this.name = name;
        this.diameter = diameter;
        this.color = color;
        this.unitPrice = unitPrice;
        this.docenaPrice = docenaPrice;
        this.cienPrice = cienPrice;
        this.pacaPrice = pacaPrice;
        this.unitsInPaca = unitsInPaca;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public List<BodegaDTO> getBodega() {
        return quantity;
    }

    public void setBodega(List<BodegaDTO> quantity) {
        this.quantity = quantity;
    }

}
