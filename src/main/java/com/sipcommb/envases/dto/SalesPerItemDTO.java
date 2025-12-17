package com.sipcommb.envases.dto;

import java.util.ArrayList;
import java.util.List;

public class SalesPerItemDTO {
    
    String itemName;

    Long totalQuantitySold;

    List<String> clientNames = new ArrayList<>();

    public SalesPerItemDTO() {
    }

    public SalesPerItemDTO(String itemName, Long totalQuantitySold) {
        this.itemName = itemName;
        this.totalQuantitySold = totalQuantitySold;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Long getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(Long totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public List<String> getClientNames() {
        return clientNames;
    }

    public void setClientNames(List<String> clientNames) {
        this.clientNames = clientNames;
    }
}
