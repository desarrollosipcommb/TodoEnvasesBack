package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Quimicos;

public class QuimicosDTO {

    private String name;

    private String description;

    private Integer quantity;

    private Double unitPrice;

    public QuimicosDTO() {
    }

    public QuimicosDTO(Quimicos qumico){
        this.name = qumico.getName();
        this.description = qumico.getDescription();
        this.quantity = qumico.getQuantity();
        this.unitPrice = qumico.getUnitPrice().doubleValue();
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
