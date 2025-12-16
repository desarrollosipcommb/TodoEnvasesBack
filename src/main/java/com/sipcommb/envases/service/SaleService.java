package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.BodegaSaleDTO;
import com.sipcommb.envases.dto.ClientDTO;
import com.sipcommb.envases.dto.ComboCapOrderDTO;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.dto.SaleDTO;
import com.sipcommb.envases.dto.SaleItemDTO;
import com.sipcommb.envases.dto.SaleItemRequest;
import com.sipcommb.envases.dto.SaleRequest;
import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaCapColor;
import com.sipcommb.envases.entity.BodegaExtractos;
import com.sipcommb.envases.entity.BodegaJar;
import com.sipcommb.envases.entity.BodegaQuimicos;
import com.sipcommb.envases.entity.BodegaSale;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.CapColor;
import com.sipcommb.envases.entity.Client;
import com.sipcommb.envases.entity.Combo;
import com.sipcommb.envases.entity.ComboCap;
import com.sipcommb.envases.entity.ComboItemOrder;
import com.sipcommb.envases.entity.Extractos;
import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.Quimicos;
import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.entity.User;
import com.sipcommb.envases.repository.BodegaCapColorRepository;
import com.sipcommb.envases.repository.BodegaExtractoRepository;
import com.sipcommb.envases.repository.BodegaJarRepository;
import com.sipcommb.envases.repository.BodegaQuimicoRepository;
import com.sipcommb.envases.repository.BodegaRepository;
import com.sipcommb.envases.repository.BodegaSaleRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private BodegaCapColorRepository bodegaCapColorRepository;

    @Autowired
    private BodegaExtractoRepository bodegaExtractoRepository;

    @Autowired
    private BodegaJarRepository bodegaJarRepository;

    @Autowired
    private BodegaQuimicoRepository bodegaQuimicoRepository;

    @Autowired
    private BodegaSaleRepository bodegaSaleRepository;

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
    public SaleDTO addSale(SaleRequest saleRequest, String token, boolean saveSale, String... args) {
        Sale sale = new Sale();

        if (saleRequest.getItems() == null || saleRequest.getItems().size() == 0 || saleRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un item de venta");
        }

        try {
            sale.setPaymentMethod(Sale.PaymentMethod.valueOf(saleRequest.getPaymentMethod().toUpperCase()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Problema al establecer el método de pago: " + e.getMessage());
        }

        if (saleRequest.getType() == null) {
            sale.setType(com.sipcommb.envases.entity.SaleType.PUNTO_DE_VENTA);
        } else if (saleRequest.getType().equalsIgnoreCase("DOMICILIO")) {
            sale.setType(com.sipcommb.envases.entity.SaleType.DOMICILIO);
        } else if (saleRequest.getType().equalsIgnoreCase("PUNTO_DE_VENTA")) {
            sale.setType(com.sipcommb.envases.entity.SaleType.PUNTO_DE_VENTA);
        } else if (!saveSale) {
            sale.setType(com.sipcommb.envases.entity.SaleType.PUNTO_DE_VENTA);
        } else {
            throw new IllegalArgumentException("Tipo de venta no reconocido: " + saleRequest.getType());
        }

        if (saleRequest.getDescription() == null) {
            saleRequest.setDescription("");
        } else {
            saleRequest.setDescription(saleRequest.getDescription().trim());
        }

        sale.setNotes(saleRequest.getDescription());

        if (!saveSale) {
            if (saleRequest.getClientDocument() == null) {
                sale.setClient(new Client(
                        "nombre de prueba",
                        "direccion de prueba",
                        "telefono de prueba",
                        "descripcion de prueba",
                        "0"));
                // return new SaleDTO(sale, new ArrayList<>());
            } else {
                Optional<Client> client = clientService.findClientByDocument(saleRequest.getClientDocument());
                if (client.isPresent()) {
                    sale.setClient(client.get());
                } else {
                    sale.setClient(new Client(
                            "nombre de prueba",
                            "direccion de prueba",
                            "telefono de prueba",
                            "descripcion de prueba",
                            "0"));
                }
            }
        } else {
            Optional<Client> client = clientService.findClientByDocument(saleRequest.getClientDocument());
            if (client.isPresent()) {
                sale.setClient(client.get());
            } else {
                String document = saleRequest.getClientDocument();
                if (args.length < 3) {
                    throw new IllegalArgumentException("El cliente con documento " + saleRequest.getClientDocument()
                            + " no existe. Proporcione los datos necesarios para crearlo automáticamente.");
                }

                if (document == null || document.isEmpty() || document == "") {
                    throw new IllegalArgumentException(
                            "El documento del cliente no puede estar vacío para la creación automática.");
                }

                if (args[0] == null || args[0].isEmpty() || args[0] == "") {
                    throw new IllegalArgumentException(
                            "El nombre del cliente no puede estar vacío para la creación automática.");
                }

                ClientDTO newClientDTO = new ClientDTO(
                        args[0], // name
                        args[2], // address
                        args[1], // phone
                        "Cliente agregado automáticamente al crear la venta", // description
                        true, // isActive
                        document // document
                );
                clientService.addClient(newClientDTO);
                Client newClient = clientService.getClientByDocument(saleRequest.getClientDocument());
                sale.setClient(newClient);
            }
        }

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

        // crea los sale items usando la lista de saleItemRequests
        // Además, va creado la lista de SaleItemDTOs para el retorno
        for (SaleItemRequest saleItemRequest : saleItems) {
            SaleItem saleItem = checkSaleItems(saleItemRequest);
            int index = checkSaleItemList(saleItemList, saleItem);

            if (index != -1) {
                // si el item ya existe, sacamos el existente de la lista
                SaleItem existingItem = saleItemList.get(index);

                // aca sumamos las cantidades
                existingItem.setQuantity(existingItem.getQuantity() + saleItem.getQuantity());

                // añadimos la cantidad del request para recalcular el subtotal
                saleItemRequest.setQuantity(existingItem.getQuantity());

                // llamamos este metodo de nuevo, porque este es el que nos dice que precio
                // unitario usar
                SaleItem modifiedItem = checkSaleItems(saleItemRequest);

                // sacamos el precio unitario y subtotal recalculados
                existingItem.setUnitPrice(modifiedItem.getUnitPrice());
                existingItem.setSubtotal(modifiedItem.getSubtotal());

                // reemplazamos el saleItem por el existente modificado
                saleItem = existingItem;

                // creamos el DTO correspondiente, que va a ser lo que vamos a devolver
                SaleItemDTO existingDTO = saleItemDTOList.get(index);

                existingDTO.setQuantity(saleItem.getQuantity());
                existingDTO.setSubtotal(saleItem.getSubtotal());
                existingDTO.setUnitPrice(saleItem.getUnitPrice());

            } else {
                if (saleItem.getItemType() == ItemType.COMBO) {
                    checkComboSaleItemList(saleItemList, saleItem, saleItemDTOList);
                }

                saleItem.setSale(sale);
                saleItemList.add(saleItem);

                if (saleItem.getItemType() == ItemType.COMBO) {
                    saleItemDTOList.add(
                            new SaleItemDTO(saleItemRequest.getComboName(), saleItem, saleItemRequest.getCapColor()));
                } else if (saleItem.getItemType() == ItemType.JAR) {
                    saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getJarName(), saleItem));
                } else if (saleItem.getItemType() == ItemType.CAP) {
                    saleItemDTOList.add(new SaleItemDTO(
                            saleItemRequest.getCapName() + ' ' + saleItemRequest.getCapColor(), saleItem,
                            saleItemRequest.getCapColor()));
                } else if (saleItem.getItemType() == ItemType.QUIMICO) {
                    saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getQuimicoName(), saleItem));
                } else if (saleItem.getItemType() == ItemType.EXTRACTO) {
                    saleItemDTOList.add(new SaleItemDTO(saleItemRequest.getExtractoName(), saleItem));
                }

            }

        }

        // modifica o verifica el invenvtario, se aprovecha para ir sumando el total de
        // la venta
        List<BodegaSale> bodegaSales = new ArrayList<>();
        for(int c=0; c<saleItemList.size(); c++) {
            SaleItem si = saleItemList.get(c);
            
            if(saveSale){
                bodegaSales.addAll(modifyInventory(si, saleItemList, userOpt.get().getId().intValue()));
            }else{
                bodegaSales.addAll(validateInventory(si, saleItemList));
            }
            sale.addPrice(si.getSubtotal());
        }

        bodegaSales = consolidateList(bodegaSales);

        List<BodegaSaleDTO> bodegaSaleDTOs = new ArrayList<>();
        for(BodegaSale bs : bodegaSales){
            bs.setSale(sale);
            bodegaSaleDTOs.add(new BodegaSaleDTO(bs));
        }

        sale.setBodegaSales(bodegaSales);
        

        /* 
        for (SaleItem saleItem : saleItemList) {

            if (saveSale) {
                modifyInventory(saleItem, saleItemList, userOpt.get().getId().intValue());
            } else {
                validateInventory(saleItem, saleItemList);
            }

            sale.addPrice(saleItem.getSubtotal());

        }
            */

        // crea la venta y añade a la base de datos si es necesario
        if (saveSale) {
            sale.setCreatedAt(LocalDateTime.now());
            sale.setUpdatedAt(LocalDateTime.now());
            saleRepository.save(sale);
            saleItemRepository.saveAll(saleItemList);
        }

        return new SaleDTO(sale, saleItemDTOList, bodegaSaleDTOs);
    }

    /**
     * Verifica un saleItemRequest y crea un SaleItem correspondiente
     * Un saleItemRequest representa un item de venta y la cantidad solicitada, por
     * ejemplo un el envase A y 5 unidades
     *
     * @param saleItemRequest el DTO de saleItem
     * @return SaleItem el item final que se va a añadir al la base de datos junto a
     *         la venta
     */
    private SaleItem checkSaleItems(SaleItemRequest saleItemRequest) {

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

        if (saleItemRequest.getCapName() != null) {
            Cap cap = manageCap(saleItemRequest.getCapName());
            return createSaleItem(cap, saleItemRequest);
        }

        throw new IllegalArgumentException("No se pudo determinar el tipo de item de venta");

    }

    /**
     * Crea un SaleItem de tipo Combo basado en el Combo y el SaleItemRequest
     * proporcionados.
     *
     * @param combo           el Combo del cual se creará el SaleItem
     * @param saleItemRequest el DTO que contiene los detalles del item de venta
     * @return el SaleItem creado
     */
    private SaleItem createSaleItem(Combo combo, SaleItemRequest saleItemRequest) {
        SaleItem saleItem = new SaleItem();

        saleItem.setCombo(combo);
        saleItem.setComboItemOrder(
                createComboItemOrders(saleItemRequest, saleItem));
        saleItem.setQuantity(saleItemRequest.getQuantity());
        saleItem.setUnitPrice(BigDecimal.valueOf(determinePrice(combo, saleItemRequest)));
        saleItem.setSubtotal(saleItem.getUnitPrice().multiply(BigDecimal.valueOf(saleItemRequest.getQuantity())));
        saleItem.setItemType(ItemType.COMBO);
        saleItem.setSale(null);
        return saleItem;
    }

    private List<ComboItemOrder> createComboItemOrders(SaleItemRequest saleItemRequest, SaleItem saleItem) {
        List<ComboItemOrder> comboItemOrders = new ArrayList<>();

        for (ComboCapOrderDTO comboCapOrderDTO : saleItemRequest.getComboCapOrderDTO()) {
            ComboItemOrder comboItemOrder = new ComboItemOrder();
            Optional<Cap> capOpt = capRepository.findByName(comboCapOrderDTO.getCapName().trim());

            if (!capOpt.isPresent()) {
                throw new IllegalArgumentException(
                        "La tapa " + comboCapOrderDTO.getCapName() + " no existe en el sistema");
            }

            Optional<CapColor> capColorOpt = capColorRepository.findByCapAndColor(
                    capOpt.get(),
                    comboCapOrderDTO.getColor().trim());

            if (!capColorOpt.isPresent()) {
                throw new IllegalArgumentException("El color " + comboCapOrderDTO.getColor()
                        + " no existe para la tapa " + comboCapOrderDTO.getCapName());
            }

            comboItemOrder.setColor(capColorOpt.get());
            comboItemOrder.setQuantity(comboCapOrderDTO.getQuantity());
            comboItemOrder.setSaleItem(saleItem);
            comboItemOrders.add(comboItemOrder);
        }

        return comboItemOrders;
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
                    break;
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
     * Verifica y actualiza los precios unitarios y subtotales de los SaleItems de
     * tipo COMBO en la lista existente, basándose en la cantidad total vendida.
     * Hace lo mismo que checkItemSaleList pero actualiza los precios de los combos
     * ya existentes en la lista
     * No junta los combos en uno solo, solo actualiza los precios, dado que los
     * combos pueden tener colores de tapa diferentes
     *
     * @param existingItems Lista de SaleItem ya existentes
     * @param newItem       Nuevo SaleItem a agregar
     * @param existingDTOs  Lista de SaleItemDTO ya existentes
     */
    private void checkComboSaleItemList(List<SaleItem> existingItems, SaleItem newItem,
            List<SaleItemDTO> existingDTOs) {

        int totalQuantity = newItem.getQuantity();

        for (SaleItem item : existingItems) {
            if (item.getItemType() == ItemType.COMBO && item.getCombo().getId().equals(newItem.getCombo().getId())) {
                totalQuantity += item.getQuantity();
            }
        }

        double unitPrice = determinePrice(newItem.getCombo(), totalQuantity);

        for (int i = 0; i < existingItems.size(); i++) {
            SaleItem item = existingItems.get(i);
            if (item.getItemType() == ItemType.COMBO && item.getCombo().getId().equals(newItem.getCombo().getId())) {
                item.setUnitPrice(BigDecimal.valueOf(unitPrice));
                item.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                existingItems.set(i, item);
                // Actualizar también el DTO correspondiente
                SaleItemDTO dto = existingDTOs.get(i);
                dto.setUnitPrice(item.getUnitPrice());
                dto.setSubtotal(item.getSubtotal());
                existingDTOs.set(i, dto);
            }
        }

        newItem.setUnitPrice(BigDecimal.valueOf(unitPrice));
        newItem.setSubtotal(newItem.getUnitPrice().multiply(BigDecimal.valueOf(newItem.getQuantity())));
        // return newItem;
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
    private Cap manageCap(String capName) {
        Optional<Cap> capOpt = capRepository.findByName(capName);
        if (!capOpt.isPresent()) {
            throw new IllegalArgumentException(
                    "Tapa no encontrada: " + capName);
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
        if (saleItemRequest.getQuantity() == combo.getUnitsInPaca()
                && (combo.getPacaPrice() != null && combo.getPacaPrice() > 0)) {
            return combo.getPacaPrice();
        }

        if (combo.getCienPrice() != null && combo.getCienPrice() > 0 && saleItemRequest.getQuantity() >= 100) {
            return combo.getCienPrice();
        }

        if ((combo.getDocenaPrice() != null || combo.getDocenaPrice() > 0)
                && (saleItemRequest.getQuantity() >= 12 || saleItemRequest.getQuantity() % 12 == 0)) {
            return combo.getDocenaPrice();
        }

        return combo.getUnitPrice();

    }

    private double determinePrice(Combo combo, int totalQuantity) {
        if (totalQuantity == combo.getUnitsInPaca()
                && (combo.getPacaPrice() != null && combo.getPacaPrice() > 0)) {
            return combo.getPacaPrice();
        }

        if (combo.getCienPrice() != null && combo.getCienPrice() > 0 && totalQuantity >= 100) {
            return combo.getCienPrice();
        }

        if ((combo.getDocenaPrice() != null || combo.getDocenaPrice() > 0)
                && (totalQuantity >= 12 || totalQuantity % 12 == 0)) {
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

        if (quantity >= 1000) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 1000);
            return extracto.getPrice1000ml();
        }

        if (quantity >= 500) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 500);
            return extracto.getPrice500ml();
        }

        if (quantity >= 250) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 250);
            return extracto.getPrice250ml();
        }

        if (quantity >= 125) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 125);
            return extracto.getPrice125ml();
        }

        if (quantity >= 60) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 60);
            return extracto.getPrice60ml();
        }

        if (quantity >= 22) {
            saleItemRequest.setQuantity(saleItemRequest.getQuantity() / 22);
            return extracto.getPrice22ml();
        }

        throw new IllegalArgumentException("La cantidad de extracto debe ser al menos 22ml");
    }

    /**
     * Se usa para planear la venta, hace la validación del inventario sin
     * modificarlo
     *
     * @param saleItem el item de venta a validar
     */
    private List<BodegaSale> validateInventory(SaleItem saleItem, List<SaleItem> existingItems) {
        List<BodegaSale> bodegaSales = new ArrayList<>();
        if (saleItem.getItemType() == ItemType.COMBO) {
            bodegaSales = validateInventoryCombo(saleItem, existingItems, bodegaSales);
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
                bodegaSales.add(new BodegaSale(bj.getBodega(), null, jar.getName(), deductQuantity));
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
                bodegaSales.add(new BodegaSale(bodegaCapColor.getBodega(), null, capColor.getCap().getName()+ " "+capColor.getColor(), deductQuantity));
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
                bodegaSales.add(new BodegaSale(bq.getBodega(), null, quimico.getName(), deductQuantity));
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
                bodegaSales.add(new BodegaSale(be.getBodega(), null, extracto.getName(), deductQuantity));
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException("No hay suficiente inventario para el extracto: "
                        + extracto.getName() + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else {
            throw new IllegalArgumentException("Tipo de item de venta no reconocido: " + saleItem.getItemType());
        }

        return bodegaSales;
    }

    /**
     * Valida el inventario para un SaleItem de tipo COMBO sin modificarlo.
     * Dado que estos tiene 1 envase y 1 o más tapas, tenemos que validar que
     * tengamos inventario para todos los componentes
     *
     * @param saleItem      el Combo a vender
     * @param existingItems los items ya existentes en la venta
     */
    private List<BodegaSale> validateInventoryCombo(SaleItem saleItem, List<SaleItem> existingItems, List<BodegaSale> bodegaSales) {

        // Sacamos el combo del saleItem para luego sacar el tarro y las tapas
        Combo combo = saleItem.getCombo();

        // Sacamos el envase para validar su inventario
        Jar jar = combo.getJar();

        // Sacamos todas las bodegas donde tengamos este envase y las ordenamos segun su
        // prioridad
        List<BodegaJar> bodegaJar = jarService.sortBodegaJar(jar.getBodegas());

        // Inventario inicial que necesitamos
        int requestedQuantity = saleItem.getQuantity();

        // Sumar cantidades de combos existentes en la lista
        // Es importante resaltar que este revisa si hay suficiente inventario para EL +
        // el existente, si ya se ha pedido el mismo envase en la venta
        for (SaleItem item : existingItems) {
            if (item.getItemType() == ItemType.JAR && item.getJar().getId().equals(jar.getId())) {
                requestedQuantity += item.getQuantity();
            }
        }

        // Recorremos las bodegas para ver si tenemos suficiente inventario
        for (BodegaJar bj : bodegaJar) {
            if (requestedQuantity <= 0) {
                break;
            }

            int availableInBodega = bj.getQuantity();
            if (availableInBodega <= 0) {
                continue;
            }

            int deductQuantity = Math.min(availableInBodega, requestedQuantity);
            requestedQuantity -= deductQuantity;
            bodegaSales.add(new BodegaSale(bj.getBodega(), null, jar.getName(), deductQuantity));
        }

        if (requestedQuantity > 0) {
            throw new IllegalArgumentException("No hay suficiente inventario para el tarro: " + jar.getName()
                    + " en el combo: " + combo.getName() + ", se necesitan " + requestedQuantity + " unidades más.");
        }

        // Ahora validamos las tapas del combo

        // Como un combo puede tener varias tapas, recorremos la lista de tapas
        List<ComboCap> caps = combo.getCaps();

        int totalCheckedQuantity = 0;

        // Como normalmente en los goteros se venden los envases tapa y pitorro como uno
        // creamos una variable para contar cuantos pitorros se estan vendiendo
        // esta puede ser igual o menor a la cantidad de envases vendidos
        int totalPitorrosQuantity = 0;
        for (ComboItemOrder comboOrder : saleItem.getComboItemOrder()) {
            if (comboOrder.getColor().getCap().getName().toLowerCase().contains("pitorro")) {
                totalPitorrosQuantity += comboOrder.getQuantity();
            } else {
                totalCheckedQuantity += comboOrder.getQuantity();
            }
        }

        if (totalCheckedQuantity != saleItem.getQuantity()) {
            throw new IllegalArgumentException(
                    "La suma de las cantidades de tapas no coincide con la cantidad de envases del combo: "
                            + combo.getName());
        }

        if (totalPitorrosQuantity > saleItem.getQuantity()) {
            throw new IllegalArgumentException(
                    "La cantidad de pitorros no puede ser mayor a la cantidad de envases del combo: "
                            + combo.getName());
        }

        // Como ahora usamos el DTO ComboItemOrder, recorremos esa lista para validar
        // cada tapa
        for (ComboItemOrder comboOrder : saleItem.getComboItemOrder()) {
            boolean found = false;

            // revisamos que la tapa pertenezca al combo
            for (ComboCap comboCap : caps) {
                if (comboCap.getCap().equals(comboOrder.getColor().getCap())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException(
                        "La tapa " + comboOrder.getColor().getCap().getName() + " no pertenece al combo: "
                                + combo.getName());
            }

            // Sacamos el color de la tapa
            CapColor capColor = comboOrder.getColor();

            // Sacamos todas las bodegas donde tengamos esta tapa de este color y las
            // ordenamos segun su prioridad
            List<BodegaCapColor> bodegaCapColors = capColorService.sortBodegas(capColor.getBodegas());

            // Sacamos cuanta cantidad de esta tapa necesitamos
            int requestedCapQuantity = comboOrder.getQuantity();

            // Sumar cantidades de combos existentes en la lista
            for (SaleItem item : existingItems) {
                if (item.getItemType() == ItemType.CAP && item.getCapColor().getId().equals(capColor.getId())) {
                    requestedCapQuantity += item.getQuantity();
                }
            }

            // recorremos todas las bodegas para ver si tenemos suficiente inventario
            for (BodegaCapColor bodegaCapColor : bodegaCapColors) {
                if (requestedCapQuantity <= 0) {
                    break;
                }

                int availableInBodega = bodegaCapColor.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, requestedCapQuantity);
                requestedCapQuantity -= deductQuantity;
                bodegaSales.add(new BodegaSale(bodegaCapColor.getBodega(), null, 
                        capColor.getCap().getName() + " " + capColor.getColor(), deductQuantity));

            }

            if (requestedCapQuantity > 0) {
                throw new IllegalArgumentException(
                        "No hay suficiente inventario para la tapa del combo: " + combo.getName()
                                + ", se necesitan " + requestedCapQuantity + " unidades más.");
            }

        }
        System.out.println("Bodega Sales: " + bodegaSales.size());
        return bodegaSales;

    }

    /**
     * Modifica el inventario restando las cantidades vendidas.
     *
     * @param saleItem el item de venta que se va a procesar
     * @param userId   el ID del usuario que realiza la venta
     */
    private List<BodegaSale> modifyInventory(SaleItem saleItem, List<SaleItem> existingItems, int userId) {
        List<BodegaSale> bodegaSales = new ArrayList<>();
        if (saleItem.getItemType() == ItemType.COMBO) {
            bodegaSales =modifyInventoryCombo(saleItem, existingItems, userId);
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
                bodegaSales.add(new BodegaSale(bj.getBodega(), null, jar.getName(), deductQuantity));
                inventoryService.newItem(
                        jar.getId(),
                        "jar",
                        deductQuantity,
                        "sale",
                        userId,
                        "Se vendieron " + deductQuantity + " unidades del tarro: " + jar.getName()
                                + ", salio de la bodega: " + bj.getBodega().getName());
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
                bodegaSales.add(new BodegaSale(bodegaCapColor.getBodega(), null,
                        capColor.getCap().getName() + " " + capColor.getColor(), deductQuantity));

                inventoryService.newItem(
                        capColor.getId(),
                        "cap",
                        deductQuantity,
                        "sale",
                        userId,
                        "Se vendieron " + deductQuantity + " unidades de la tapa: " + capColor.getCap().getName()
                                + " color " + capColor.getColor() + ", salio de la bodega: "
                                + bodegaCapColor.getBodega().getName());
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
                bodegaSales.add(new BodegaSale(bq.getBodega(), null, quimico.getName(), deductQuantity));

                inventoryService.newItem(
                        Long.valueOf(quimico.getId()),
                        "quimico",
                        deductQuantity,
                        "sale",
                        userId,
                        "Se vendieron " + deductQuantity + " unidades del químico: " + quimico.getName()
                                + ", salio de la bodega: " + bq.getBodega().getName());
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
                bodegaSales.add(new BodegaSale(be.getBodega(), null, extracto.getName(), deductQuantity));

                inventoryService.newItem(
                        Long.valueOf(extracto.getId()),
                        "extracto",
                        deductQuantity,
                        "sale",
                        userId,
                        "Se vendieron " + deductQuantity + " unidades del extracto: " + extracto.getName()
                                + ", salio de la bodega: " + be.getBodega().getName());
            }

            if (quantityToDeduct > 0) {
                throw new IllegalArgumentException("No hay suficiente inventario para el extracto: "
                        + extracto.getName() + ", se necesitan " + quantityToDeduct + " unidades más.");
            }

        } else {
            throw new IllegalArgumentException("Tipo de item de venta no reconocido: " + saleItem.getItemType());
        }
        return bodegaSales;
    }

    /**
     * Modifica el inventario restando las cantidades vendidas para un SaleItem de
     * tipo COMBO.
     * Dado que estos tiene 1 envase y 1 o más tapas, tenemos que modificar el
     * inventario para todos los componentes
     *
     * @param saleItem      el Combo a vender
     * @param existingItems los items ya existentes en la venta
     * @param userId        el ID del usuario que realiza la venta
     */
    private List<BodegaSale> modifyInventoryCombo(SaleItem saleItem, List<SaleItem> existingItems, int userId) {
        List<BodegaSale> bodegaSales = new ArrayList<>();
        // Sacamos el combo del saleItem para luego sacar el tarro y las tapas
        Combo combo = saleItem.getCombo();

        // Sacamos el envase para validar su inventario
        Jar jar = combo.getJar();

        // Sacamos todas las bodegas donde tengamos este envase y las ordenamos segun su
        // prioridad
        List<BodegaJar> bodegaJar = jarService.sortBodegaJar(jar.getBodegas());

        // Inventario inicial que necesitamos
        int requestedQuantity = saleItem.getQuantity();

        // Sumar cantidades de combos existentes en la lista
        // Es importante resaltar que este revisa si hay suficiente inventario para EL +
        // el existente, si ya se ha pedido el mismo envase en la venta
        for (SaleItem item : existingItems) {
            if (item.getItemType() == ItemType.JAR && item.getJar().getId().equals(jar.getId())) {
                requestedQuantity += item.getQuantity();
            }
        }

        // Recorremos las bodegas para ver si tenemos suficiente inventario
        for (BodegaJar bj : bodegaJar) {
            if (requestedQuantity <= 0) {
                break;
            }

            int availableInBodega = bj.getQuantity();
            if (availableInBodega <= 0) {
                continue;
            }

            int deductQuantity = Math.min(availableInBodega, requestedQuantity);
            bj.setQuantity(availableInBodega - deductQuantity);
            requestedQuantity -= deductQuantity;
            bodegaSales.add(new BodegaSale(bj.getBodega(), null, jar.getName(), deductQuantity));

            inventoryService.newItem(
                    jar.getId(),
                    "jar",
                    deductQuantity,
                    "sale",
                    userId,
                    "Se vendieron " + deductQuantity + " unidades del tarro: " + jar.getName() + " en el combo: "
                            + combo.getName() + ", salio de la bodega: " + bj.getBodega().getName());
        }

        if (requestedQuantity > 0) {
            throw new IllegalArgumentException("No hay suficiente inventario para el tarro: " + jar.getName()
                    + " en el combo: " + combo.getName() + ", se necesitan " + requestedQuantity + " unidades más.");
        }

        List<ComboItemOrder> comboItemOrder = saleItem.getComboItemOrder();

        // Hacemos la sumatoria para ver que tengan la misma cantidad de tapas que el
        // envase
        int totalCheckedQuantity = 0;

        // Revisamos los pitorros por separado
        int totalPitorrosQuantity = 0;
        for (ComboItemOrder order : comboItemOrder) {
            if( order.getColor().getCap().getName().toLowerCase().contains("pitorro")) {
                totalPitorrosQuantity += order.getQuantity();
            } else {
                totalCheckedQuantity += order.getQuantity();
            }
        }

        if (totalCheckedQuantity != saleItem.getQuantity()) {
            throw new IllegalArgumentException(
                    "La suma de las cantidades de tapas no coincide con la cantidad de tapas del combo: "
                            + combo.getName());
        }

        if (totalPitorrosQuantity > saleItem.getQuantity()) {
            throw new IllegalArgumentException(
                    "La cantidad de pitorros no puede ser mayor a la cantidad de envases del combo: "
                            + combo.getName());
        }

        List<ComboCap> caps = combo.getCaps();

        for (ComboItemOrder comboOrder : saleItem.getComboItemOrder()) {
            boolean found = false;

            // revisamos que la tapa pertenezca al combo
            for (ComboCap comboCap : caps) {
                if (comboCap.getCap().equals(comboOrder.getColor().getCap())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new IllegalArgumentException(
                        "La tapa " + comboOrder.getColor().getCap().getName() + " no pertenece al combo: "
                                + combo.getName());
            }

            // Sacamos el color de la tapa
            CapColor capColor = comboOrder.getColor();

            // Sacamos todas las bodegas donde tengamos esta tapa de este color y las
            // ordenamos segun su prioridad
            List<BodegaCapColor> bodegaCapColors = capColorService.sortBodegas(capColor.getBodegas());

            // Sacamos cuanta cantidad de esta tapa necesitamos
            int requestedCapQuantity = comboOrder.getQuantity();

            // Sumar cantidades de combos existentes en la lista
            for (SaleItem item : existingItems) {
                if (item.getItemType() == ItemType.CAP && item.getCapColor().getId().equals(capColor.getId())) {
                    requestedCapQuantity += item.getQuantity();
                }
            }

            // recorremos todas las bodegas para ver si tenemos suficiente inventario
            for (BodegaCapColor bodegaCapColor : bodegaCapColors) {
                if (requestedCapQuantity <= 0) {
                    break;
                }

                int availableInBodega = bodegaCapColor.getQuantity();
                if (availableInBodega <= 0) {
                    continue;
                }

                int deductQuantity = Math.min(availableInBodega, requestedCapQuantity);
                bodegaCapColor.setQuantity(availableInBodega - deductQuantity);
                requestedCapQuantity -= deductQuantity;
                bodegaSales.add(new BodegaSale(bodegaCapColor.getBodega(), null,
                        capColor.getCap().getName() + " " + capColor.getColor(), deductQuantity));

                inventoryService.newItem(
                        capColor.getId(),
                        "cap",
                        deductQuantity,
                        "sale",
                        userId,
                        "Se vendieron " + deductQuantity + " unidades de la tapa: " + capColor.getCap().getName()
                                + " color " + capColor.getColor() + " en el combo: " + combo.getName()
                                + ", salio de la bodega: " + bodegaCapColor.getBodega().getName());
            }

            if (requestedCapQuantity > 0) {
                throw new IllegalArgumentException(
                        "No hay suficiente inventario para la tapa del combo: " + combo.getName()
                                + ", se necesitan " + requestedCapQuantity + " unidades más.");
            }

        }

        return bodegaSales;

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
    public Page<SaleDTO> getFindByFechaAndVendedor(String fechaInicioStr, String fechaFinStr,
            String nombreUsuario, String nombreCliente,
            Pageable pageable) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate fechaInicio = LocalDate.parse(fechaInicioStr, formatter);
        LocalDate fechaFin = LocalDate.parse(fechaFinStr, formatter);
        Page<Sale> sales = saleRepository.findByFechaAndVendedorAndComprador(fechaInicio, fechaFin, nombreUsuario,
                nombreCliente, pageable);
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
        List<BodegaSale> bodegaSales = bodegaSaleRepository.findBySaleId(sale.getId());
        List<SaleItemDTO> saleItemDTOs = new ArrayList<>();
        for (SaleItem saleItem : saleItems) {
            if (saleItem.getItemType() == ItemType.COMBO) {
                saleItemDTOs.add(new SaleItemDTO(
                        comboRepository.findById(saleItem.getCombo().getId()).orElse(null).getName(), saleItem));
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
        List<BodegaSaleDTO> bodegaSaleDTOs = new ArrayList<>();
        for (BodegaSale bodegaSale : bodegaSales) {
            bodegaSaleDTOs.add(new BodegaSaleDTO(bodegaSale));
        }

        SaleDTO saleDTO = new SaleDTO(sale, saleItemDTOs, bodegaSaleDTOs);
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

    /**
     * Desactiva una venta y devuelve los items vendidos a la bodega de
     * devoluciones.
     *
     * @param id el ID de la venta a desactivar
     * @return el DTO SaleDTO de la venta desactivada
     */
    public SaleDTO deactivateSale(Long id) {
        Optional<Sale> saleOpt = saleRepository.findById(id);
        if (!saleOpt.isPresent()) {
            throw new IllegalArgumentException("Venta no encontrada: " + id);
        }

        Bodega devoluciones = bodegaRepository.findByName("devoluciones").orElseThrow(
                () -> new IllegalArgumentException("No se ha configurado una bodega de devoluciones"));

        Sale sale = saleOpt.get();

        if (sale.isActive() == false) {
            throw new IllegalArgumentException("La venta ya está desactivada: " + id);
        }

        List<SaleItem> saleItems = saleItemRepository.findBySale(sale.getId());

        for (SaleItem items : saleItems) {
            if (items.getItemType() == ItemType.JAR) {

                Optional<BodegaJar> bodegaJarOpt = bodegaJarRepository
                        .findByBodegaAndJar(devoluciones, items.getJar());

                if (bodegaJarOpt.isPresent()) {
                    BodegaJar bodegaJar = bodegaJarOpt.get();
                    bodegaJar.setQuantity(bodegaJar.getQuantity() + items.getQuantity());
                    bodegaJarRepository.save(bodegaJar);
                } else {
                    BodegaJar newBodegaJar = new BodegaJar();
                    newBodegaJar.setBodega(devoluciones);
                    newBodegaJar.setJar(items.getJar());
                    newBodegaJar.setQuantity(items.getQuantity());
                    bodegaJarRepository.save(newBodegaJar);
                }

            } else if (items.getItemType() == ItemType.CAP) {

                Optional<BodegaCapColor> bodegaCapColorOpt = bodegaCapColorRepository
                        .findByBodegaIdAndCapColorId(devoluciones.getId(), items.getCapColor().getId());

                if (bodegaCapColorOpt.isPresent()) {
                    BodegaCapColor bodegaCapColor = bodegaCapColorOpt.get();
                    bodegaCapColor.setQuantity(bodegaCapColor.getQuantity() + items.getQuantity());
                    bodegaCapColorRepository.save(bodegaCapColor);
                } else {
                    BodegaCapColor newBodegaCapColor = new BodegaCapColor();
                    newBodegaCapColor.setBodega(devoluciones);
                    newBodegaCapColor.setCapColor(items.getCapColor());
                    newBodegaCapColor.setQuantity(items.getQuantity());
                    bodegaCapColorRepository.save(newBodegaCapColor);
                }

            } else if (items.getItemType() == ItemType.QUIMICO) {

                Optional<BodegaQuimicos> bodegaQuimicoOpt = bodegaQuimicoRepository
                        .findByBodegaAndQuimico(devoluciones, items.getQuimico());

                if (bodegaQuimicoOpt.isPresent()) {
                    BodegaQuimicos bodegaQuimico = bodegaQuimicoOpt.get();
                    bodegaQuimico.setQuantity(bodegaQuimico.getQuantity() + items.getQuantity());
                    bodegaQuimicoRepository.save(bodegaQuimico);
                } else {
                    BodegaQuimicos newBodegaQuimico = new BodegaQuimicos();
                    newBodegaQuimico.setBodega(devoluciones);
                    newBodegaQuimico.setQuimico(items.getQuimico());
                    newBodegaQuimico.setQuantity(items.getQuantity());
                    bodegaQuimicoRepository.save(newBodegaQuimico);
                }

            } else if (items.getItemType() == ItemType.EXTRACTO) {

                Optional<BodegaExtractos> bodegaExtractoOpt = bodegaExtractoRepository
                        .findByBodegaAndExtracto(devoluciones, items.getExtracto());

                if (bodegaExtractoOpt.isPresent()) {
                    BodegaExtractos bodegaExtracto = bodegaExtractoOpt.get();
                    bodegaExtracto.setQuantity(bodegaExtracto.getQuantity() + items.getQuantity());
                    bodegaExtractoRepository.save(bodegaExtracto);
                } else {
                    BodegaExtractos newBodegaExtracto = new BodegaExtractos();
                    newBodegaExtracto.setBodega(devoluciones);
                    newBodegaExtracto.setExtracto(items.getExtracto());
                    newBodegaExtracto.setQuantity(items.getQuantity());
                    bodegaExtractoRepository.save(newBodegaExtracto);
                }

            } else if (items.getItemType() == ItemType.COMBO) {

                // Devolvemos el tarro
                Jar jar = items.getCombo().getJar();
                Optional<BodegaJar> bodegaJarOpt = bodegaJarRepository
                        .findByBodegaAndJar(devoluciones, jar);

                if (bodegaJarOpt.isPresent()) {
                    BodegaJar bodegaJar = bodegaJarOpt.get();
                    bodegaJar.setQuantity(bodegaJar.getQuantity() + items.getQuantity());
                    bodegaJarRepository.save(bodegaJar);
                } else {
                    BodegaJar newBodegaJar = new BodegaJar();
                    newBodegaJar.setBodega(devoluciones);
                    newBodegaJar.setJar(jar);
                    newBodegaJar.setQuantity(items.getQuantity());
                    bodegaJarRepository.save(newBodegaJar);
                }

                for (ComboItemOrder comboOrder : items.getComboItemOrder()) {

                    CapColor capColor = comboOrder.getColor();
                    Integer capQuantity = comboOrder.getQuantity();
                    if (capQuantity == null || capQuantity.equals(0)) {
                        continue;
                    }

                    Optional<BodegaCapColor> bodegaCapColorOpt = bodegaCapColorRepository
                            .findByBodegaIdAndCapColorId(devoluciones.getId(), capColor.getId());

                    if (bodegaCapColorOpt.isPresent()) {
                        BodegaCapColor bodegaCapColor = bodegaCapColorOpt.get();
                        bodegaCapColor
                                .setQuantity(bodegaCapColor.getQuantity() + capQuantity);
                        bodegaCapColorRepository.save(bodegaCapColor);
                    } else {
                        BodegaCapColor newBodegaCapColor = new BodegaCapColor();
                        newBodegaCapColor.setBodega(devoluciones);
                        newBodegaCapColor.setCapColor(capColor);
                        newBodegaCapColor.setQuantity(capQuantity);
                        bodegaCapColorRepository.save(newBodegaCapColor);
                    }
                }

            }
        }
        sale.setActive(false);
        saleRepository.save(sale);

        return toSaleDTO(sale);

    }

    public Page<SaleDTO> getSalesByClient(Pageable pageable, String clientName) {
        Page<Sale> sales = saleRepository.findByClient(clientName, pageable);
        List<SaleDTO> saleDTOs = new ArrayList<>();
        for (Sale sale : sales) {
            SaleDTO saleDTO = toSaleDTO(sale);
            saleDTOs.add(saleDTO);
        }
        return new PageImpl<>(saleDTOs, pageable, sales.getTotalElements());
    }


    private List<BodegaSale> consolidateList(List<BodegaSale> bodegaSales) {
        Map<String, BodegaSale> consolidatedMap = new HashMap<>();

        for (BodegaSale bodegaSale : bodegaSales) {
            String key = bodegaSale.getItemName() + "|" + bodegaSale.getBodega().getName();
            if (consolidatedMap.containsKey(key)) {
                BodegaSale existingBodegaSale = consolidatedMap.get(key);

                int higherQuantity = Math.max(existingBodegaSale.getQuantity(), bodegaSale.getQuantity());
                existingBodegaSale.setQuantity(higherQuantity);
            } else {
                consolidatedMap.put(key, new BodegaSale(
                        bodegaSale.getBodega(),
                        null,
                        bodegaSale.getItemName(),
                        bodegaSale.getQuantity()));
            }
        }

        return new ArrayList<>(consolidatedMap.values());
    }

}
