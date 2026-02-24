package com.sipcommb.envases.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "bodega_sale")
public class BodegaSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bodega_id", nullable = false)
    private Bodega bodega;

    @ManyToOne
    @JoinColumn(name = "sales_id", nullable = false)
    private Sale sale;

    @Column(name = "item_name", nullable = false)
    private String itemName;

    private Integer quantity;

    public BodegaSale() {
    }

    public BodegaSale(Bodega bodega, Sale sale) {
        this.bodega = bodega;
        this.sale = sale;
    }

    public BodegaSale(Bodega bodega, Sale sale, String itemName, Integer quantity) {
        this.bodega = bodega;
        this.sale = sale;
        this.itemName = itemName;
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

    public Sale getSale() {
        return sale;
    }

    public void setSale(Sale sale) {
        this.sale = sale;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
