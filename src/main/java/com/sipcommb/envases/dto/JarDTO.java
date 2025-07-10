package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Jar;

public class JarDTO {

    private String name;

    private String diameter;

    private String quantity;

    private String unitPrice;

    private String docenaPrice;

    private String cienPrice;

    private String pacaPrice;

    private int unitsInPaca;

    // Default constructor

    public JarDTO() {
        // Default constructor
    }

    // Parameterized constructor
    public JarDTO(Jar jar) {
        this.name = jar.getName();
        this.diameter = jar.getJarType() != null ? jar.getJarType().getDiameter() : "";
        this.quantity = String.valueOf(jar.getQuantity());
        this.unitPrice = String.valueOf(jar.getUnitPrice());
        this.docenaPrice = String.valueOf(jar.getDocenaPrice());
        this.cienPrice = String.valueOf(jar.getCienPrice());
        this.pacaPrice = String.valueOf(jar.getPacaPrice());
        this.unitsInPaca = jar.getUnitsInPaca();
    }

    //getter and setter methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDocenaPrice() {
        return docenaPrice;
    }

    public void setDocenaPrice(String docenaPrice) {
        this.docenaPrice = docenaPrice;
    }

    public String getCienPrice() {
        return cienPrice;
    }

    public void setCienPrice(String cienPrice) {
        this.cienPrice = cienPrice;
    }


    public String getPacaPrice() {
        return pacaPrice;
    }

    public void setPacaPrice(String pacaPrice) {
        this.pacaPrice = pacaPrice;
    }

    public int getUnitsInPaca() {
        return unitsInPaca;
    }

    public void setUnitsInPaca(int unitsInPaca) {
        this.unitsInPaca = unitsInPaca;
    }


    
}
