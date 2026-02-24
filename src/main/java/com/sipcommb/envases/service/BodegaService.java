package com.sipcommb.envases.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import com.sipcommb.envases.dto.BodegaItem;
import com.sipcommb.envases.dto.BodegaResponse;
import com.sipcommb.envases.entity.Bodega;
import com.sipcommb.envases.entity.BodegaCapColor;
import com.sipcommb.envases.entity.BodegaExtractos;
import com.sipcommb.envases.entity.BodegaJar;
import com.sipcommb.envases.entity.BodegaQuimicos;
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

    public Bodega addBodegaExcel(String name, Long priority) {

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

        return newBodega;
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

    public BodegaResponse getSpecific(String name, String itemFilter) {
        Bodega bodega = getBodegaByName(name);

        List<BodegaItem> filteredItems = new ArrayList<>();

        filteredItems.addAll(bodegaQuimicoRepository.findByBodegaNameContaining(name, itemFilter).stream()
                .map(bq -> new BodegaItem(bq.getQuimico().getName(), bq.getQuantity())).collect(Collectors.toList()));
        filteredItems.addAll(bodegaExtractoRepository.findByBodegaNameContaining(name, itemFilter).stream()
                .map(be -> new BodegaItem(be.getExtracto().getName(), be.getQuantity())).collect(Collectors.toList()));
        filteredItems.addAll(bodegaJarRepository.findByBodegaNameContaining(name, itemFilter).stream()
                .map(bj -> new BodegaItem(bj.getJar().getName(), bj.getQuantity())).collect(Collectors.toList()));
        filteredItems.addAll(bodegaCapColorRepository.findByBodegaNameContaining(name, itemFilter).stream()
                .map(bcc -> new BodegaItem(bcc.getCapColor().getCap().getName() + " " + bcc.getCapColor().getColor(),
                        bcc.getQuantity()))
                .collect(Collectors.toList()));
        BodegaResponse bodegaResponse = new BodegaResponse(bodega);

        bodegaResponse.setItems(filteredItems);
        return bodegaResponse;
    }

    public BodegaResponse transferInventory(String fromBodegaName, String toBodegaName, String item, Integer quantity) {
        Bodega fromBodega = getBodegaByName(fromBodegaName);
        Bodega toBodega = getBodegaByName(toBodegaName);

        Optional<BodegaCapColor> capColorItem = bodegaCapColorRepository.findBodegaItem(fromBodega.getName(), item);

        Optional<BodegaJar> jarItem = bodegaJarRepository.findBodegaItem(fromBodega.getName(), item);

        Optional<BodegaExtractos> extractoItem = bodegaExtractoRepository.findBodegaItem(fromBodega.getName(), item);

        Optional<BodegaQuimicos> quimicoItem = bodegaQuimicoRepository.findBodegaItem(fromBodega.getName(), item);

        if(capColorItem.isPresent()){
            if(capColorItem.get().getQuantity() < quantity){
                throw new IllegalArgumentException("No hay suficiente inventario para transferir.");
            }

            capColorItem.get().setQuantity(capColorItem.get().getQuantity() - quantity);

            bodegaCapColorRepository.save(capColorItem.get());

            Optional<BodegaCapColor> toCapColorItem = bodegaCapColorRepository.findByBodegaIdAndCapColorId(toBodega.getId(), capColorItem.get().getCapColor().getId());

            if(toCapColorItem.isPresent()){
                toCapColorItem.get().setQuantity(toCapColorItem.get().getQuantity() + quantity);
                bodegaCapColorRepository.save(toCapColorItem.get());
            } else {
                BodegaCapColor newItem = new BodegaCapColor();
                newItem.setBodega(toBodega);
                newItem.setCapColor(capColorItem.get().getCapColor());
                newItem.setQuantity(quantity);
                bodegaCapColorRepository.save(newItem);
            }
            return new BodegaResponse(toBodega);
        }

        if(jarItem.isPresent()){
            if(jarItem.get().getQuantity() < quantity){
                throw new IllegalArgumentException("No hay suficiente inventario para transferir.");
            }

            jarItem.get().setQuantity(jarItem.get().getQuantity() - quantity);

            bodegaJarRepository.save(jarItem.get());

            Optional<BodegaJar> toJarItem = bodegaJarRepository.findByBodegaAndJar(toBodega, jarItem.get().getJar());

            if(toJarItem.isPresent()){
                toJarItem.get().setQuantity(toJarItem.get().getQuantity() + quantity);
                bodegaJarRepository.save(toJarItem.get());
            } else {
                BodegaJar newItem = new BodegaJar();
                newItem.setBodega(toBodega);
                newItem.setJar(jarItem.get().getJar());
                newItem.setQuantity(quantity);
                bodegaJarRepository.save(newItem);
            }
            return new BodegaResponse(toBodega);
        }

        if(extractoItem.isPresent()){
            if(extractoItem.get().getQuantity() < quantity){
                throw new IllegalArgumentException("No hay suficiente inventario para transferir.");
            }

            extractoItem.get().setQuantity(extractoItem.get().getQuantity() - quantity);

            bodegaExtractoRepository.save(extractoItem.get());

            Optional<BodegaExtractos> toExtractoItem = bodegaExtractoRepository.findByBodegaAndExtracto(toBodega, extractoItem.get().getExtracto());

            if(toExtractoItem.isPresent()){
                toExtractoItem.get().setQuantity(toExtractoItem.get().getQuantity() + quantity);
                bodegaExtractoRepository.save(toExtractoItem.get());
            } else {
                BodegaExtractos newItem = new BodegaExtractos();
                newItem.setBodega(toBodega);
                newItem.setExtracto(extractoItem.get().getExtracto());
                newItem.setQuantity(quantity);
                bodegaExtractoRepository.save(newItem);
            }
            return new BodegaResponse(toBodega);
        }

        if(quimicoItem.isPresent()){
            if(quimicoItem.get().getQuantity() < quantity){
                throw new IllegalArgumentException("No hay suficiente inventario para transferir.");
            }

            quimicoItem.get().setQuantity(quimicoItem.get().getQuantity() - quantity);

            bodegaQuimicoRepository.save(quimicoItem.get());

            Optional<BodegaQuimicos> toQuimicoItem = bodegaQuimicoRepository.findByBodegaAndQuimico(toBodega, quimicoItem.get().getQuimico());

            if(toQuimicoItem.isPresent()){
                toQuimicoItem.get().setQuantity(toQuimicoItem.get().getQuantity() + quantity);
                bodegaQuimicoRepository.save(toQuimicoItem.get());
            } else {
                BodegaQuimicos newItem = new BodegaQuimicos();
                newItem.setBodega(toBodega);
                newItem.setQuimico(quimicoItem.get().getQuimico());
                newItem.setQuantity(quantity);
                bodegaQuimicoRepository.save(newItem);
            }
            return new BodegaResponse(toBodega);
        }

        throw new IllegalArgumentException("El ítem " + item + " no existe en la bodega " + fromBodegaName + ".");
    }

}
