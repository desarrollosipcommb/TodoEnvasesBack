package com.sipcommb.envases.entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caps")
public class Cap {


    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Size(max = 50)
    private String color;
    
    @Min(0)
    private Integer quantity = 0;
    
    @DecimalMin("0.0")
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @DecimalMin("0.0")
    @Column(name = "docena_price", precision = 10, scale = 2)
    private BigDecimal docenaPrice = BigDecimal.ZERO;

    @DecimalMin("0.0")
    @Column(name = "cien_price", precision = 10, scale = 2)
    private BigDecimal cienPrice = BigDecimal.ZERO;

    @DecimalMin("0.0")
    @Column(name = "paca_price", precision = 10, scale = 2)
    private BigDecimal pacaPrice = BigDecimal.ZERO;

    @Column(name = "units_in_paca")
    private Integer unitsInPaca = 0;

    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diameter", nullable = true)
    private JarType jarType;
    
    // Constructors
    public Cap() {}
    
    public Cap(String name, String description, String color, JarType jarType, Integer quantity, BigDecimal unitPrice) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.jarType = jarType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    
    // Getters and Setters

    public BigDecimal getDocenaPrice() { return docenaPrice; }
    public void setDocenaPrice(BigDecimal docenaPrice) { this.docenaPrice = docenaPrice; }

    public BigDecimal getCienPrice() { return cienPrice; }
    public void setCienPrice(BigDecimal cienPrice) { this.cienPrice = cienPrice; }

    public BigDecimal getPacaPrice() { return pacaPrice; }
    public void setPacaPrice(BigDecimal pacaPrice) { this.pacaPrice = pacaPrice; }

    public Integer getUnitsInPaca() { return unitsInPaca; }
    public void setUnitsInPaca(Integer unitsInPaca) { this.unitsInPaca = unitsInPaca; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public JarType getJarType() { return jarType; }
    public void setJarType(JarType jarType) { this.jarType = jarType; }
}
