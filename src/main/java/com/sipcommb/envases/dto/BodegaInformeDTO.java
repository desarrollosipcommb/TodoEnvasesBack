package com.sipcommb.envases.dto;

import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class BodegaInformeDTO {

    private String bodegaName;

    private Page<BodegaInformeItemDTO> items = new PageImpl<>(new ArrayList<>());

    public BodegaInformeDTO() {
    }

    public BodegaInformeDTO(String bodegaName, Page<BodegaInformeItemDTO> items) {
        this.bodegaName = bodegaName;
        this.items = items;
    }

    public String getBodegaName() {
        return bodegaName;
    }

    public void setBodegaName(String bodegaName) {
        this.bodegaName = bodegaName;
    }

    public Page<BodegaInformeItemDTO> getItems() {
        return items;
    }

    public void setItems(Page<BodegaInformeItemDTO> items) {
        this.items = items;
    }
    
}
