package com.sipcommb.envases.dto;

import java.util.List;

public class QuimicoRequestDTO {

    private String name;

    private String description;

    private List<BodegaDTO> quantity;

    private Double unitPrice;

    private boolean estado;

    public QuimicoRequestDTO() {
    }

    public QuimicoRequestDTO(String name, String description, List<BodegaDTO> quantity, Double unitPrice) {
        this.name = name;
        this.description = description;
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

    public List<BodegaDTO> getBodegaName() {
        return quantity;
    }

    public void setBodegaName(List<BodegaDTO> quantity) {
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
