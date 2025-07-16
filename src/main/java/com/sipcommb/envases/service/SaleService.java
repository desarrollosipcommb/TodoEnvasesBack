package com.sipcommb.envases.service;



import com.sipcommb.envases.dto.SaleItemRequest;
import com.sipcommb.envases.dto.SaleRequest;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.Combo;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.ComboRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.SaleItemRepository;
import com.sipcommb.envases.repository.SaleRepository;
import com.sipcommb.envases.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class SaleService {

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private JarRepository jarRepository;

    @Autowired
    private CapRepository capRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    jwtService.getUserIdFromToken(token).intValue()

    public void addSale(SaleRequest saleRequest, String token){
        Sale sale = new Sale();

        if(saleRequest.getItems().size() == 0 || saleRequest.getItems() == null || saleRequest.getItems().isEmpty()){
            throw new IllegalArgumentException("Debe especificar al menos un item de venta");
        }

        try{
            sale.setPaymentMethod(Sale.PaymentMethod.valueOf(saleRequest.getPaymentMethod()));
        }catch (Exception e) {
            throw new IllegalArgumentException("Problema al establecer el método de pago: " + e.getMessage());
        }

        sale.setClientName(saleRequest.getClientName());
        sale.setClientEmail(saleRequest.getClientEmail());
        sale.setClientPhone(saleRequest.getClientPhone());
        Optional<User> userOpt = userRepository.findById(jwtService.getUserIdFromToken(token));
       
        if(!userOpt.isPresent()){
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        sale.setSeller(userOpt.get());
        sale.setNotes(saleRequest.getDescripion() != null ? saleRequest.getDescripion() : "");
        // Convert String to LocalDateTime
        java.time.LocalDateTime saleDate = java.time.LocalDateTime.parse(saleRequest.getSaleDate());
        sale.setSaleDate(saleDate);

        List<SaleItemRequest> saleItems = saleRequest.getItems();

        List<SaleItem> saleItemList = new ArrayList<>();
        for(SaleItemRequest saleItemRequest : saleItems){
            SaleItem saleItem = checkSaleItems(saleItemRequest);
            saleItem.setSale(sale);
            saleItemList.add(saleItem);
            saleItem.setSale(sale);
            sale.addPrice(saleItem.getSubtotal());
        }

        saleItemRepository.saveAll(saleItemList);
        sale.setCreatedAt(java.time.LocalDateTime.now());
        sale.setUpdatedAt(java.time.LocalDateTime.now());
        saleRepository.save(sale);

    }

    private SaleItem checkSaleItems(SaleItemRequest saleItemRequest) {
        
        if( saleItemRequest.getQuantity() <= 0){
            throw new IllegalArgumentException("La cantidad de venta no puede ser negativa o cero");
        }

        if( saleItemRequest.getComboName() == null && saleItemRequest.getJarName() == null && saleItemRequest.getCapName() == null){
            throw new IllegalArgumentException("Debe especificar al menos un item de venta");
        }

        if( (saleItemRequest.getComboName() != null && saleItemRequest.getJarName() != null) || 
            (saleItemRequest.getCapName() != null && saleItemRequest.getJarName() != null) ||  
            (saleItemRequest.getCapName() != null && saleItemRequest.getComboName() != null)){
            throw new IllegalArgumentException("No se puede especificar más de un item de venta al mismo tiempo");
        }

        if( saleItemRequest.getComboName() != null){
            Combo combo = manageCombo(saleItemRequest.getComboName());
            return createSaleItem(combo, saleItemRequest);
        }

        if( saleItemRequest.getJarName() != null){
            Jar jar = manageJar(saleItemRequest.getJarName());
            return createSaleItem(jar, saleItemRequest);
        }

        if( (saleItemRequest.getCapColor() == null || saleItemRequest.getCapColor() =="") && (saleItemRequest.getDiameter() == null || saleItemRequest.getDiameter() == "") ){
            throw new IllegalArgumentException("Debe especificar el color y el diametro de la tapa");
        }

        if( saleItemRequest.getCapName() != null ){
            Cap cap = manageCap(saleItemRequest.getCapName(), saleItemRequest.getDiameter(), saleItemRequest.getCapColor());
            return createSaleItem(cap, saleItemRequest);
        }

        throw new IllegalArgumentException("No se pudo determinar el tipo de item de venta");

    }

    private SaleItem createSaleItem(Combo combo, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(combo.getJar());
        saleItem.setCap(combo.getCap());
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(java.math.BigDecimal.valueOf(determinePrice(combo, saleItemRequest)));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(java.math.BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(SaleItem.ItemType.COMBO);
        saleItem.setSale(null); 

        return saleItem;
    }

    private SaleItem createSaleItem(Jar jar, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(jar);
        saleItem.setCap(null);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(determinePrice(jar, saleItemRequest));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(java.math.BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(SaleItem.ItemType.JAR);
        saleItem.setSale(null); 

        return saleItem;
    }

    private SaleItem createSaleItem(Cap cap, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(null);
        saleItem.setCap(cap);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(determinePrice(cap, saleItemRequest));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(java.math.BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(SaleItem.ItemType.CAP);
        saleItem.setSale(null); 

        return saleItem;
    }

    private Combo manageCombo(String comboName) {
        Optional<Combo> comboOpt = comboRepository.findByName(comboName);
        if(!comboOpt.isPresent()){
            throw new IllegalArgumentException("Combo no encontrado: " + comboName);
        }
      
        Combo combo = comboOpt.get();

        if(!combo.getActive()){
            throw new IllegalArgumentException("Combo inactivo: " + comboName);
        }

        return combo;
    }

    private Jar manageJar(String jarName) {
        Optional<Jar> jarOpt = jarRepository.getByName(jarName);
        if(!jarOpt.isPresent()){
            throw new IllegalArgumentException("Tarro no encontrado: " + jarName);
        }
      
        Jar jar = jarOpt.get();

        if(!jar.getIsActive()){
            throw new IllegalArgumentException("Tarro inactivo: " + jarName);
        }

        return jar;
    }

    private Cap manageCap(String capName, String diameter, String color) {
        Optional<Cap> capOpt = capRepository.findByNameAndDiameterAndColor(capName, diameter, color);
        if(!capOpt.isPresent()){
            throw new IllegalArgumentException("Tapa no encontrada: " + capName);
        }
      
        Cap cap = capOpt.get();

        if(!cap.getIsActive()){
            throw new IllegalArgumentException("Tapa inactiva: " + capName);
        }

        return cap;
    }

    private double determinePrice(Combo combo, SaleItemRequest saleItemRequest) {
        if(combo.getCienPrice() != null && combo.getCienPrice() > 0 && saleItemRequest.getQuantity() >= 100){
            return combo.getCienPrice();
        }

        if((combo.getDocenaPrice() !=null || combo.getDocenaPrice() > 0) && (saleItemRequest.getQuantity() >= 12 || saleItemRequest.getQuantity() % 12 == 0)){
            return combo.getDocenaPrice();
        }

        return combo.getUnitPrice();

    }

    private BigDecimal determinePrice(Jar jar, SaleItemRequest saleItemRequest) {
        if(saleItemRequest.getQuantity() == jar.getUnitsInPaca()){
            return jar.getPacaPrice();
        }

        if(jar.getCienPrice() != null && jar.getCienPrice().compareTo(java.math.BigDecimal.ZERO) > 0 && saleItemRequest.getQuantity() >= 100){
            return jar.getCienPrice();
        }

        if((jar.getDocenaPrice() != null && jar.getDocenaPrice().compareTo(java.math.BigDecimal.ZERO) > 0) && (saleItemRequest.getQuantity() >= 12 || saleItemRequest.getQuantity() % 12 == 0)){
            return jar.getDocenaPrice();
        }

        return jar.getUnitPrice();

    }

    private BigDecimal determinePrice(Cap cap, SaleItemRequest saleItemRequest) {
        if(saleItemRequest.getQuantity() == cap.getUnitsInPaca()){
            return cap.getPacaPrice();
        }

        if(cap.getCienPrice() != null && cap.getCienPrice().compareTo(java.math.BigDecimal.ZERO) > 0 && saleItemRequest.getQuantity() >= 100){
            return cap.getCienPrice();
        }

        if((cap.getDocenaPrice() != null && cap.getDocenaPrice().compareTo(java.math.BigDecimal.ZERO) > 0) && (saleItemRequest.getQuantity() >= 12 || saleItemRequest.getQuantity() % 12 == 0)){
            return cap.getDocenaPrice();
        }

        return cap.getUnitPrice();

    }
    
    
}
