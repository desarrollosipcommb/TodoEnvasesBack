package com.sipcommb.envases.dto;

public class SaleItemComboCapRequest {
    
    private String capName;

    private String capColor;

    private int quantity;

    public SaleItemComboCapRequest() {
    }

    public SaleItemComboCapRequest(String capName, String capColor, int quantity) {
        this.capName = capName;
        this.capColor = capColor;
        this.quantity = quantity;
    }

    public String getCapName() {
        return capName;
    }

    public void setCapName(String capName) {
        this.capName = capName;
    }

    public String getCapColor() {
        return capColor;
    }

    public void setCapColor(String capColor) {
        this.capColor = capColor;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
   
}
