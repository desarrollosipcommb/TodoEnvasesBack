package com.sipcommb.envases.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "extractos")
public class Extractos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    
    private String description;
    
    private Integer quantity; // Stock quantity
    
    private BigDecimal price22ml; // Price for 22ml
    
    private BigDecimal price60ml; // Price for 60ml
    
    private BigDecimal price125ml; // Price for 125ml
    
    private BigDecimal price250ml; // Price for 250ml
    
    private BigDecimal price500ml; // Price for 500ml
    
    private BigDecimal price1000ml; // Price for 1000ml

    @Column(name = "is_active")
    private boolean active = true; // Default value for active status

    public Extractos() {
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice22ml() {
        return price22ml;
    }

    public void setPrice22ml(BigDecimal price22ml) {
        this.price22ml = price22ml;
    }

    public BigDecimal getPrice60ml() {
        return price60ml;
    }

    public void setPrice60ml(BigDecimal price60ml) {
        this.price60ml = price60ml;
    }

    public BigDecimal getPrice125ml() {
        return price125ml;
    }

    public void setPrice125ml(BigDecimal price125ml) {
        this.price125ml = price125ml;
    }

    public BigDecimal getPrice250ml() {
        return price250ml;
    }

    public void setPrice250ml(BigDecimal price250ml) {
        this.price250ml = price250ml;
    }

    public BigDecimal getPrice500ml() {
        return price500ml;
    }

    public void setPrice500ml(BigDecimal price500ml) {
        this.price500ml = price500ml;
    }

    public BigDecimal getPrice1000ml() {
        return price1000ml;
    }

    public void setPrice1000ml(BigDecimal price1000ml) {
        this.price1000ml = price1000ml;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
}

/*CREATE TABLE extractos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    quantity INT NOT NULL DEFAULT 0, -- Stock quantity
    price22ml DECIMAL(10, 2) DEFAULT 0.00 NOT NULL,
    price60ml DECIMAL(10, 2) DEFAULT 0.00,
    price125ml DECIMAL(10, 2) DEFAULT 0.00,
    price250ml DECIMAL(10, 2) DEFAULT 0.00,
    price500ml DECIMAL(10, 2) DEFAULT 0.00,
    price1000ml DECIMAL(10, 2) DEFAULT 0.00,
); */
