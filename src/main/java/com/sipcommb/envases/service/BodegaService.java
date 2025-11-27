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

    public BodegaResponse addBodega(String name, Long priority) {

        if (bodegaExists(name)) {
            throw new IllegalArgumentException("La bodega con nombre " + name + " ya existe.");
        }

        Optional<Bodega> existingPriority = bodegaRepository.findByPriority(priority);
        if (existingPriority.isPresent()) {
            throw new IllegalArgumentException("Bodega con la prioridad " + priority + " ya existe.");
        }

        if (name.toLowerCase().equals("devoluciones")) {
            priority = 0L;
        } else if (priority <= 0) {
            throw new IllegalArgumentException("La prioridad debe ser un número positivo.");
        }

        Bodega newBodega = new Bodega();
        newBodega.setName(name.toLowerCase());
        newBodega.setPriority(priority);
        bodegaRepository.save(newBodega);

        BodegaResponse bodegaResponse = new BodegaResponse();
        bodegaResponse.setBodegaName(newBodega.getName());
        bodegaResponse.setPriority(newBodega.getPriority());

        return bodegaResponse;
    }

    public BodegaResponse changePriority(String name, Long newPriority) {
        Bodega bodega = getBodegaByName(name);

        if (bodega.getName().equals("devoluciones")) {
            throw new IllegalArgumentException("No se puede cambiar la prioridad de la bodega de devoluciones.");
        } else if (newPriority <= 0) {
            throw new IllegalArgumentException("La prioridad debe ser un número positivo.");
        }

        Optional<Bodega> existingPriority = bodegaRepository.findByPriority(newPriority);
        if (existingPriority.isPresent() && !existingPriority.get().getName().equals(name)) {
            throw new IllegalArgumentException("La prioridad " + newPriority + " ya está asignada a la bodega: "
                    + existingPriority.get().getName());
        } else if (existingPriority.isPresent() && existingPriority.get().getName().equals(name)) {
            throw new IllegalArgumentException("La bodega " + name + " ya tiene la prioridad " + newPriority + ".");
        }

        bodega.setPriority(newPriority);
        bodegaRepository.save(bodega);

        BodegaResponse bodegaResponse = new BodegaResponse();
        bodegaResponse.setBodegaName(bodega.getName());
        bodegaResponse.setPriority(bodega.getPriority());

        return bodegaResponse;
    }

    public BodegaResponse ChangeName(String oldName, String newName) {
        Bodega bodega = getBodegaByName(oldName);

        if (oldName.equals(newName)) {
            throw new IllegalArgumentException("El nombre nuevo es igual al nombre actual.");
        }

        if (bodega.getName().equals("devoluciones")) {
            throw new IllegalArgumentException("No se puede cambiar el nombre de la bodega de devoluciones.");
        }

        if (bodegaExists(newName)) {
            throw new IllegalArgumentException("La bodega con nombre " + newName + " ya existe.");
        }

        bodega.setName(newName.toLowerCase());
        bodegaRepository.save(bodega);

        BodegaResponse bodegaResponse = new BodegaResponse();
        bodegaResponse.setBodegaName(bodega.getName());
        bodegaResponse.setPriority(bodega.getPriority());

        return bodegaResponse;
    }

    public List<String> getAllBodegas(String nameFilter) {
        return bodegaRepository.findBodegasByNameFilter(nameFilter);
    }

    public Bodega getBodegaByName(String name) {
        Optional<Bodega> bodega = bodegaRepository.findByName(name.toLowerCase());

        if (!bodega.isPresent()) {
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
            bodegaResponse.setPriority(bodegaRepository.findByName(bodegaName).get().getPriority());
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

    public Page<BodegaResponse> getBodegas(Pageable pageable, String nameFilter) {

        if (nameFilter != null && !nameFilter.isEmpty()) {
            nameFilter = nameFilter.toLowerCase().trim();
        } else {
            nameFilter = "";
        }

        Page<Bodega> bodegas = bodegaRepository.findBodegasByNameFilter(nameFilter, pageable);
        Page<BodegaResponse> bodegaResponses = bodegas.map(bodega -> new BodegaResponse(bodega));

        return bodegaResponses;
    }

    public BodegaResponse update(String newName, String oldName, Long priority) {

        Bodega bodega = getBodegaByName(oldName);
        
        if (!newName.equals(oldName)) {
            if (newName != null && !newName.isEmpty()) {
                if (bodegaExists(newName)) {
                    throw new IllegalArgumentException("La bodega con nombre " + newName + " ya existe.");
                }
                bodega.setName(newName.toLowerCase());
            }
        }

        if (priority != null && priority != 0L && !priority.equals(bodega.getPriority())) {
            if (bodega.getName().equals("devoluciones")) {
                throw new IllegalArgumentException("No se puede cambiar la prioridad de la bodega de devoluciones.");
            } else if (priority <= 0) {
                throw new IllegalArgumentException("La prioridad debe ser un número positivo.");
            }

            Optional<Bodega> existingPriority = bodegaRepository.findByPriority(priority);
            if (existingPriority.isPresent()) {
                throw new IllegalArgumentException("La prioridad " + priority + " ya está asignada a la bodega: "
                        + existingPriority.get().getName());
            }

            bodega.setPriority(priority);
        }

        bodegaRepository.save(bodega);

        BodegaResponse bodegaResponse = new BodegaResponse();
        bodegaResponse.setBodegaName(bodega.getName());
        bodegaResponse.setPriority(bodega.getPriority());

        return bodegaResponse;
    }

}
