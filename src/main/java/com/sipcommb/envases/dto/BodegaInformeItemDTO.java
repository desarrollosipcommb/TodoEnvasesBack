package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.BodegaCapColor;
import com.sipcommb.envases.entity.BodegaExtractos;
import com.sipcommb.envases.entity.BodegaJar;
import com.sipcommb.envases.entity.BodegaQuimicos;

public class BodegaInformeItemDTO {
    
    private String itemName;

    private Integer quantity;

    public BodegaInformeItemDTO() {
    }

    public BodegaInformeItemDTO(String itemName, Integer quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public BodegaInformeItemDTO(BodegaCapColor bodegaCapColor) {
        this.itemName = bodegaCapColor.getCapColor().getCap().getName() + " - " + bodegaCapColor.getCapColor().getColor();
        this.quantity = bodegaCapColor.getQuantity();
    }

    public BodegaInformeItemDTO(BodegaQuimicos bodegaQuimicos) {
        this.itemName = bodegaQuimicos.getQuimico().getName();
        this.quantity = bodegaQuimicos.getQuantity();
    }

    public BodegaInformeItemDTO(BodegaExtractos bodegaExtractos) {
        this.itemName = bodegaExtractos.getExtracto().getName();
        this.quantity = bodegaExtractos.getQuantity();
    }

    public BodegaInformeItemDTO(BodegaJar bodegaJar) {
        this.itemName = bodegaJar.getJar().getName();
        this.quantity = bodegaJar.getQuantity();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
