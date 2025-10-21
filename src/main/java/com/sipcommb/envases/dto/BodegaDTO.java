package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.BodegaCapColor;
import com.sipcommb.envases.entity.BodegaExtractos;
import com.sipcommb.envases.entity.BodegaJar;
import com.sipcommb.envases.entity.BodegaQuimicos;

public class BodegaDTO {
    
    private String name;

    private Integer quantity;

    public BodegaDTO() {
    }

    public BodegaDTO(String name, Integer quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public BodegaDTO(BodegaCapColor bodegaCapColor) {
        this.name = bodegaCapColor.getBodega().getName();
        this.quantity = bodegaCapColor.getQuantity();
    }

    public BodegaDTO(BodegaExtractos bodegaExtractos) {
        this.name = bodegaExtractos.getBodega().getName();
        this.quantity = bodegaExtractos.getQuantity();
    }

    public BodegaDTO(BodegaQuimicos bodegaQuimicos) {
        this.name = bodegaQuimicos.getBodega().getName();
        this.quantity = bodegaQuimicos.getQuantity();
    }

    public BodegaDTO(BodegaJar bodegaJar) {
        this.name = bodegaJar.getBodega().getName();
        this.quantity = bodegaJar.getQuantity();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
