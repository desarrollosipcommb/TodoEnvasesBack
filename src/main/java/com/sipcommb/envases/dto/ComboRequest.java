package com.sipcommb.envases.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sipcommb.envases.entity.Combo;

public class ComboRequest {
    
    private String name;

    private String jarName;

    private List<CapRequest> capRequests;

    private Double unitPrice;

    private Double docenaPrice; // Price for a dozen combos

    private Double cienPrice; // Price for a hundred combos

    private Double pacaPrice; // Price for a pack of combos

    private String description;

    public ComboRequest() {}

    public ComboRequest(String name, String jarName, List<CapRequest> capRequests, 
            Double unitPrice, Double docenaPrice, Double cienPrice, Double pacaPrice, String description) {

        this.name = name;
        this.jarName = jarName;
        this.capRequests = capRequests;
        this.unitPrice = unitPrice;
        this.docenaPrice = docenaPrice;
        this.cienPrice = cienPrice;
        this.pacaPrice = pacaPrice;
        this.description = description;
    }

    public ComboRequest(Combo combo){
        this.name = combo.getName();
        this.capRequests = combo.getCaps().stream()
                            .map(cc -> new CapRequest(cc.getCap().getName(), cc.getCap().getDescription(), cc.getCap().getJarType().getDiameter()))
                            .collect(Collectors.toList());
        this.unitPrice = combo.getUnitPrice();
        this.docenaPrice = combo.getDocenaPrice();
        this.cienPrice = combo.getCienPrice();
        this.description = combo.getDescription();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public List<CapRequest> getCapRequests() {
        return capRequests;
    }

    public void setCapRequests(List<CapRequest> capRequests) {
        this.capRequests = capRequests;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Double getDocenaPrice() {
        return docenaPrice;
    }

    public void setDocenaPrice(Double docenaPrice) {
        this.docenaPrice = docenaPrice;
    }

    public Double getCienPrice() {
        return cienPrice;
    }

    public void setCienPrice(Double cienPrice) {
        this.cienPrice = cienPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPacaPrice() {
        return pacaPrice;
    }

    public void setPacaPrice(Double pacaPrice) {
        this.pacaPrice = pacaPrice;
    }

}
