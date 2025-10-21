package com.sipcommb.envases.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "jars")
public class Jar {

  
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @DecimalMin("0.0")
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cap_diameter", nullable = true)
    private JarType jarType;

    @Column(name = "docena_price", precision = 10, scale = 2)
    private BigDecimal docenaPrice = BigDecimal.ZERO;

    @Column(name = "cien_price", precision = 10, scale = 2)
    private BigDecimal cienPrice = BigDecimal.ZERO;

    @Column(name = "paca_price", precision = 10, scale = 2)
    private BigDecimal pacaPrice = BigDecimal.ZERO;

    @Column(name = "units_in_paca")
    private int unitsInPaca = 0;

    @OneToMany(mappedBy = "jar", fetch = FetchType.LAZY)
    private List<BodegaJar> bodegas = new ArrayList<>();
    
    // Constructors
    public Jar() {}
    
    public Jar(String name, String description, JarType jarType, BigDecimal unitPrice) {
        this.name = name;
        this.description = description;
        this.jarType = jarType;
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
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

    public BigDecimal getDocenaPrice() { return docenaPrice; }
    public void setDocenaPrice(BigDecimal docenaPrice) { this.docenaPrice = docenaPrice; }

    public BigDecimal getCienPrice() { return cienPrice; }
    public void setCienPrice(BigDecimal cienPrice) { this.cienPrice = cienPrice; }

    public BigDecimal getPacaPrice() { return pacaPrice; }
    public void setPacaPrice(BigDecimal pacaPrice) { this.pacaPrice = pacaPrice; }

    public int getUnitsInPaca() { return unitsInPaca; }
    public void setUnitsInPaca(int unitsInPaca) { this.unitsInPaca = unitsInPaca; }

    public List<BodegaJar> getBodegas() { return bodegas; }
    public void setBodegas(List<BodegaJar> bodegas) { this.bodegas = bodegas; }
    
}
