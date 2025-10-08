package com.sipcommb.envases.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sipcommb.envases.entity.Cap;

public class CapDTO {

    private long id;
    private String name;
    private String diameter;
    private String description;
    private boolean isActive;
    private List<CapColorDTO> colors;
    
    // Default constructor
    public CapDTO() {}

    // Parameterized constructor
    public CapDTO(Cap cap) {
        this.id = cap.getId();
        this.name = cap.getName();
        this.diameter = cap.getJarType() != null ? cap.getJarType().getDiameter() : "";
        this.description = cap.getDescription();
        this.colors = cap.getColors().stream().map(CapColorDTO::new).collect(Collectors.toList());
    }

    // Getters and Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDiameter() {
        return diameter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDiameter(String diameter) {
        this.diameter = diameter;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<CapColorDTO> getColors() {
        return colors;
    }

    public void setColors(List<CapColorDTO> colors) {
        this.colors = colors;
    }

}
