package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.BodegaSale;

public class BodegaSaleDTO {

    private String name;

    private String bodegaName;

    private int quantity;

    public BodegaSaleDTO() {
    }

    public BodegaSaleDTO(String bodegaName, int quantity, String name) {
        this.bodegaName = bodegaName;
        this.quantity = quantity;
        this.name = name;
    }

    public BodegaSaleDTO(BodegaSale bodegaSale) {
        this.bodegaName = bodegaSale.getBodega().getName();
        this.quantity = bodegaSale.getQuantity();
        this.name = bodegaSale.getItemName();
    }

    public String getBodegaName() {
        return bodegaName;
    }

    public void setBodegaName(String bodegaName) {
        this.bodegaName = bodegaName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
