package com.sipcommb.envases.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.sipcommb.envases.entity.Quimicos;

public class QuimicosDTO {

    private String name;

    private String description;

    private List<BodegaDTO> quantity;

    private Double unitPrice;

    private boolean estado;

    public QuimicosDTO() {
    }

    public QuimicosDTO(Quimicos qumico){
        this.name = qumico.getName();
        this.description = qumico.getDescription();
        this.quantity = qumico.getBodegas().stream().map(BodegaDTO::new).collect(Collectors.toList());
        this.unitPrice = qumico.getUnitPrice().doubleValue();
        this.estado = qumico.isActive();
    }

    public QuimicosDTO(String name, String description, List<BodegaDTO> quantity, Double unitPrice) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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

    public List<BodegaDTO> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<BodegaDTO> quantity) {
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

  public boolean isEstado() {
    return estado;
  }

  public void setEstado(boolean estado) {
    this.estado = estado;
  }
}
