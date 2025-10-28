package com.sipcommb.envases.dto;

import java.util.List;

public class ExtractoRequest {
    
    private String name; 

    private String description;
    
    private List<BodegaDTO> bodega;

    private Double price22ml; // Price for 22ml

    private Double price60ml; // Price for 60ml

    private Double price125ml; // Price for 125ml

    private Double price250ml; // Price for 250ml

    private Double price500ml; // Price for 500ml

    private Double price1000ml; // Price for 1000ml

    private boolean active; // Indicates if the extracto is active

    public ExtractoRequest() {
    }

    public ExtractoRequest(String name, String description, List<BodegaDTO> bodega, Double price22ml, Double price60ml, Double price125ml, Double price250ml, Double price500ml, Double price1000ml) {
        this.name = name;
        this.description = description;
        this.bodega = bodega;
        this.price22ml = price22ml;
        this.price60ml = price60ml;
        this.price125ml = price125ml;
        this.price250ml = price250ml;
        this.price500ml = price500ml;
        this.price1000ml = price1000ml;

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

    public List<BodegaDTO> getBodega() {
        return bodega;
    }

    public void setBodega(List<BodegaDTO> bodega) {
        this.bodega = bodega;
    }

    public Double getPrice22ml() {
        return price22ml;
    }

    public void setPrice22ml(Double price22ml) {
        this.price22ml = price22ml;
    }

    public Double getPrice60ml() {
        return price60ml;
    }

    public void setPrice60ml(Double price60ml) {
        this.price60ml = price60ml;
    }

    public Double getPrice125ml() {
        return price125ml;
    }

    public void setPrice125ml(Double price125ml) {
        this.price125ml = price125ml;
    }

    public Double getPrice250ml() {
        return price250ml;
    }

    public void setPrice250ml(Double price250ml) {
        this.price250ml = price250ml;
    }

    public Double getPrice500ml() {
        return price500ml;
    }

    public void setPrice500ml(Double price500ml) {
        this.price500ml = price500ml;
    }

    public Double getPrice1000ml() {
        return price1000ml;
    }

    public void setPrice1000ml(Double price1000ml) {
        this.price1000ml = price1000ml;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
