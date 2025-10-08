package com.sipcommb.envases.dto;

import javax.validation.constraints.NotBlank;

public class CapRequest {


    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotBlank(message = "El diámetro es obligatorio")
    private String diameter;

    public CapRequest() {
    }

    public CapRequest(String name, String description, String diameter) {
        this.name = name;
        this.description = description;
        this.diameter = diameter;
    }

    // Getters and Setters

    public String getDiameter() {
        return diameter;
    }

    public void setDiameter(String diameter) {
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

}
