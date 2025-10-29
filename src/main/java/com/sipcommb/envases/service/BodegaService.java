package com.sipcommb.envases.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.BodegaItem;
import com.sipcommb.envases.dto.BodegaResponse;
import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.repository.BodegaCapColorRepository;
import com.sipcommb.envases.repository.BodegaExtractoRepository;
import com.sipcommb.envases.repository.BodegaJarRepository;
import com.sipcommb.envases.repository.BodegaQuimicoRepository;
import com.sipcommb.envases.repository.BodegaRepository;

@Service
@Transactional
public class BodegaService {
    
    @Autowired
    private BodegaRepository bodegaRepository;

    @Autowired
    private BodegaQuimicoRepository bodegaQuimicoRepository;

    @Autowired
    private BodegaExtractoRepository bodegaExtractoRepository;

    @Autowired
    private BodegaJarRepository bodegaJarRepository;

    @Autowired
    private BodegaCapColorRepository bodegaCapColorRepository;

    public String addBodega(String name) {

        Optional<Bodega> existingBodega = bodegaRepository.findByName(name);
        if (existingBodega.isPresent()) {
            throw new IllegalArgumentException("La bodega con nombre " + name + " ya existe.");
        }

        Bodega newBodega = new Bodega();
        newBodega.setName(name.toLowerCase());
        bodegaRepository.save(newBodega);

        return "Se añadió la bodega: " + newBodega.getName();
    }

    public List<String> getAllBodegas() {
        return bodegaRepository.findAllBodegasNames();
    }

    public Bodega getBodegaByName(String name) {
        Optional<Bodega> bodega = bodegaRepository.findByName(name.toLowerCase());

        if(!bodega.isPresent()) {
            throw new IllegalArgumentException("La bodega con nombre " + name + " no existe.");
        }

        return bodega.get();
    }

    public boolean bodegaExists(String name) {
        Optional<Bodega> bodega = bodegaRepository.findByName(name.toLowerCase());

        return bodega.isPresent();
    }

    public Page<BodegaResponse> getAllBodegasGrouped(Pageable pageable) {
        List<String> bodegaNames = bodegaRepository.findAllBodegasNames();
        List<BodegaResponse> bodegaResponses = new ArrayList<>();

        for (String bodegaName : bodegaNames) {
            BodegaResponse bodegaResponse = new BodegaResponse();
            bodegaResponse.setBodegaName(bodegaName);
            bodegaResponses.add(bodegaResponse);
        }

        List<Object[]> quimicosData = bodegaQuimicoRepository.getGroupedByBodega();
        List<Object[]> extractosData = bodegaExtractoRepository.getGroupedByBodega();
        List<Object[]> jarsData = bodegaJarRepository.getGroupedByBodega();
        List<Object[]> capColorsData = bodegaCapColorRepository.getGroupedByBodega();

        for (Object[] quimico : quimicosData) {
            String bodegaName = (String) quimico[0];
            String quimicoName = (String) quimico[1];
            Integer quantity = (Integer) quimico[2];

            for (BodegaResponse bodegaResponse : bodegaResponses) {
                if (bodegaResponse.getBodegaName().equals(bodegaName)) {
                    bodegaResponse.getItems().add(new BodegaItem(quimicoName, quantity));
                    break; 
                }
            }

        }

        for (Object[] extracto : extractosData) {
            String bodegaName = (String) extracto[0];
            String extractoName = (String) extracto[1];
            Integer quantity = (Integer) extracto[2];

            for (BodegaResponse bodegaResponse : bodegaResponses) {
                if (bodegaResponse.getBodegaName().equals(bodegaName)) {
                    bodegaResponse.getItems().add(new BodegaItem(extractoName, quantity));
                    break; 
                }
            }

        }

        for (Object[] jar : jarsData) {
            String bodegaName = (String) jar[0];
            String jarName = (String) jar[1];
            Integer quantity = (Integer) jar[2];

            for (BodegaResponse bodegaResponse : bodegaResponses) {
                if (bodegaResponse.getBodegaName().equals(bodegaName)) {
                    bodegaResponse.getItems().add(new BodegaItem(jarName, quantity));
                    break; 
                }
            }

        }
        for (Object[] capColor : capColorsData) {
            String bodegaName = (String) capColor[0];
            String capName = (String) capColor[1];
            String colorName = (String) capColor[2];
            Integer quantity = (Integer) capColor[3];

            for (BodegaResponse bodegaResponse : bodegaResponses) {
                if (bodegaResponse.getBodegaName().equals(bodegaName)) {
                    String fullCapColorName = capName + " - " + colorName;
                    bodegaResponse.getItems().add(new BodegaItem(fullCapColorName, quantity));
                    break; 
                }
            }

        }

        return new PageImpl<>(bodegaResponses, pageable, bodegaResponses.size());
    }

}
