package com.sipcommb.envases.dto;

import java.util.ArrayList;
import java.util.List;

public class SaleItemRequest {

    private String comboName;

    private String jarName;

    private String capName;

    private String capColor;

    private String diameter;

    private Integer quantity;

    private String quimicoName;

    private String extractoName;

    //private String comboCapQuantity;

    private List<ComboCapOrderDTO> comboCapOrderDTO = new ArrayList<>();

    public SaleItemRequest() {
    }

    public SaleItemRequest(String comboName, String jarName, String capName, String capColor, 
    String diameter, Integer quantity, String quimicoName, String extractoName) {
        this.comboName = comboName;
        this.jarName = jarName;
        this.capName = capName;
        this.capColor = capColor;
        this.diameter = diameter;
        this.quantity = quantity;
        this.quimicoName = quimicoName;
        this.extractoName = extractoName;
        this.quantity = quantity;
    }

    public String getComboName() {
        return comboName;
    }

    public void setComboName(String comboName) {
        this.comboName = comboName;
    }

    public String getJarName() {
        return jarName;
    }
    public void setJarName(String jarName) {
        this.jarName = jarName;
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

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getQuimicoName() {
        return quimicoName;
    }

    public void setQuimicoName(String quimicoName) {
        this.quimicoName = quimicoName;
    }

    public String getExtractoName() {
        return extractoName;
    }

    public void setExtractoName(String extractoName) {
        this.extractoName = extractoName;
    }
    /* 
    public String getComboCapQuantity() {
        return comboCapQuantity;
    }

    public void setComboCapQuantity(String comboCapQuantity) {
        this.comboCapQuantity = comboCapQuantity;
    }
    */
    public List<ComboCapOrderDTO> getComboCapOrderDTO() {
        return comboCapOrderDTO;
    }

    public void setComboCapOrderDTO(List<ComboCapOrderDTO> comboCapOrderDTO) {
        this.comboCapOrderDTO = comboCapOrderDTO;
    }

}
