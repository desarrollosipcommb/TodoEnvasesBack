package com.sipcommb.envases.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bodega_extractos")
public class BodegaExtractos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @ManyToOne
    @JoinColumn(name = "extracto_id", nullable = false)
    private Extractos extracto;

    private Integer quantity;

    public BodegaExtractos() {
    }

    public BodegaExtractos(Bodega bodega, Extractos extracto, Integer quantity) {
        this.bodega = bodega;
        this.extracto = extracto;
        this.quantity = quantity;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    public Extractos getExtracto() {
        return extracto;
    }

    public void setExtracto(Extractos extracto) {
        this.extracto = extracto;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
}
