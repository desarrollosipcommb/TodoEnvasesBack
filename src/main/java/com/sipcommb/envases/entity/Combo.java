package com.sipcommb.envases.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "combos")
public class Combo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private Double unitPrice = 0.00;

    @Column(name = "docena_price", precision = 10, scale = 2, nullable = false)
    private Double docenaPrice = 0.00; // Price for a dozen combos

    @Column(name = "cien_price", precision = 10, scale = 2, nullable = false)
    private Double cienPrice = 0.00; // Price for a hundred combos

    @Column(name = "paca_price", precision = 10, scale = 2, nullable = false)
    private Double pacaPrice = 0.00; // Price for a pack of combos

    @Column(name = "units_in_paca", nullable = false)
    private Integer unitsInPaca = 0; // Number of combos in a pack

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jar_id", nullable = false)
    private Jar jar; // Array of jar IDs in the combo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cap_id", nullable = false)
    private Cap cap; // Array of cap IDs in the combo

    public Combo() {
    }

    public Long getId() {
        return id;
    }

    public Jar getJar() {
        return jar;
    }

    public Cap getCap() {
        return cap;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public Double getDocenaPrice() {
        return docenaPrice;
    }

    public Double getCienPrice() {
        return cienPrice;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setJar(Jar jar) {
        this.jar = jar;
    }

    public void setCap(Cap cap) {
        this.cap = cap;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setDocenaPrice(Double docenaPrice) {
        this.docenaPrice = docenaPrice;
    }

    public void setCienPrice(Double cienPrice) {
        this.cienPrice = cienPrice;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Double getPacaPrice() {
        return pacaPrice;
    }

    public void setPacaPrice(Double pacaPrice) {
        this.pacaPrice = pacaPrice;
    }

    public Integer getUnitsInPaca() {
        return unitsInPaca;
    }

    public void setUnitsInPaca(Integer unitsInPaca) {
        this.unitsInPaca = unitsInPaca;
    }

}