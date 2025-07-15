package com.sipcommb.envases.dto;

public class JarTypeDTO {

    private String name;
    private String description;
    private String diameter;

    public JarTypeDTO() {
    }

    public JarTypeDTO(String name, String description, String diameter) {
        this.name = name;
        this.description = description;
        this.diameter = diameter;
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

    
}
