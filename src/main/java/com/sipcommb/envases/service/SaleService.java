package com.sipcommb.envases.service;



import com.sipcommb.envases.dto.SaleDTO;
import com.sipcommb.envases.dto.SaleItemDTO;
import com.sipcommb.envases.dto.SaleItemRequest;
import com.sipcommb.envases.dto.SaleRequest;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.Combo;
import com.sipcommb.envases.entity.Extractos;
import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Quimicos;
import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.ComboRepository;
import com.sipcommb.envases.repository.ExtractosRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.QuimicosRepository;
import com.sipcommb.envases.repository.SaleItemRepository;
import com.sipcommb.envases.repository.SaleRepository;
import com.sipcommb.envases.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private QuimicosRepository quimicosRepository;

    @Autowired
    private ExtractosRepository extractosRepository;




    //Lo mismo que addSale pero no guarda la venta en la base de datos, solo crea el objeto SaleDTO
    //maybe se podria crear un metodo para no duplicar codigo, pero no se me ocurre como hacerlo :(
    public SaleDTO planSale(SaleRequest saleRequest, String token) {
         Sale sale = new Sale();

        if( saleRequest.getItems() == null || saleRequest.getItems().size() == 0 || saleRequest.getItems().isEmpty()){
            throw new IllegalArgumentException("Debe especificar al menos un item de venta");
        }

        try{
           sale.setPaymentMethod(Sale.PaymentMethod.valueOf(saleRequest.getPaymentMethod().toUpperCase()));
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.time.LocalDate saleDate = java.time.LocalDate.parse(saleRequest.getSaleDate(), formatter);
        sale.setSaleDate(saleDate);

        List<SaleItemRequest> saleItems = saleRequest.getItems();

        List<SaleItem> saleItemList = new ArrayList<>();
        List<SaleItemDTO> saleItemDTOList = new ArrayList<>();
        for(SaleItemRequest saleItemRequest : saleItems){
            SaleItem saleItem = checkSaleItems(saleItemRequest);

            saleItem.setSale(sale);
            saleItemList.add(saleItem);
            sale.addPrice(saleItem.getSubtotal());

            if(saleItem.getItemType() == ItemType.COMBO){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getComboName(), saleItem));
            } else if(saleItem.getItemType() == ItemType.JAR){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getJarName(), saleItem));
            } else if(saleItem.getItemType() == ItemType.CAP){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getCapName(), saleItem));
            }else if(saleItem.getItemType() == ItemType.QUIMICO){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getQuimicoName(), saleItem));
            } else if(saleItem.getItemType() == ItemType.EXTRACTO){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getExtractoName(), saleItem));
            }

        }

        return new SaleDTO(sale, saleItemDTOList);
       
    }

    

    public SaleDTO addSale(SaleRequest saleRequest, String token){
        Sale sale = new Sale();

        if(saleRequest.getItems() == null || saleRequest.getItems().size() == 0 ||  saleRequest.getItems().isEmpty()){
            throw new IllegalArgumentException("Debe especificar al menos un item de venta");
        }

        try{
            sale.setPaymentMethod(Sale.PaymentMethod.valueOf(saleRequest.getPaymentMethod().toUpperCase()));
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.time.LocalDate saleDate = java.time.LocalDate.parse(saleRequest.getSaleDate(), formatter);
        sale.setSaleDate(saleDate);

        List<SaleItemRequest> saleItems = saleRequest.getItems();

        List<SaleItem> saleItemList = new ArrayList<>();
        List<SaleItemDTO> saleItemDTOList = new ArrayList<>();
        sale.setTotalAmount(BigDecimal.ZERO);
        saleRepository.save(sale);

        for(SaleItemRequest saleItemRequest : saleItems){
            SaleItem saleItem = checkSaleItems(saleItemRequest);

            saleItem.setSale(sale);
            saleItemList.add(saleItem);
            

            sale.addPrice(saleItem.getSubtotal());
             if(saleItem.getItemType() == ItemType.COMBO){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getComboName(), saleItem));
            } else if(saleItem.getItemType() == ItemType.JAR){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getJarName(), saleItem));
            } else if(saleItem.getItemType() == ItemType.CAP){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getCapName(), saleItem));
            } else if(saleItem.getItemType() == ItemType.QUIMICO){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getQuimicoName(), saleItem));
            } else if(saleItem.getItemType() == ItemType.EXTRACTO){
                saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getExtractoName(), saleItem));
            }
        }

        for(SaleItem saleItem : saleItemList){
            modifyInventory(saleItem, userOpt.get().getId().intValue());
        }

       
        sale.setCreatedAt(java.time.LocalDateTime.now());
        sale.setUpdatedAt(java.time.LocalDateTime.now());
        saleRepository.save(sale);
        saleItemRepository.saveAll(saleItemList);

        return new SaleDTO(sale, saleItemDTOList);

    }

    private SaleItem checkSaleItems(SaleItemRequest saleItemRequest) {
        
        if( saleItemRequest.getQuantity() <= 0){
            throw new IllegalArgumentException("La cantidad de venta no puede ser negativa o cero");
        }

        //se revisa que solo se haya especificado un item de venta
        int notNullCount = 0;
        if (saleItemRequest.getComboName() != null) notNullCount++;
        if (saleItemRequest.getJarName() != null) notNullCount++;
        if (saleItemRequest.getCapName() != null) notNullCount++;
        if (saleItemRequest.getQuimicoName() != null) notNullCount++;
        if (saleItemRequest.getExtractoName() != null) notNullCount++;
        
        if (notNullCount != 1) {
            throw new IllegalArgumentException("Debe especificar exactamente un item de venta");
        }else if(notNullCount == 0){
            throw new IllegalArgumentException("Debe especificar al menos un item de venta");
        }

        if( saleItemRequest.getComboName() != null){
            Combo combo = manageCombo(saleItemRequest.getComboName());
            return createSaleItem(combo, saleItemRequest);
        }

        if( saleItemRequest.getJarName() != null){
            Jar jar = manageJar(saleItemRequest.getJarName());
            return createSaleItem(jar, saleItemRequest);
        }

        if( saleItemRequest.getQuimicoName() != null){
            Quimicos quimico = quimicosRepository.findByName(saleItemRequest.getQuimicoName())
                .orElseThrow(() -> new IllegalArgumentException("Químico no encontrado: " + saleItemRequest.getQuimicoName()));
            return createSaleItem(quimico, saleItemRequest);
        }

        if( saleItemRequest.getExtractoName() != null){
            Extractos extracto = extractosRepository.findByName(saleItemRequest.getExtractoName())
                .orElseThrow(() -> new IllegalArgumentException("Extracto no encontrado: " + saleItemRequest.getExtractoName()));
            return createSaleItem(extracto, saleItemRequest);
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
        saleItem.setItemType(ItemType.COMBO);
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
        saleItem.setItemType(ItemType.JAR);
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
        saleItem.setItemType(ItemType.CAP);
        saleItem.setSale(null); 

        return saleItem;
    }

    private SaleItem createSaleItem(Quimicos quimico, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(null);
        saleItem.setCap(null);
        saleItem.setQuimico(quimico);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(quimico.getUnitPrice());
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(java.math.BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(ItemType.QUIMICO);
        saleItem.setSale(null); 
        return saleItem;
    }

    private SaleItem createSaleItem(Extractos extracto, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(null);
        saleItem.setCap(null);
        saleItem.setExtracto(extracto);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(determinePrice(extracto, saleItemRequest));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(java.math.BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(ItemType.EXTRACTO);
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
            throw new IllegalArgumentException("Tapa no encontrada: " + capName+ " con diámetro: " + diameter + " y color: " + color);
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
        if(saleItemRequest.getQuantity() == jar.getUnitsInPaca() && (jar.getPacaPrice() != null && jar.getPacaPrice().compareTo(java.math.BigDecimal.ZERO) > 0)){
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

    private BigDecimal determinePrice(Extractos extracto, SaleItemRequest saleItemRequest) {
       int quantity = saleItemRequest.getQuantity();
        
        if(quantity < 22){
            throw new IllegalArgumentException("La cantidad de extracto debe ser al menos 22ml");
        }

        if(quantity%1000 == 0){
           return extracto.getPrice1000ml();
        }

        if(quantity%500 == 0){
           return extracto.getPrice500ml();
        }

        if(quantity%250 == 0){
           return extracto.getPrice250ml();
        }

        if(quantity%125 == 0){
           return extracto.getPrice125ml();
        }

        if(quantity%60 == 0){
           return extracto.getPrice60ml();
        }

        if(quantity%22 == 0){
           return extracto.getPrice22ml();
        }

        throw new IllegalArgumentException("La cantidad de extracto debe ser un múltiplo de 22, 60, 125, 250, 500 o 1000ml");

    }

    private void modifyInventory(SaleItem saleItem, int userId) {
        if(saleItem.getItemType() == ItemType.COMBO){
            Jar jar = saleItem.getJar();
            Cap cap = saleItem.getCap();
            jar.setQuantity(jar.getQuantity() - saleItem.getQuantity());
            cap.setQuantity(cap.getQuantity() - saleItem.getQuantity());
            jar.setUpdatedAt(LocalDateTime.now());
            cap.setUpdatedAt(LocalDateTime.now());
            jarRepository.save(jar);
            capRepository.save(cap);
            inventoryService.newItem(jar.getId(), "jar", saleItem.getQuantity(), "sale", userId, "Se vendieron " + saleItem.getQuantity() + " del envase en combo: " + jar.getName());
            inventoryService.newItem(cap.getId(), "cap", saleItem.getQuantity(), "sale", userId, "Se vendieron " + saleItem.getQuantity() + " de tapa en combo: " + cap.getName());
        } else if(saleItem.getItemType() == ItemType.JAR){
            Jar jar = saleItem.getJar();
            jar.setQuantity(jar.getQuantity() - saleItem.getQuantity());
            jar.setUpdatedAt(LocalDateTime.now());
            jarRepository.save(jar);
            inventoryService.newItem(jar.getId(), "jar", saleItem.getQuantity(), "sale", userId, "Se vendieron " + saleItem.getQuantity() + " de tarro: " + jar.getName());
        } else if(saleItem.getItemType() == ItemType.CAP){
            Cap cap = saleItem.getCap();
            cap.setQuantity(cap.getQuantity() - saleItem.getQuantity());
            cap.setUpdatedAt(LocalDateTime.now());
            capRepository.save(cap);
            inventoryService.newItem(cap.getId(), "cap", saleItem.getQuantity(), "sale", userId, "Se vendieron " + saleItem.getQuantity() + " de tapa: " + cap.getName());
        } else if (saleItem.getItemType() == ItemType.QUIMICO){
            Quimicos quimico = saleItem.getQuimico();
            quimico.setQuantity(quimico.getQuantity() - saleItem.getQuantity());
            quimicosRepository.save(quimico);
            inventoryService.newItem(
                quimico.getId().longValue(), 
                "quimico", 
                saleItem.getQuantity(), 
                "sale", 
                userId, 
                "Se vendieron " + saleItem.getQuantity() + " de químico: " + quimico.getName()
            );
        } else if (saleItem.getItemType() == ItemType.EXTRACTO){
            Extractos extracto = saleItem.getExtracto();
            extracto.setQuantity(extracto.getQuantity() - saleItem.getQuantity());
            extractosRepository.save(extracto);
            inventoryService.newItem(
                extracto.getId().longValue(), 
                "extracto", 
                saleItem.getQuantity(), 
                "sale", 
                userId, 
                "Se vendieron " + saleItem.getQuantity() + " de extracto: " + extracto.getName()
            );
        }  else {
            throw new IllegalArgumentException("Tipo de item de venta no reconocido: " + saleItem.getItemType());
        }
    }
       
    public List<SaleDTO> getAllSales() {

        List<Sale> sales = saleRepository.findAll();
        List<SaleDTO> saleDTOs = new ArrayList<>();
        for(Sale sale : sales){
            SaleDTO saleDTO = toSaleDTO(sale);
            saleDTOs.add(saleDTO);
        }
        return saleDTOs;
    }

    public List<SaleDTO> getSalesByUser(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if(!userOpt.isPresent()){
            throw new IllegalArgumentException("Usuario no encontrado: " + email);
        }

        List<Sale> sales = saleRepository.findBySeller(userOpt.get().getId());
        List<SaleDTO> saleDTOs = new ArrayList<>();
        for(Sale sale : sales){
            SaleDTO saleDTO = toSaleDTO(sale);
            saleDTOs.add(saleDTO);
        }
        return saleDTOs;
    }

    private SaleDTO toSaleDTO(Sale sale){
         List<SaleItem> saleItems = saleItemRepository.findBySale(sale.getId());
            List<SaleItemDTO> saleItemDTOs = new ArrayList<>();
            for(SaleItem saleItem : saleItems){
                if(saleItem.getItemType() == ItemType.COMBO){
                    saleItemDTOs.add(new SaleItemDTO(comboRepository.findByJarAndCap(saleItem.getJar().getId(), saleItem.getCap().getId()).orElse(null).getName(), saleItem));
                } else if(saleItem.getItemType() == ItemType.JAR){
                    saleItemDTOs.add(new SaleItemDTO(jarRepository.findById(saleItem.getJar().getId()).orElse(null).getName(), saleItem));
                } else if(saleItem.getItemType() == ItemType.CAP){
                    saleItemDTOs.add(new SaleItemDTO(capRepository.findById(saleItem.getCap().getId()).orElse(null).getName(), saleItem));
                }
            }
            SaleDTO saleDTO = new SaleDTO(sale, saleItemDTOs);
            return saleDTO;
    }

}
