package com.sipcommb.envases.entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;

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
    private CapColor capColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quimico_id")
    private Quimicos quimico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extracto_id")
    private Extractos extracto;

    @Min(1)
    @Column(nullable = false)
    private Integer quantity;

    @DecimalMin("0.0")
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @DecimalMin("0.0")
    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;

    


    
    // Constructors
    public SaleItem() {}
    
    public SaleItem(Sale sale, Jar jar, Integer quantity, BigDecimal unitPrice) {
        this.sale = sale;
        this.jar = jar;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        
    }
    
    public SaleItem(Sale sale, CapColor capColor, Integer quantity, BigDecimal unitPrice, Quimicos quimico, Extractos extracto) {
        this.sale = sale;
        this.capColor = capColor;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.quimico = quimico;
        this.extracto = extracto;
    
    }
    
    
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Sale getSale() { return sale; }
    public void setSale(Sale sale) { this.sale = sale; }
    
    public Jar getJar() { return jar; }
    public void setJar(Jar jar) { this.jar = jar; }

    public CapColor getCapColor() { return capColor; }
    public void setCapColor(CapColor capColor) { this.capColor = capColor; }

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice;}

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType;}

    public Quimicos getQuimico() { return quimico; }
    public void setQuimico(Quimicos quimico) {
        this.quimico = quimico;
    }

    public Extractos getExtracto() { return extracto; }
    public void setExtracto(Extractos extracto) {
        this.extracto = extracto;
    }

}
