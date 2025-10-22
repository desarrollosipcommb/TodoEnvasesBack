package com.sipcommb.envases.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sipcommb.envases.entity.CapColor;

public class CapColorDTO {

    private String name;
    private String color;
    private boolean isActive;
    private List<BodegaDTO> quantity;
    private Double unitPrice;
    private Double docenaPrice;
    private Double cienPrice;
    private Double pacaPrice;
    private Integer unitsInPaca;

    public CapColorDTO() {
    }

    public CapColorDTO(String name, String color, boolean isActive, List<BodegaDTO> quantity, Double unitPrice, Double docenaPrice, Double cienPrice, Double pacaPrice, Integer unitsInPaca) {
        this.name = name;
        this.color = color;
        this.isActive = isActive;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.docenaPrice = docenaPrice;
        this.cienPrice = cienPrice;
        this.pacaPrice = pacaPrice;
        this.unitsInPaca = unitsInPaca;
    }

    public CapColorDTO(CapColor capColor) {
        this.name = capColor.getCap().getName();
        this.color = capColor.getColor();
        this.quantity = capColor.getBodegas().stream().map(BodegaDTO::new).collect(Collectors.toList());
        this.unitPrice = capColor.getUnit_price();
        this.docenaPrice = capColor.getDocena_price();
        this.cienPrice = capColor.getCien_price();
        this.pacaPrice = capColor.getPaca_price();
        this.unitsInPaca = capColor.getUnits_in_paca();
    }


    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public List<BodegaDTO> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<BodegaDTO> quantity) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}