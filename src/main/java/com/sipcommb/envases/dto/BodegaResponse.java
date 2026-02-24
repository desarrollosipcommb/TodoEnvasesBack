package com.sipcommb.envases.dto;

import java.util.ArrayList;
import java.util.List;

import com.sipcommb.envases.entity.Bodega;

public class BodegaResponse {

    private String bodegaName;

    private Long priority;

    private List<BodegaItem> items = new ArrayList<>();

    public BodegaResponse() {
    }

    public BodegaResponse(String bodegaName, Long priority, List<BodegaItem> items) {
        this.bodegaName = bodegaName;
        this.priority = priority;
        this.items = items;
    }

    public BodegaResponse(Bodega bodega){
        this.bodegaName = bodega.getName();
        this.priority = bodega.getPriority();
    }

    public String getBodegaName() {
        return bodegaName;
    }

    public void setBodegaName(String bodegaName) {
        this.bodegaName = bodegaName;
    }

    public List<BodegaItem> getItems() {
        return items;
    }

    public void setItems(List<BodegaItem> items) {
        this.items = items;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }


    
}
