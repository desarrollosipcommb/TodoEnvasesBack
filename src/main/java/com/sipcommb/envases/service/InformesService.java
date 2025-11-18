package com.sipcommb.envases.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.BodegaInformeDTO;
import com.sipcommb.envases.dto.BodegaInformeItemDTO;
import com.sipcommb.envases.dto.SaleItemInformeDTO;
import com.sipcommb.envases.dto.SalesByClientInformeDTO;
import com.sipcommb.envases.dto.SalesPerItemDTO;
import com.sipcommb.envases.entity.Client;
import com.sipcommb.envases.entity.ItemType;
import com.sipcommb.envases.entity.Sale;
import com.sipcommb.envases.entity.SaleItem;
import com.sipcommb.envases.repository.BodegaCapColorRepository;
import com.sipcommb.envases.repository.BodegaExtractoRepository;
import com.sipcommb.envases.repository.BodegaJarRepository;
import com.sipcommb.envases.repository.BodegaQuimicoRepository;
import com.sipcommb.envases.repository.BodegaRepository;
import com.sipcommb.envases.repository.SaleItemRepository;
import com.sipcommb.envases.repository.SaleRepository;

@Service
@Transactional
public class InformesService {

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
    private ClientService clientService;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    public List<BodegaInformeDTO> generarInformeBodegas(
            String bodegaNameFilter,
            String itemName,
            Pageable pageable,
            boolean cap,
            boolean jar,
            boolean quimico,
            boolean extracto) {

        if (bodegaNameFilter == null || bodegaNameFilter.isEmpty()) {
            bodegaNameFilter = "";
        }

        if (itemName == null || itemName.isEmpty()) {
            itemName = "";
        }

        List<String> bodegas = bodegaRepository.findBodegasByNameFilter(bodegaNameFilter);

        List<BodegaInformeDTO> informes = new ArrayList<>();

        for (String bodegaName : bodegas) {
            List<BodegaInformeItemDTO> items = new ArrayList<>();

            if (quimico) {
                items.addAll(
                        bodegaQuimicoRepository.findByBodegaNameContaining(bodegaName, itemName)
                                .stream()
                                .map(BodegaInformeItemDTO::new)
                                .collect(java.util.stream.Collectors.toList()));
            }

            if (extracto) {
                items.addAll(
                        bodegaExtractoRepository.findByBodegaNameContaining(bodegaName, itemName)
                                .stream()
                                .map(BodegaInformeItemDTO::new)
                                .collect(java.util.stream.Collectors.toList()));
            }

            if (jar) {
                items.addAll(
                        bodegaJarRepository.findByBodegaNameContaining(bodegaName, itemName)
                                .stream()
                                .map(BodegaInformeItemDTO::new)
                                .collect(java.util.stream.Collectors.toList()));
            }

            if (cap) {
                items.addAll(
                        bodegaCapColorRepository.findByBodegaNameContaining(bodegaName, itemName)
                                .stream()
                                .map(BodegaInformeItemDTO::new)
                                .collect(java.util.stream.Collectors.toList()));
            }

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), items.size());
            List<BodegaInformeItemDTO> paginatedItems = (start < items.size()) ? items.subList(start, end)
                    : new ArrayList<>();

