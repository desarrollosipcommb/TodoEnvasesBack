package com.sipcommb.envases.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.BodegaInformeDTO;
import com.sipcommb.envases.dto.BodegaInformeItemDTO;
import com.sipcommb.envases.repository.BodegaCapColorRepository;
import com.sipcommb.envases.repository.BodegaExtractoRepository;
import com.sipcommb.envases.repository.BodegaJarRepository;
import com.sipcommb.envases.repository.BodegaQuimicoRepository;
import com.sipcommb.envases.repository.BodegaRepository;

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

    
    public List<BodegaInformeDTO> generarInformeBodegas(
        String bodegaNameFilter, 
        String itemName,
        Pageable pageable,
        boolean cap,
        boolean jar,
        boolean quimico,
        boolean extracto
    ) {

        if(bodegaNameFilter == null || bodegaNameFilter.isEmpty()) {
            bodegaNameFilter = "";
        }

        if(itemName == null || itemName.isEmpty()) {
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
                        .collect(java.util.stream.Collectors.toList())
                );
            }

            if (extracto) {
                items.addAll(
                    bodegaExtractoRepository.findByBodegaNameContaining(bodegaName, itemName)
                        .stream()
                        .map(BodegaInformeItemDTO::new)
                        .collect(java.util.stream.Collectors.toList())
                );
            }

            if (jar) {
                items.addAll(
                    bodegaJarRepository.findByBodegaNameContaining(bodegaName, itemName)
                        .stream()
                        .map(BodegaInformeItemDTO::new)
                        .collect(java.util.stream.Collectors.toList())
                );
            }

            if (cap) {
                items.addAll(
                    bodegaCapColorRepository.findByBodegaNameContaining(bodegaName, itemName)
                        .stream()
                        .map(BodegaInformeItemDTO::new)
                        .collect(java.util.stream.Collectors.toList())
                );
            }

            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), items.size());
            List<BodegaInformeItemDTO> paginatedItems = (start < items.size()) ? items.subList(start, end) : new ArrayList<>();


            informes.add(new BodegaInformeDTO(bodegaName, 
                new PageImpl<>(paginatedItems, pageable, items.size())
            ));
        }

        return informes;
    }

}
