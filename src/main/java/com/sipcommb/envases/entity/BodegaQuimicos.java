package com.sipcommb.envases.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bodega_quimicos")
public class BodegaQuimicos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @ManyToOne
    @JoinColumn(name = "quimico_id", nullable = false)
    private Quimicos quimico;

    private Integer quantity;

    public BodegaQuimicos() {
    }

    public BodegaQuimicos(Bodega bodega, Quimicos quimico, Integer quantity) {
        this.bodega = bodega;
        this.quimico = quimico;
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

    public Quimicos getQuimico() {
        return quimico;
    }

    public void setQuimico(Quimicos quimico) {
        this.quimico = quimico;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
