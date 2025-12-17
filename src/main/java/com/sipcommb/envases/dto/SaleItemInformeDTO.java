package com.sipcommb.envases.dto;

public class SaleItemInformeDTO {

    String itemName;

    Integer totalQuantitySold;

    boolean activa;

    String fecha; 

    public SaleItemInformeDTO() {
    }

    public SaleItemInformeDTO(String itemName, Integer totalQuantitySold, boolean activa, String fecha) {
        this.itemName = itemName;
        this.totalQuantitySold = totalQuantitySold;
        this.activa = activa;
        this.fecha = fecha;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(Integer totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
}
