package com.sipcommb.envases.entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Table(name = "sale_items")
public class SaleItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jar_id")
    private Jar jar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cap_id")
    private Cap cap;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @DecimalMin("0.0")
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @DecimalMin("0.0")
    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    public enum ItemType {
        JAR, CAP, COMBO
    }

    @PrePersist
    @PreUpdate
    private void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
    
    // Constructors
    public SaleItem() {}
    
    public SaleItem(Sale sale, Jar jar, Integer quantity, BigDecimal unitPrice) {
        this.sale = sale;
        this.jar = jar;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        
    }
    
    public SaleItem(Sale sale, Cap cap, Integer quantity, BigDecimal unitPrice) {
        this.sale = sale;
        this.cap = cap;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
       
    }
    
    
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }
    
    public Jar getJar() { return jar; }
    public void setJar(Jar jar) { this.jar = jar; }
    
    public Cap getCap() { return cap; }
    public void setCap(Cap cap) { this.cap = cap; }
    
    // Helper methods
    public String getItemType() {
        return jar != null ? "JAR" : "CAP";
    }
    
    public String getItemName() {
        return jar != null ? jar.getName() : (cap != null ? cap.getName() : "Unknown");
    }
}
