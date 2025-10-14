package com.sipcommb.envases.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sipcommb.envases.entity.Combo;

public class ComboResponse {

    private String name;
    private String jarName;
    private List<CapDTO> caps;
    private String diameter;
    private String color;
    private Double unitPrice;
    private Double docenaPrice; // Price for a dozen combos
    private Double cienPrice; // Price for a hundred combos
    private Double pacaPrice; // Price for a pack of combos
    private Integer unitsInPaca; // Number of combos in a pack
    private String description;
    private Integer jar_quantity; // Quantity of jars in the combo
    private Boolean active;

    public ComboResponse() {
    }

    public ComboResponse(String name, String jarName, List<CapDTO> caps, String diameter, String color,
            Double unitPrice, Double docenaPrice, Double cienPrice, String description,
            Integer cap_quantity, Integer jar_quantity) {
        this.name = name;
        this.jarName = jarName;
        this.caps = caps;
        this.diameter = diameter;
        this.color = color;
        this.unitPrice = unitPrice;
        this.docenaPrice = docenaPrice;
        this.cienPrice = cienPrice;
        this.description = description;
        this.jar_quantity = jar_quantity;

    }

    public ComboResponse(Combo combo) {
        this.name = combo.getName();
        this.jarName = combo.getJar().getName();
        this.caps = combo.getCaps().stream()
                .map(cap -> new CapDTO(cap.getCap()))
                .collect(Collectors.toList());
        this.diameter = combo.getJar().getJarType().getDiameter();
        this.unitPrice = combo.getUnitPrice();
        this.docenaPrice = combo.getDocenaPrice();
        this.cienPrice = combo.getCienPrice();
        this.pacaPrice = combo.getPacaPrice();
        this.unitsInPaca = combo.getUnitsInPaca();
        this.description = combo.getDescription();
        this.jar_quantity = combo.getJar().getQuantity();
        this.active = combo.getActive();
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

    public List<CapDTO> getCaps() {
        return caps;
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

    public Integer getJar_quantity() {
        return jar_quantity;
    }

    public void setJar_quantity(Integer jar_quantity) {
        this.jar_quantity = jar_quantity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

}
