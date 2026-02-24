package com.sipcommb.envases.dto;


import com.sipcommb.envases.entity.JarType;

public class JarTypeDTO {

    private String name;
    private String description;
    private String diameter;
    private Boolean isActive;

    public JarTypeDTO() {
    }

    public JarTypeDTO(String name, String description, String diameter, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.diameter = diameter;
        this.isActive = isActive;
    }

    public JarTypeDTO(JarType jarType) {
        this.name = jarType.getName();
        this.description = jarType.getDescription();
        this.diameter = jarType.getDiameter();
        this.isActive = jarType.getIsActive();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
}