            informes.add(new BodegaInformeDTO(bodegaName,
                    new PageImpl<>(paginatedItems, pageable, items.size())));
        }

        return informes;
    }

    public List<SalesByClientInformeDTO> generarInformeVentasPorCliente(
            String clientNameFilter,
            String fechaInicio,
            String fechaFin,
            Pageable pageable,
            boolean completed,
            boolean cancelled) {

        if (clientNameFilter == null || clientNameFilter.isEmpty()) {
            clientNameFilter = "";
        }

        if (fechaInicio == null || fechaInicio.isEmpty()) {
            fechaInicio = "0000-01-01"; // yyyy-MM-dd
        }

        if (fechaFin == null || fechaFin.isEmpty()) {
            fechaFin = "9999-12-31"; // yyyy-MM-dd
        }

        List<Client> clients = clientService.getClientsLikeName(clientNameFilter);

        List<SalesByClientInformeDTO> informes = new ArrayList<>();

        for (Client client : clients) {
            SalesByClientInformeDTO currentItem = new SalesByClientInformeDTO();
            currentItem.setClientName(client.getName());

            List<Sale> sales = saleRepository.findByDateRangeAndClient(
                    java.time.LocalDate.parse(fechaInicio),
                    java.time.LocalDate.parse(fechaFin),
                    client.getName());

            List<SaleItemInformeDTO> saleItemDTOs = new ArrayList<>();
            for (Sale sale : sales) {
                List<SaleItem> saleItems = saleItemRepository.findBySale(sale.getId());
                if (completed && cancelled) {
                    for (SaleItem saleItem : saleItems) {
                        saleItemDTOs.add(mapToSaleItemInformeDTO(saleItem, sale));
                    }
                } else if (completed == sale.isActive()) {
                    for (SaleItem saleItem : saleItems) {
                        saleItemDTOs.add(mapToSaleItemInformeDTO(saleItem, sale));
                    }
                } else if (cancelled == !sale.isActive()) {
                    for (SaleItem saleItem : saleItems) {
                        saleItemDTOs.add(mapToSaleItemInformeDTO(saleItem, sale));
                    }
                }
            }
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), saleItemDTOs.size());
            List<SaleItemInformeDTO> paginatedItems = (start < saleItemDTOs.size()) ? saleItemDTOs.subList(start, end)
                    : new ArrayList<>();

            Page<SaleItemInformeDTO> page = new PageImpl<>(paginatedItems, pageable, saleItemDTOs.size());
            currentItem.setTotalSalesAmount(saleItemDTOs.size());
            currentItem.setItems(page);
            informes.add(currentItem);
        }

        return informes;
    }

    private SaleItemInformeDTO mapToSaleItemInformeDTO(SaleItem saleItem, Sale sale) {
        String itemName = "";
        if (saleItem.getItemType() == ItemType.CAP) {
            itemName = saleItem.getCapColor().getCap().getName() + " " + saleItem.getColor();
        } else if (saleItem.getItemType() == ItemType.JAR) {
            itemName = saleItem.getJar().getName();
        } else if (saleItem.getItemType() == ItemType.QUIMICO) {
            itemName = saleItem.getQuimico().getName();
        } else if (saleItem.getItemType() == ItemType.EXTRACTO) {
            itemName = saleItem.getExtracto().getName();
        } else if (saleItem.getItemType() == ItemType.COMBO) {
            itemName = saleItem.getCombo().getName();
        }

        return new SaleItemInformeDTO(
                itemName,
                saleItem.getQuantity(),
                sale.isActive(),
                sale.getSaleDate().toString());
    }

    public Page<SalesPerItemDTO> generarInformeVentasPorItem(
            String name,
            Pageable pageable,
            boolean jar,
            boolean cap,
            boolean quimico,
            boolean extracto,
            boolean combo) {

        List<Object[]> items = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            name = "";
        }

        List<SalesPerItemDTO> informes = new ArrayList<>();

        if (jar) {
            items.addAll(saleItemRepository.findAllJarItems(name));

            for (Object[] item : items) {
                SalesPerItemDTO dto = new SalesPerItemDTO();
                dto.setItemName((String) item[0]);
                dto.setTotalQuantitySold(Long.valueOf(0 + item[1].toString()));
                List<Object[]> clients = saleItemRepository.findAllClientsByJarName((String) item[0]);
                List<String> clientNames = new ArrayList<>();
                for (Object[] client : clients) {
                    clientNames.add((String) client[0]);
                }
                dto.setClientNames(clientNames);
                informes.add(dto);
            }
            items.clear();
        }

        if (cap) {
            items.addAll(saleItemRepository.findAllCapItems(name));

            for (Object[] item : items) {
                SalesPerItemDTO dto = new SalesPerItemDTO();
                dto.setItemName((String) item[0] + " " + (String) item[1]);
                dto.setTotalQuantitySold(Long.valueOf(0 + item[2].toString()));
                List<Object[]> clients = saleItemRepository.findAllClientsByCapName((String) item[0], (String) item[1]);
                List<String> clientNames = new ArrayList<>();
                for (Object[] client : clients) {
                    clientNames.add((String) client[0]);
                }
                dto.setClientNames(clientNames);
                informes.add(dto);
            }
            items.clear();
        }

        if (quimico) {
            items.addAll(saleItemRepository.findAllQuimicoItems(name));

            for (Object[] item : items) {
                SalesPerItemDTO dto = new SalesPerItemDTO();
                dto.setItemName((String) item[0]);
                dto.setTotalQuantitySold(Long.valueOf(0 + item[1].toString()));
                List<Object[]> clients = saleItemRepository.findAllClientsByQuimicoName((String) item[0]);
                List<String> clientNames = new ArrayList<>();
                for (Object[] client : clients) {
                    clientNames.add((String) client[0]);
                }
                dto.setClientNames(clientNames);
                informes.add(dto);
            }
            items.clear();
        }

        if (extracto) {
            items.addAll(saleItemRepository.findAllExtractItems(name));

            for (Object[] item : items) {
                SalesPerItemDTO dto = new SalesPerItemDTO();
                dto.setItemName((String) item[0]);
                dto.setTotalQuantitySold(Long.valueOf(0 + item[1].toString()));
                List<Object[]> clients = saleItemRepository.findAllClientsByExtractName((String) item[0]);
                List<String> clientNames = new ArrayList<>();
                for (Object[] client : clients) {
                    clientNames.add((String) client[0]);
                }
                dto.setClientNames(clientNames);
                informes.add(dto);
            }
            items.clear();
        }

        if (combo) {
            items.addAll(saleItemRepository.findAllComboItems(name));

            for (Object[] item : items) {
                SalesPerItemDTO dto = new SalesPerItemDTO();
                dto.setItemName((String) item[0]);
                dto.setTotalQuantitySold(Long.valueOf(0 + item[1].toString()));
                List<Object[]> clients = saleItemRepository.findAllClientsByComboName((String) item[0]);
                List<String> clientNames = new ArrayList<>();
                for (Object[] client : clients) {
                    clientNames.add((String) client[0]);
                }
                dto.setClientNames(clientNames);
                informes.add(dto);
            }
            items.clear();
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), informes.size());
        informes = (start < informes.size()) ? informes.subList(start, end)
                : new ArrayList<>();

        Page<SalesPerItemDTO> page = new PageImpl<>(informes, pageable, informes.size());

        return page;
    }
}
