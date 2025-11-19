package com.sipcommb.envases.dto;


import org.springframework.data.domain.Page;

public class SalesByClientInformeDTO {
    
    String clientName;

    Integer totalSalesAmount;

    Page<SaleItemInformeDTO> items;

    public SalesByClientInformeDTO() {
    }

    public SalesByClientInformeDTO(String clientName, Integer totalSalesAmount, Page<SaleItemInformeDTO> items) {
        this.clientName = clientName;
        this.totalSalesAmount = totalSalesAmount;
        this.items = items;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Integer getTotalSalesAmount() {
        return totalSalesAmount;
    }

    public void setTotalSalesAmount(Integer totalSalesAmount) {
        this.totalSalesAmount = totalSalesAmount;
    }

    public Page<SaleItemInformeDTO> getItems() {
        return items;
    }

    public void setItems(Page<SaleItemInformeDTO> items) {
        this.items = items;
    }

}
