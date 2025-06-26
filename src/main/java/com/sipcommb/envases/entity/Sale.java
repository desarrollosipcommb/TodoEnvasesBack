package com.sipcommb.envases.entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 50)
    @Column(name = "sale_number", unique = true, nullable = false)
    private String saleNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User seller;
    
    @Size(max = 100)
    @Column(name = "customer_name")
    private String customerName;
    
    @Size(max = 100)
    @Column(name = "customer_email")
    private String customerEmail;
    
    @Size(max = 20)
    @Column(name = "customer_phone")
    private String customerPhone;
    
    @DecimalMin("0.0")
    @Column(name = "subtotal", precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @DecimalMin("0.0")
    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    
    @DecimalMin("0.0")
    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    
    @DecimalMin("0.0")
    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false)
    private SaleStatus saleStatus = SaleStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "sale_date", nullable = false)
    private LocalDateTime saleDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SaleItem> saleItems = new ArrayList<>();
    
    // Enums
    public enum PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, CHECK
    }
    
    public enum SaleStatus {
        PENDING, COMPLETED, CANCELLED, REFUNDED
    }
    
    // Constructors
    public Sale() {}
    
    public Sale(String saleNumber, User seller, String customerName) {
        this.saleNumber = saleNumber;
        this.seller = seller;
        this.customerName = customerName;
        this.saleDate = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (saleDate == null) {
            saleDate = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotals();
    }
    
    // Business logic methods
    public void addSaleItem(SaleItem saleItem) {
        saleItems.add(saleItem);
        saleItem.setSale(this);
        calculateTotals();
    }
    
    public void removeSaleItem(SaleItem saleItem) {
        saleItems.remove(saleItem);
        saleItem.setSale(null);
        calculateTotals();
    }
    
    public void calculateTotals() {
        subtotal = saleItems.stream()
            .map(SaleItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        totalAmount = subtotal
            .add(taxAmount != null ? taxAmount : BigDecimal.ZERO)
            .subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }
    
    public int getTotalItems() {
        return saleItems.stream()
            .mapToInt(SaleItem::getQuantity)
            .sum();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSaleNumber() { return saleNumber; }
    public void setSaleNumber(String saleNumber) { this.saleNumber = saleNumber; }
    
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { 
        this.taxAmount = taxAmount;
        calculateTotals();
    }
    
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { 
        this.discountAmount = discountAmount;
        calculateTotals();
    }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public SaleStatus getSaleStatus() { return saleStatus; }
    public void setSaleStatus(SaleStatus saleStatus) { this.saleStatus = saleStatus; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDateTime saleDate) { this.saleDate = saleDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public List<SaleItem> getSaleItems() { return saleItems; }
    public void setSaleItems(List<SaleItem> saleItems) { this.saleItems = saleItems; }
}
