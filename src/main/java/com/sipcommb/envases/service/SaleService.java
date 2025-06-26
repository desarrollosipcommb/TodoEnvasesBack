package com.sipcommb.envases.service;

import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.repository.SaleRepository;
import com.sipcommb.envases.repository.SaleItemRepository;
import com.sipcommb.envases.repository.UserRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.CapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SaleService {
    
    @Autowired
    private SaleRepository saleRepository;
    
    @Autowired
    private SaleItemRepository saleItemRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JarRepository jarRepository;
    
    @Autowired
    private CapRepository capRepository;
    
    // Create a new sale
    public Sale createSale(Long userId, String saleNumber, String customerName) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        
        Sale sale = new Sale();
        sale.setSeller(userOpt.get());
        sale.setSaleNumber(saleNumber);
        sale.setCustomerName(customerName);
        sale.setSaleDate(LocalDateTime.now());
        sale.setTotalAmount(BigDecimal.ZERO);
        
        return saleRepository.save(sale);
    }
    
    // Add item to sale (jar)
    public SaleItem addJarToSale(Long saleId, Long jarId, Integer quantity, BigDecimal unitPrice) {
        Optional<Sale> saleOpt = saleRepository.findById(saleId);
        Optional<Jar> jarOpt = jarRepository.findById(jarId);
        
        if (!saleOpt.isPresent()) {
            throw new RuntimeException("Sale not found with id: " + saleId);
        }
        if (!jarOpt.isPresent()) {
            throw new RuntimeException("Jar not found with id: " + jarId);
        }
        
        Jar jar = jarOpt.get();
        
        // Check if there's enough stock
        if (jar.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + jar.getQuantity() + ", Requested: " + quantity);
        }
        
        Sale sale = saleOpt.get();
        SaleItem saleItem = new SaleItem(sale, jar, quantity, unitPrice);
        saleItem = saleItemRepository.save(saleItem);
        
        // Update jar stock
        jar.setQuantity(jar.getQuantity() - quantity);
        jarRepository.save(jar);
        
        // Update sale total
        updateSaleTotal(sale);
        
        return saleItem;
    }
    
    // Add item to sale (cap)
    public SaleItem addCapToSale(Long saleId, Long capId, Integer quantity, BigDecimal unitPrice) {
        Optional<Sale> saleOpt = saleRepository.findById(saleId);
        Optional<Cap> capOpt = capRepository.findById(capId);
        
        if (!saleOpt.isPresent()) {
            throw new RuntimeException("Sale not found with id: " + saleId);
        }
        if (!capOpt.isPresent()) {
            throw new RuntimeException("Cap not found with id: " + capId);
        }
        
        Cap cap = capOpt.get();
        
        // Check if there's enough stock
        if (cap.getQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + cap.getQuantity() + ", Requested: " + quantity);
        }
        
        Sale sale = saleOpt.get();
        SaleItem saleItem = new SaleItem(sale, cap, quantity, unitPrice);
        saleItem = saleItemRepository.save(saleItem);
        
        // Update cap stock
        cap.setQuantity(cap.getQuantity() - quantity);
        capRepository.save(cap);
        
        // Update sale total
        updateSaleTotal(sale);
        
        return saleItem;
    }
    
    // Remove item from sale
    public void removeSaleItem(Long saleItemId) {
        Optional<SaleItem> saleItemOpt = saleItemRepository.findById(saleItemId);
        if (!saleItemOpt.isPresent()) {
            throw new RuntimeException("Sale item not found with id: " + saleItemId);
        }
        
        SaleItem saleItem = saleItemOpt.get();
        Sale sale = saleItem.getSale();
        
        // Restore stock
        if (saleItem.getJar() != null) {
            Jar jar = saleItem.getJar();
            jar.setQuantity(jar.getQuantity() + saleItem.getQuantity());
            jarRepository.save(jar);
        } else if (saleItem.getCap() != null) {
            Cap cap = saleItem.getCap();
            cap.setQuantity(cap.getQuantity() + saleItem.getQuantity());
            capRepository.save(cap);
        }
        
        saleItemRepository.delete(saleItem);
        
        // Update sale total
        updateSaleTotal(sale);
    }
    
    // Update sale total amount
    private void updateSaleTotal(Sale sale) {
        List<SaleItem> items = saleItemRepository.findBySale(sale);
        BigDecimal total = items.stream()
                .map(SaleItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        sale.setTotalAmount(total);
        saleRepository.save(sale);
    }
    
    // Get all sales
    public List<Sale> getAllSales() {
        return saleRepository.findAllByOrderBySaleDateDesc();
    }
    
    // Get sale by ID
    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }
    
    // Get sales by user
    public List<Sale> getSalesByUser(Long userId) {
        return saleRepository.findBySellerId(userId);
    }
    
    // Get sale items for a sale
    public List<SaleItem> getSaleItems(Long saleId) {
        return saleItemRepository.findBySaleId(saleId);
    }
    
    // Get sales within date range
    public List<Sale> getSalesInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findSalesBetweenDates(startDate, endDate);
    }
    
    // Delete sale (and restore stock)
    @Transactional
    public void deleteSale(Long saleId) {
        Optional<Sale> saleOpt = saleRepository.findById(saleId);
        if (!saleOpt.isPresent()) {
            throw new RuntimeException("Sale not found with id: " + saleId);
        }
        
        Sale sale = saleOpt.get();
        List<SaleItem> items = saleItemRepository.findBySale(sale);
        
        // Restore stock for all items
        for (SaleItem item : items) {
            if (item.getJar() != null) {
                Jar jar = item.getJar();
                jar.setQuantity(jar.getQuantity() + item.getQuantity());
                jarRepository.save(jar);
            } else if (item.getCap() != null) {
                Cap cap = item.getCap();
                cap.setQuantity(cap.getQuantity() + item.getQuantity());
                capRepository.save(cap);
            }
        }
        
        // Delete all sale items first
        saleItemRepository.deleteAll(items);
        
        // Delete the sale
        saleRepository.delete(sale);
    }
}
