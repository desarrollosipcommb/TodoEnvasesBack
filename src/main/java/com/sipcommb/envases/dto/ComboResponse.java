package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Combo;

public class ComboResponse {
    
    private String name;
    private String jarName;
    private String capName;
    private String diameter;
    private String color;
    private Double unitPrice;
    private Double docenaPrice; // Price for a dozen combos
    private Double cienPrice; // Price for a hundred combos
    private String description;
    private Integer cap_quantity; // Quantity of caps in the combo
    private Integer jar_quantity; // Quantity of jars in the combo

    public ComboResponse() {}

    public ComboResponse(String name, String jarName, String capName, String diameter, String color,
                         Double unitPrice, Double docenaPrice, Double cienPrice, String description,
                         Integer cap_quantity, Integer jar_quantity) {
        this.name = name;
        this.jarName = jarName;
        this.capName = capName;
        this.diameter = diameter;
        this.color = color;
        this.unitPrice = unitPrice;
        this.docenaPrice = docenaPrice;
        this.cienPrice = cienPrice;
        this.description = description;
        this.cap_quantity = cap_quantity;
        this.jar_quantity = jar_quantity;
    
    }

    public ComboResponse(Combo combo){
        this.name = combo.getName();
        this.jarName = combo.getJar().getName();
        this.capName = combo.getCap().getName();
        this.diameter = combo.getJar().getJarType().getDiameter();
        this.color = combo.getCap().getColor();
        this.unitPrice = combo.getUnitPrice();
        this.docenaPrice = combo.getDocenaPrice();
        this.cienPrice = combo.getCienPrice();
        this.description = combo.getDescription();
        this.cap_quantity = combo.getCap().getQuantity();
        this.jar_quantity = combo.getJar().getQuantity();
    }

    // Getters and Setters

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCap_quantity() {
        return cap_quantity;
    }

    public void setCap_quantity(Integer cap_quantity) {
        this.cap_quantity = cap_quantity;
    }

    public Integer getJar_quantity() {
        return jar_quantity;
    }

    public void setJar_quantity(Integer jar_quantity) {
        this.jar_quantity = jar_quantity;
    }

}
