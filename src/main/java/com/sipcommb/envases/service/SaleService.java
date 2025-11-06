package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.dto.SaleDTO;
import com.sipcommb.envases.dto.SaleItemDTO;
import com.sipcommb.envases.dto.SaleItemRequest;
import com.sipcommb.envases.dto.SaleRequest;
import com.sipcommb.envases.entity.BodegaCapColor;
import com.sipcommb.envases.entity.BodegaExtractos;
import com.sipcommb.envases.entity.BodegaJar;
import com.sipcommb.envases.entity.BodegaQuimicos;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.CapColor;
import com.sipcommb.envases.entity.Client;
import com.sipcommb.envases.entity.Combo;
import com.sipcommb.envases.entity.Extractos;
import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Quimicos;
import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.CapColorRepository;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.ComboRepository;
import com.sipcommb.envases.repository.ExtractosRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.QuimicosRepository;
import com.sipcommb.envases.repository.SaleItemRepository;
import com.sipcommb.envases.repository.SaleRepository;
import com.sipcommb.envases.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    private JarService jarService;

    @Autowired
    private CapColorService capColorService;

    @Autowired
    private QuimicosService quimicosService;

    @Autowired
    private ExtractosService extractosService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuimicosRepository quimicosRepository;

    @Autowired
    private ExtractosRepository extractosRepository;

    @Autowired
    private CapColorRepository capColorRepository;

    @Autowired
    private PriceService priceService;

    /**
     * Planea o añade un venta en el sistema, dependiendo del parámetro saveSale.
     * 
     * @param saleRequest DTO que contiene los detalles de la venta
     * @param token       Token de autenticación del usuario que realiza la venta,
     *                    se usa para obtener el vendedor
     * @param saveSale    Indica si la venta debe ser guardada en la base de datos
     *                    (true) o solo validada (false)
     * @return SaleDTO que representa la venta.
     */
    public SaleDTO addSale(SaleRequest saleRequest, String token, boolean saveSale) {
        Sale sale = new Sale();

        if (saleRequest.getItems() == null || saleRequest.getItems().size() == 0 || saleRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un item de venta");
        }

        try {
            sale.setPaymentMethod(Sale.PaymentMethod.valueOf(saleRequest.getPaymentMethod().toUpperCase()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Problema al establecer el método de pago: " + e.getMessage());
        }

        Client client = clientService.getClientByName(saleRequest.getClientName());
        sale.setClient(client);
        Optional<User> userOpt = userRepository.findById(jwtService.getUserIdFromToken(token));

        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        sale.setSeller(userOpt.get());
        sale.setNotes(saleRequest.getDescription() != null ? saleRequest.getDescription() : "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        java.time.LocalDate saleDate = java.time.LocalDate.parse(saleRequest.getSaleDate(), formatter);
        sale.setSaleDate(saleDate);
        List<SaleItemRequest> saleItems = saleRequest.getItems();
        List<SaleItem> saleItemList = new ArrayList<>();
        List<SaleItemDTO> saleItemDTOList = new ArrayList<>();
        sale.setTotalAmount(BigDecimal.ZERO);

        if (saveSale) {
            saleRepository.save(sale);
        }

        // crea los sale items usando la lista de saleItemRequests
        // Además, va creado la lista de SaleItemDTOs para el retorno
        for (SaleItemRequest saleItemRequest : saleItems) {
            SaleItem saleItem = checkSaleItems(saleItemRequest, saleItemList);
            int index = checkSaleItemList(saleItemList, saleItem);

            if (index != -1) {
                SaleItem existingItem = saleItemList.get(index);
                existingItem.setQuantity(existingItem.getQuantity() + saleItem.getQuantity());
                existingItem.setSubtotal(
                        existingItem.getUnitPrice().multiply(BigDecimal.valueOf(existingItem.getQuantity())));
                saleItem = existingItem;
                SaleItemDTO existingDTO = saleItemDTOList.get(index);
                existingDTO.setQuantity(saleItem.getQuantity());
                existingDTO.setSubtotal(saleItem.getSubtotal());
                existingDTO.setUnitPrice(saleItem.getUnitPrice());

            } else {
                saleItem.setSale(sale);
                saleItemList.add(saleItem);

                if (saleItem.getItemType() == ItemType.COMBO) {
                    saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getComboName(), saleItem));
                } else if (saleItem.getItemType() == ItemType.JAR) {
                    saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getJarName(), saleItem));
                } else if (saleItem.getItemType() == ItemType.CAP) {
                    saleItemDTOList.add(new SaleItemDTO(
                            saleItemRequest.getCapName() + ' ' + saleItemRequest.getCapColor(), saleItem));
                } else if (saleItem.getItemType() == ItemType.QUIMICO) {
                    saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getQuimicoName(), saleItem));
                } else if (saleItem.getItemType() == ItemType.EXTRACTO) {
                    saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getExtractoName(), saleItem));
                }

            }

        }

        // modifica o verifica el invenvtario, se aprovecha para ir sumando el total de
        // la venta
        for (SaleItem saleItem : saleItemList) {

            if (saveSale) {
                modifyInventory(saleItem, userOpt.get().getId().intValue());
            } else {
                validateInventory(saleItem);
            }

            sale.addPrice(saleItem.getSubtotal());

        }

        // crea la venta y añade a la base de datos si es necesario
        if (saveSale) {
            sale.setCreatedAt(LocalDateTime.now());
            sale.setUpdatedAt(LocalDateTime.now());
            saleRepository.save(sale);
            saleItemRepository.saveAll(saleItemList);
        }

        return new SaleDTO(sale, saleItemDTOList);
    }

    /**
     * Verifica un saleItemRequest y crea un SaleItem correspondiente
     * Un saleItemRequest representa un item de venta y la cantidad solicitada, por
     * ejemplo un el envase A y 5 unidades
     * 
     * @param existingItems   la lista de items ya procesados en la venta actual
     * @param saleItemRequest el DTO de saleItem
     * @return SaleItem el item final que se va a añadir al la base de datos junto a
     *         la venta
     */
    private SaleItem checkSaleItems(SaleItemRequest saleItemRequest, List<SaleItem> existingItems) {

        if (saleItemRequest.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad de venta no puede ser negativa o cero");
        }

        // se revisa que solo se haya especificado un item de venta
        int notNullCount = 0;
        if (saleItemRequest.getComboName() != null)
            notNullCount++;
        if (saleItemRequest.getJarName() != null)
            notNullCount++;
        if (saleItemRequest.getCapName() != null)
            notNullCount++;
        if (saleItemRequest.getQuimicoName() != null)
            notNullCount++;
        if (saleItemRequest.getExtractoName() != null)
            notNullCount++;

        if (notNullCount != 1) {
            throw new IllegalArgumentException("Debe especificar exactamente un item de venta");
        } else if (notNullCount == 0) {
            throw new IllegalArgumentException("Debe especificar al menos un item de venta");
        }

        if (saleItemRequest.getComboName() != null) {
            Combo combo = manageCombo(saleItemRequest.getComboName());
            return createSaleItem(combo, saleItemRequest);
        }

        if (saleItemRequest.getJarName() != null) {
            Jar jar = manageJar(saleItemRequest.getJarName());
            return createSaleItem(jar, saleItemRequest);
        }

        if (saleItemRequest.getQuimicoName() != null) {
            Quimicos quimico = quimicosRepository.findByName(saleItemRequest.getQuimicoName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Químico no encontrado: " + saleItemRequest.getQuimicoName()));
            return createSaleItem(quimico, saleItemRequest);
        }

        if (saleItemRequest.getExtractoName() != null) {
            Extractos extracto = extractosRepository.findByName(saleItemRequest.getExtractoName())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Extracto no encontrado: " + saleItemRequest.getExtractoName()));
            return createSaleItem(extracto, saleItemRequest);
        }

        if ((saleItemRequest.getCapColor() == null || saleItemRequest.getCapColor() == "")
                && (saleItemRequest.getDiameter() == null || saleItemRequest.getDiameter() == "")) {
            throw new IllegalArgumentException("Debe especificar el color y el diametro de la tapa");
        }

        if (saleItemRequest.getCapName() != null) {
            Cap cap = manageCap(saleItemRequest.getCapName(), saleItemRequest.getDiameter());
            return createSaleItem(cap, saleItemRequest);
        }

        throw new IllegalArgumentException("No se pudo determinar el tipo de item de venta");

    }

    /**
     * TODO
     * Crea un SaleItem de tipo Combo basado en el Combo y el SaleItemRequest
     * proporcionados.
     * 
     * @param combo           el Combo del cual se creará el SaleItem
     * @param saleItemRequest el DTO que contiene los detalles del item de venta
     * @return el SaleItem creado
     */
    private SaleItem createSaleItem(Combo combo, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(combo.getJar());

        Cap cap = capRepository.findByNameAndDiameter(saleItemRequest.getCapName(), saleItemRequest.getDiameter())
                .orElseThrow(() -> new IllegalArgumentException("Tapa no encontrada: " + saleItemRequest.getCapName()));

        CapColor capColor = capColorRepository.findByCapAndColor(cap, saleItemRequest.getCapColor())
                .orElseThrow(
                        () -> new IllegalArgumentException("El color no existe en el tipo de tapa: " + cap.getName()));

        saleItem.setCapColor(capColor);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(BigDecimal.valueOf(determinePrice(combo, saleItemRequest)));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(ItemType.COMBO);
        saleItem.setSale(null);
        return saleItem;
    }

    /**
     * Crea un SaleItem de tipo Jar basado en el Jar y el SaleItemRequest
     * proporcionados.
     * 
     * @param jar             el envase del cual se creará el SaleItem
     * @param saleItemRequest el DTO que contiene los detalles del item de venta
     * @return el SaleItem creado
     */
    private SaleItem createSaleItem(Jar jar, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(jar);
        saleItem.setCapColor(null);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(determinePrice(jar, saleItemRequest));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(ItemType.JAR);
        saleItem.setSale(null);
        return saleItem;
    }

    /**
     * Gestiona la validación y obtención de una tapa por su nombre y diámetro.
     * 
     * @param cap             la tapa con la cual se crea el SaleItem
     * @param saleItemRequest el DTO que contiene los detalles del item de venta
     * @return el SaleItem creado
     */
    private SaleItem createSaleItem(Cap cap, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(null);

        CapColor capColor = capColorRepository.findByCapAndColor(cap, saleItemRequest.getCapColor())
                .orElseThrow(
                        () -> new IllegalArgumentException("El color no existe en el tipo de tapa: " + cap.getName()));

        saleItem.setCapColor(capColor);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(determinePrice(cap, saleItemRequest));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(ItemType.CAP);
        saleItem.setSale(null);

        return saleItem;
    }

    /**
     * Crea un SaleItem de tipo Quimico basado en el Quimico y el SaleItemRequest
     * proporcionados.
     * 
     * @param quimico         el Quimico del cual se creará el SaleItem
     * @param saleItemRequest el DTO que contiene los detalles del item de venta
     * @return el SaleItem creado
     */
    private SaleItem createSaleItem(Quimicos quimico, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(null);
        saleItem.setCapColor(null);
        saleItem.setQuimico(quimico);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(quimico.getUnitPrice());
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(ItemType.QUIMICO);
        saleItem.setSale(null);
        return saleItem;
    }

    /**
     * Crea un SaleItem de tipo Extracto basado en el Extracto y el SaleItemRequest
     * proporcionados.
     * 
     * @param extracto        el Extracto del cual se creará el SaleItem
     * @param saleItemRequest el DTO que contiene los detalles del item de venta
     * @return el SaleItem creado
     */
    private SaleItem createSaleItem(Extractos extracto, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();
        saleItem.setJar(null);
        saleItem.setCapColor(null);
        saleItem.setExtracto(extracto);
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(determinePrice(extracto, saleItemRequest));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(ItemType.EXTRACTO);
        saleItem.setSale(null);
        return saleItem;
    }

    /**
     * Verifica si un SaleItem ya existe en la lista de items de venta existentes.
     * Si existe, devuelve el índice del item existente; de lo contrario, devuelve
     * -1
     * 
     * @param existingItems Lista de SaleItem ya existentes
     * @param newItem       Nuevo SaleItem a verificar
     * @return Índice del item existente o -1 si no existe
     */
    private int checkSaleItemList(List<SaleItem> existingItems, SaleItem newItem) {
        for (int i = 0; i < existingItems.size(); i++) {
            SaleItem existingItem = existingItems.get(i);
            if (existingItem.getItemType() == newItem.getItemType()) {
                if (existingItem.getItemType() == ItemType.COMBO) {
                    /*
                     * TODO, dado que aun ni idea como hacer esto bien, lo dejo comentado por ahora
                     * if (existingItem.getJar().getId().equals(newItem.getJar().getId())
                     * && existingItem.getCapColor().getId().equals(newItem.getCapColor().getId()))
                     * {
                     * return i;
                     * }
                     */
                } else if (existingItem.getItemType() == ItemType.JAR) {
                    if (existingItem.getJar().getId().equals(newItem.getJar().getId())) {
                        return i;
                    }
                } else if (existingItem.getItemType() == ItemType.CAP) {
                    if (existingItem.getCapColor().getId().equals(newItem.getCapColor().getId())) {
                        return i;
                    }
                } else if (existingItem.getItemType() == ItemType.QUIMICO) {
                    if (existingItem.getQuimico().getId().equals(newItem.getQuimico().getId())) {
                        return i;
                    }
                } else if (existingItem.getItemType() == ItemType.EXTRACTO) {
                    if (existingItem.getExtracto().getId().equals(newItem.getExtracto().getId())) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Gestiona la validación y obtención de un combo por su nombre.
     * 
     * @param comboName
     * @return Combo válido
     */
    private Combo manageCombo(String comboName) {
        Optional<Combo> comboOpt = comboRepository.findByName(comboName);
        if (!comboOpt.isPresent()) {
            throw new IllegalArgumentException("Combo no encontrado: " + comboName);
        }

        Combo combo = comboOpt.get();

        if (!combo.getActive()) {
            throw new IllegalArgumentException("Combo inactivo: " + comboName);
        }

        return combo;
    }

    /**
     * Gestiona la validación y obtención de un tarro por su nombre.
     * 
     * @param jarName
     * @return Jar válido
     */
    private Jar manageJar(String jarName) {
        Optional<Jar> jarOpt = jarRepository.getByName(jarName);
        if (!jarOpt.isPresent()) {
            throw new IllegalArgumentException("Tarro no encontrado: " + jarName);
        }

        Jar jar = jarOpt.get();

        if (!jar.getIsActive()) {
            throw new IllegalArgumentException("Tarro inactivo: " + jarName);
        }

        return jar;
    }

    /**
     * Gestiona la validación y obtención de una tapa por su nombre y diámetro.
     * 
     * @param capName
     * @param diameter
     * @return Cap válido
     */
    private Cap manageCap(String capName, String diameter) {
        Optional<Cap> capOpt = capRepository.findByNameAndDiameter(capName, diameter);
        if (!capOpt.isPresent()) {
            throw new IllegalArgumentException(
                    "Tapa no encontrada: " + capName + " con diámetro: " + diameter);
        }

        Cap cap = capOpt.get();

        if (!cap.getIsActive()) {
            throw new IllegalArgumentException("Tapa inactiva: " + capName);
        }

        return cap;
    }

    /**
     * Determina el precio unitario basado en la cantidad y los precios disponibles
     * del combo.
     * 
     * @param combo           el combo del cual se va a determinar el precio
     * @param saleItemRequest el DTO que contiene los detalles del item de venta
     * @return el precio unitario determinado
     */
    private double determinePrice(Combo combo, SaleItemRequest saleItemRequest) {
        if (combo.getCienPrice() != null && combo.getCienPrice() > 0 && saleItemRequest.getQuantity() >= 100) {
            return combo.getCienPrice();
        }

        if ((combo.getDocenaPrice() != null || combo.getDocenaPrice() > 0)
                && (saleItemRequest.getQuantity() >= 12 || saleItemRequest.getQuantity() % 12 == 0)) {
            return combo.getDocenaPrice();
        }

        return combo.getUnitPrice();

    }

    /**
     * Determina el precio unitario basado en la cantidad y los precios disponibles
     * del tarro.
     * 
     * @param jar             el tarro del cual se va a determinar el precio
     * @param saleItemRequest el DTO que contiene los detalles del item a vender
     * @return el precio unitario determinado
     */
    private BigDecimal determinePrice(Jar jar, SaleItemRequest saleItemRequest) {
        if (saleItemRequest.getQuantity() == jar.getUnitsInPaca()
                && (jar.getPacaPrice() != null && jar.getPacaPrice().compareTo(
                        BigDecimal.ZERO) > 0)) {
            return jar.getPacaPrice();
        }

        if (jar.getCienPrice() != null && jar.getCienPrice().compareTo(BigDecimal.ZERO) > 0
                && saleItemRequest.getQuantity() >= 100) {
            return jar.getCienPrice();
        }

        if ((jar.getDocenaPrice() != null && jar.getDocenaPrice().compareTo(BigDecimal.ZERO) > 0)
                && (saleItemRequest.getQuantity() >= 12 || saleItemRequest.getQuantity() % 12 == 0)) {
            return jar.getDocenaPrice();
        }

        return jar.getUnitPrice();

    }

    /**
     * Determina el precio unitario basado en la cantidad y los precios disponibles
     * de la tapa.
     * 
     * @param cap             la tapa del cual se va a determinar el precio
     * @param saleItemRequest el DTO que contiene los detalles del item a vender
     * @return el precio unitario determinado
     */
    private BigDecimal determinePrice(Cap cap, SaleItemRequest saleItemRequest) {
        CapColor capColor = capColorRepository.findByCapAndColor(cap, saleItemRequest.getCapColor())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El color " + saleItemRequest.getCapColor() + " no existe en el tipo de tapa: "
                                + cap.getName()));

        if (saleItemRequest.getQuantity() == capColor.getUnits_in_paca()) {
            return BigDecimal.valueOf(capColor.getPaca_price());
        }

        if (capColor.getCien_price() != null && capColor.getCien_price() > 0
                && saleItemRequest.getQuantity() >= 100) {
            return BigDecimal.valueOf(capColor.getCien_price());
        }

        if ((capColor.getDocena_price() != null && capColor.getDocena_price() > 0)
                && (saleItemRequest.getQuantity() >= 12 || saleItemRequest.getQuantity() % 12 == 0)) {
            return BigDecimal.valueOf(capColor.getDocena_price());
        }

        return BigDecimal.valueOf(capColor.getUnit_price());

    }

    /**
     * Determina el precio unitario basado en la cantidad y los precios disponibles
     * del extracto.
     * 
     * @param extracto        el extracto del cual se va a determinar el precio
     * @param saleItemRequest el DTO que contiene los detalles del item a vender
     * @return el precio unitario determinado
     */
    private BigDecimal determinePrice(Extractos extracto, SaleItemRequest saleItemRequest) {
        int quantity = saleItemRequest.getQuantity();

        if (quantity < 22) {
            throw new IllegalArgumentException("La cantidad de extracto debe ser al menos 22ml");
        }

        if (quantity % 1000 == 0) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 1000);
            return extracto.getPrice1000ml();
        }

        if (quantity % 500 == 0) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 500);
            return extracto.getPrice500ml();
        }

        if (quantity % 250 == 0) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 250);
            return extracto.getPrice250ml();
        }

        if (quantity % 125 == 0) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 125);
            return extracto.getPrice125ml();
        }

        if (quantity % 60 == 0) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 60);
            return extracto.getPrice60ml();
        }

        if (quantity % 22 == 0) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 22);
            return extracto.getPrice22ml();
        }

        throw new IllegalArgumentException(
                "La cantidad de extracto debe ser un múltiplo de 22, 60, 125, 250, 500 o 1000ml");

    }

    /**
     * Se usa para planear la venta, hace la validación del inventario sin
     * modificarlo
     * 
     * @param saleItem el item de venta a validar
     */
    private void validateInventory(SaleItem saleItem) {
        // TODO , no se como resolver el tema de bodegas aun
        if (saleItem.getItemType() == ItemType.COMBO) {

        } else if (saleItem.getItemType() == ItemType.JAR) {
            Jar jar = saleItem.getJar();
            List<BodegaJar> bodegaJar = jarService.sortBodegaJar(jar.getBodegas());

            int quantityToDeduct = saleItem.getQuantity();

            for (BodegaJar bj : bodegaJar) {
                if (quantityToDeduct <= 0) {
                    break;
                }

                int availableInBodega = bj.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, quantityToDeduct);
                quantityToDeduct -= deductQuantity;
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException("No hay suficiente inventario para el tarro: " + jar.getName()
                        + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else if (saleItem.getItemType() == ItemType.CAP) {
            CapColor capColor = saleItem.getCapColor();
            List<BodegaCapColor> bodegaCapColors = capColorService.sortBodegas(capColor.getBodegas());

            int quantityToDeduct = saleItem.getQuantity();

            for (BodegaCapColor bodegaCapColor : bodegaCapColors) {
                if (quantityToDeduct <= 0) {
                    break;
                }

                int availableInBodega = bodegaCapColor.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, quantityToDeduct);
                quantityToDeduct -= deductQuantity;
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException(
                        "No hay suficiente inventario para la tapa: " + capColor.getCap().getName() + " color "
                                + capColor.getColor() + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else if (saleItem.getItemType() == ItemType.QUIMICO) {
            Quimicos quimico = saleItem.getQuimico();
            List<BodegaQuimicos> bodegaQuimicos = quimicosService.sortBodegaQuimicos(quimico.getBodegas());
            int quantityToDeduct = saleItem.getQuantity();

            for (BodegaQuimicos bq : bodegaQuimicos) {
                if (quantityToDeduct <= 0) {
                    break;
                }

                int availableInBodega = bq.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, quantityToDeduct);
                quantityToDeduct -= deductQuantity;
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException("No hay suficiente inventario para el químico: " + quimico.getName()
                        + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else if (saleItem.getItemType() == ItemType.EXTRACTO) {

            Extractos extracto = saleItem.getExtracto();
            List<BodegaExtractos> bodegaExtractos = extractosService.sortBodegaExtractos(extracto.getBodegas());

            int quantityToDeduct = saleItem.getQuantity();

            for (BodegaExtractos be : bodegaExtractos) {
                if (quantityToDeduct <= 0) {
                    break;
                }

                int availableInBodega = be.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, quantityToDeduct);
                quantityToDeduct -= deductQuantity;
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException("No hay suficiente inventario para el extracto: "
                        + extracto.getName() + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else {
            throw new IllegalArgumentException("Tipo de item de venta no reconocido: " + saleItem.getItemType());
        }
    }

    /**
     * Modifica el inventario restando las cantidades vendidas.
     * 
     * @param saleItem el item de venta que se va a procesar
     * @param userId   el ID del usuario que realiza la venta
     */
    private void modifyInventory(SaleItem saleItem, int userId) {
        // TODO, no se como resolver el tema de bodegas aun
        if (saleItem.getItemType() == ItemType.COMBO) {

        } else if (saleItem.getItemType() == ItemType.JAR) {
            Jar jar = saleItem.getJar();
            List<BodegaJar> bodegaJar = jarService.sortBodegaJar(jar.getBodegas());

            int quantityToDeduct = saleItem.getQuantity();

            for (BodegaJar bj : bodegaJar) {
                if (quantityToDeduct <= 0) {
                    break;
                }

                int availableInBodega = bj.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, quantityToDeduct);
                bj.setQuantity(availableInBodega - deductQuantity);
                quantityToDeduct -= deductQuantity;
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException("No hay suficiente inventario para el tarro: " + jar.getName()
                        + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else if (saleItem.getItemType() == ItemType.CAP) {
            CapColor capColor = saleItem.getCapColor();
            List<BodegaCapColor> bodegaCapColors = capColorService.sortBodegas(capColor.getBodegas());

            int quantityToDeduct = saleItem.getQuantity();

            for (BodegaCapColor bodegaCapColor : bodegaCapColors) {
                if (quantityToDeduct <= 0) {
                    break;
                }

                int availableInBodega = bodegaCapColor.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, quantityToDeduct);
                bodegaCapColor.setQuantity(availableInBodega - deductQuantity);
                quantityToDeduct -= deductQuantity;
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException(
                        "No hay suficiente inventario para la tapa: " + capColor.getCap().getName() + " color "
                                + capColor.getColor() + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else if (saleItem.getItemType() == ItemType.QUIMICO) {
            Quimicos quimico = saleItem.getQuimico();
            List<BodegaQuimicos> bodegaQuimicos = quimicosService.sortBodegaQuimicos(quimico.getBodegas());
            int quantityToDeduct = saleItem.getQuantity();

            for (BodegaQuimicos bq : bodegaQuimicos) {
                if (quantityToDeduct <= 0) {
                    break;
                }

                int availableInBodega = bq.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, quantityToDeduct);
                bq.setQuantity(availableInBodega - deductQuantity);
                quantityToDeduct -= deductQuantity;
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException("No hay suficiente inventario para el químico: " + quimico.getName()
                        + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else if (saleItem.getItemType() == ItemType.EXTRACTO) {

            Extractos extracto = saleItem.getExtracto();
            List<BodegaExtractos> bodegaExtractos = extractosService.sortBodegaExtractos(extracto.getBodegas());

            int quantityToDeduct = saleItem.getQuantity();

            for (BodegaExtractos be : bodegaExtractos) {
                if (quantityToDeduct <= 0) {
                    break;
                }

                int availableInBodega = be.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, quantityToDeduct);
                be.setQuantity(availableInBodega - deductQuantity);
                quantityToDeduct -= deductQuantity;
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException("No hay suficiente inventario para el extracto: "
                        + extracto.getName() + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else {
            throw new IllegalArgumentException("Tipo de item de venta no reconocido: " + saleItem.getItemType());
        }

    }

    /**
     * Obtiene todas las ventas en formato paginado.
     * 
     * @param pageable la información de paginación
     * @return una página de SaleDTO que representa las ventas
     */
    public Page<SaleDTO> getAllSales(Pageable pageable) {

        Page<Sale> sales = saleRepository.findAll(pageable);
        List<SaleDTO> saleDTOs = new ArrayList<>();
        for (Sale sale : sales) {
            SaleDTO saleDTO = toSaleDTO(sale);
            saleDTOs.add(saleDTO);
        }
        return new PageImpl<>(saleDTOs, pageable, sales.getTotalElements());
    }

    /**
     * Obtiene las ventas filtradas por rango de fechas y nombre de vendedor en
     * formato paginado.
     * 
     * @param fechaInicioStr la fecha de inicio en formato "yyyy-MM-dd"
     * @param fechaFinStr    la fecha de fin en formato "yyyy-MM-dd"
     * @param nombreUsuario  el nombre del vendedor
     * @param pageable       la información de paginación
     * @return una página de SaleDTO que representa las ventas filtradas
     */
    public Page<SaleDTO> getFindByFechaAndVendedor(String fechaInicioStr, String fechaFinStr, String nombreUsuario,
            Pageable pageable) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate fechaInicio = LocalDate.parse(fechaInicioStr, formatter);
        LocalDate fechaFin = LocalDate.parse(fechaFinStr, formatter);
        Page<Sale> sales = saleRepository.findByFechaAndVendedor(fechaInicio, fechaFin, nombreUsuario, pageable);
        List<SaleDTO> saleDTOs = new ArrayList<>();
        for (Sale sale : sales) {
            SaleDTO saleDTO = toSaleDTO(sale);
            saleDTOs.add(saleDTO);
        }
        return new PageImpl<>(saleDTOs, pageable, sales.getTotalElements());
    }

    /**
     * Obtiene el monto total de ventas filtradas por rango de fechas y nombre de
     * vendedor.
     * 
     * @param fechaInicioStr la fecha de inicio en formato "yyyy-MM-dd"
     * @param fechaFinStr    la fecha de fin en formato "yyyy-MM-dd"
     * @param nombreUsuario  el nombre del vendedor
     * @return el monto total de ventas como BigDecimal
     */
    public BigDecimal getTotalAmountByFechaAndVendedor(String fechaInicioStr, String fechaFinStr,
            String nombreUsuario) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate fechaInicio = LocalDate.parse(fechaInicioStr, formatter);
        LocalDate fechaFin = LocalDate.parse(fechaFinStr, formatter);
        BigDecimal totalAmount = saleRepository.findByFechaAndVendedorTotal(fechaInicio, fechaFin,
                nombreUsuario);
        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    /**
     * Obtiene las ventas realizadas por un usuario específico, identificado por su
     * correo electrónico, en formato paginado.
     * 
     * @param email    el correo electrónico del usuario
     * @param pageable la información de paginación
     * @return una página de SaleDTO que representa las ventas del usuario
     */
    public Page<SaleDTO> getSalesByEmail(String email, Pageable pageable) {
        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado: " + email);
        }

        List<Sale> sales = saleRepository.findBySeller(userOpt.get().getId());
        List<SaleDTO> saleDTOs = new ArrayList<>();
        for (Sale sale : sales) {
            SaleDTO saleDTO = toSaleDTO(sale);
            saleDTOs.add(saleDTO);
        }
        return new PageImpl<>(saleDTOs, pageable, sales.size());
    }

    /**
     * Obtiene las ventas realizadas por un usuario específico, identificado por su
     * nombre de usuario, en formato paginado.
     * 
     * @param username el nombre de usuario del usuario
     * @param pageable la información de paginación
     * @return una página de SaleDTO que representa las ventas del usuario
     */
    public Page<SaleDTO> getSalesByUsername(String username, Pageable pageable) {
        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado: " + username);
        }

        List<Sale> sales = saleRepository.findBySeller(userOpt.get().getId());
        List<SaleDTO> saleDTOs = new ArrayList<>();
        for (Sale sale : sales) {
            SaleDTO saleDTO = toSaleDTO(sale);
            saleDTOs.add(saleDTO);
        }
        return new PageImpl<>(saleDTOs, pageable, sales.size());
    }

    /**
     * Convierte una entidad Sale en un DTO SaleDTO, incluyendo sus items de venta.
     * 
     * @param sale la entidad Sale a convertir
     * @return el DTO SaleDTO correspondiente
     */
    private SaleDTO toSaleDTO(Sale sale) {
        List<SaleItem> saleItems = saleItemRepository.findBySale(sale.getId());
        List<SaleItemDTO> saleItemDTOs = new ArrayList<>();
        for (SaleItem saleItem : saleItems) {
            if (saleItem.getItemType() == ItemType.COMBO) {
                /*
                 * TODO no funiona
                 * saleItemDTOs.add(new SaleItemDTO(comboRepository
                 * .findByJarAndCap(saleItem.getJar().getId(),
                 * saleItem.getCapColor().getId()).orElse(null).getName(),
                 * saleItem));
                 */
            } else if (saleItem.getItemType() == ItemType.JAR) {
                saleItemDTOs.add(new SaleItemDTO(
                        jarRepository.findById(saleItem.getJar().getId()).orElse(null).getName(), saleItem));
            } else if (saleItem.getItemType() == ItemType.CAP) {
                saleItemDTOs.add(new SaleItemDTO(
                        capColorRepository.findById(saleItem.getCapColor().getId()).orElse(null).getCap().getName(),
                        saleItem, saleItem.getCapColor().getColor()));
            } else if (saleItem.getItemType() == ItemType.QUIMICO) {
                saleItemDTOs.add(new SaleItemDTO(
                        quimicosRepository.findById(saleItem.getQuimico().getId()).orElse(null).getName(), saleItem));
            } else if (saleItem.getItemType() == ItemType.EXTRACTO) {
                saleItemDTOs.add(new SaleItemDTO(
                        extractosRepository.findById(saleItem.getExtracto().getId()).orElse(null).getName(), saleItem));
            }
        }
        SaleDTO saleDTO = new SaleDTO(sale, saleItemDTOs);
        return saleDTO;
    }

    /**
     * Obtiene las ventas filtradas por rango de precios en formato paginado.
     * 
     * @param priceRangeRequest el DTO que contiene los detalles del rango de
     *                          precios
     * @param pageable          la información de paginación
     * @return una página de SaleDTO que representa las ventas filtradas por precio
     */
    public Page<SaleDTO> getPriceRange(PriceSearchRequest priceRangeRequest, Pageable pageable) {
        boolean exactSearch = priceService.verifyPriceSearchRequest(priceRangeRequest);

        if (exactSearch) {
            return saleRepository.findByExactPrice(priceRangeRequest.getExactPrice(), pageable)
                    .map(sale -> toSaleDTO(sale));
        } else {
            return saleRepository
                    .findByPriceBetween(priceRangeRequest.getMinPrice(), priceRangeRequest.getMaxPrice(), pageable)
                    .map(sale -> toSaleDTO(sale));
        }
    }

}
