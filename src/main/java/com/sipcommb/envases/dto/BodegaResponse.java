package com.sipcommb.envases.dto;

import java.util.ArrayList;
import java.util.List;

public class BodegaResponse {

    private String bodegaName;

    private List<BodegaItem> items = new ArrayList<>();

    public BodegaResponse() {
    }

    public BodegaResponse(String bodegaName, List<BodegaItem> items) {
        this.bodegaName = bodegaName;
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


    
}
