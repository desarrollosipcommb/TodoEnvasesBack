package com.sipcommb.envases.dto;

import java.util.ArrayList;
import java.util.List;

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
