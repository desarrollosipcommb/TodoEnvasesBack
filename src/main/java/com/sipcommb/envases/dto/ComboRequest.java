package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Combo;

public class ComboRequest {
    
    private String name;

    private String jarName;

    private String capName;

    private String diameter;

    private Double unitPrice;

    private Double docenaPrice; // Price for a dozen combos

    private Double cienPrice; // Price for a hundred combos

    private Double pacaPrice; // Price for a pack of combos

    private String description;

    public ComboRequest() {}
    
    public ComboRequest(String name, String jarName, String capName, String diameter, String
            color, Integer quantity, Double unitPrice, Double docenaPrice, Double cienPrice, String description) {
        this.name = name;
        this.jarName = jarName;
        this.capName = capName;
        this.diameter = diameter;
        this.unitPrice = unitPrice;
        this.docenaPrice = docenaPrice;
        this.cienPrice = cienPrice;
        this.description = description;
    }

    public ComboRequest(Combo combo){
        this.name = combo.getName();
        this.jarName = combo.getJar().getName();
        this.capName = combo.getCap().getName();
        this.diameter = combo.getJar().getJarType().getDiameter();
        this.unitPrice = combo.getUnitPrice();
        this.docenaPrice = combo.getDocenaPrice();
        this.cienPrice = combo.getCienPrice();
        this.description = combo.getDescription();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getCapName() {
        return capName;
    }

    public void setCapName(String capName) {
        this.capName = capName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPacaPrice() {
        return pacaPrice;
    }

    public void setPacaPrice(Double pacaPrice) {
        this.pacaPrice = pacaPrice;
    }

}
