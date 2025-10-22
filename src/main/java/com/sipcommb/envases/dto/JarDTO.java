package com.sipcommb.envases.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sipcommb.envases.entity.Jar;

public class JarDTO {

    private String name;

    private String diameter;

    private List<BodegaDTO> quantity;

    private String unitPrice;

    private String docenaPrice;

    private String cienPrice;

    private String pacaPrice;

    private int unitsInPaca;

   private Boolean isActive;

    // Default constructor

    public JarDTO() {
        // Default constructor
    }

    // Parameterized constructor
    public JarDTO(Jar jar) {
        this.name = jar.getName();
        this.diameter = jar.getJarType() != null ? jar.getJarType().getDiameter() : "";
        this.quantity = jar.getBodegas().stream().map(BodegaDTO::new).collect(Collectors.toList());
        this.unitPrice = String.valueOf(jar.getUnitPrice());
        this.docenaPrice = String.valueOf(jar.getDocenaPrice());
        this.cienPrice = String.valueOf(jar.getCienPrice());
        this.pacaPrice = String.valueOf(jar.getPacaPrice());
        this.unitsInPaca = jar.getUnitsInPaca();
        this.isActive= jar.getIsActive();
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

    public List<BodegaDTO> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<BodegaDTO> quantity) {
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

  public Boolean getActive() {
    return isActive;
  }

  public void setActive(Boolean active) {
    isActive = active;
  }
}
