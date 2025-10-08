package com.sipcommb.envases.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "cap_colors")
public class CapColor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cap_id", nullable = false)
    private Cap cap;

    private boolean is_active = true;

    private Integer quantity = 0; // Stock quantity

    private Double unit_price = 0.00;

    private Double docena_price = 0.00; // Price for a dozen jars

    private Double cien_price = 0.00; // Price for a hundred jars

    private Double paca_price = 0.00; // Price for a pack of jars

    private Integer units_in_paca = 0; // Number of caps in a pack

    CapColor() {
    }

    public CapColor(String color, Cap cap, Integer quantity, Double unit_price, Double docena_price,
            Double cien_price, Double paca_price, Integer units_in_paca) {
        this.color = color;
        this.cap = cap;
        this.quantity = quantity;
        this.unit_price = unit_price;
        this.docena_price = docena_price;
        this.cien_price = cien_price;
        this.paca_price = paca_price;
        this.units_in_paca = units_in_paca;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Cap getCap() { return cap; }
    public void setCap(Cap cap) { this.cap = cap; }

    public boolean getIs_active() { return is_active; }
    public void setIs_active(boolean is_active) { this.is_active = is_active; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnit_price() { return unit_price; }
    public void setUnit_price(Double unit_price) { this.unit_price = unit_price; }

    public Double getDocena_price() { return docena_price; }
    public void setDocena_price(Double docena_price) { this.docena_price = docena_price; }

    public Double getCien_price() { return cien_price; }
    public void setCien_price(Double cien_price) { this.cien_price = cien_price; }

    public Double getPaca_price() { return paca_price; }
    public void setPaca_price(Double paca_price) { this.paca_price = paca_price; }

    public Integer getUnits_in_paca() { return units_in_paca; }
    public void setUnits_in_paca(Integer units_in_paca) { this.units_in_paca = units_in_paca; }

}