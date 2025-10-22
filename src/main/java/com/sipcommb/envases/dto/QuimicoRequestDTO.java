package com.sipcommb.envases.dto;

public class QuimicoRequestDTO {

    private String name;

    private String description;

    private String bodegaName;

    private Integer quantity;

    private Double unitPrice;

    private boolean estado;

    public QuimicoRequestDTO() {
    }

    public QuimicoRequestDTO(String name, String description, String bodegaName, Integer quantity, Double unitPrice) {
        this.name = name;
        this.description = description;
        this.bodegaName = bodegaName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
       
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

    public String getBodegaName() {
        return bodegaName;
    }

    public void setBodegaName(String bodegaName) {
        this.bodegaName = bodegaName;
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

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
}
