package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.CapRequest;
import com.sipcommb.envases.dto.ComboRequest;
import com.sipcommb.envases.dto.ComboResponse;
import com.sipcommb.envases.dto.PriceDeals;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.Combo;
import com.sipcommb.envases.entity.ComboCap;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.JarCapCompatibility;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.ComboCapRepository;
import com.sipcommb.envases.repository.ComboRepository;
import com.sipcommb.envases.repository.JarCapCompatibilityRepository;
import com.sipcommb.envases.repository.JarRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import java.util.Set;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ComboService {

    @Autowired
    private ComboRepository comboRepository;

    @Autowired
    private JarRepository jarRepository;

    @Autowired
    private CapRepository capRepository;

    @Autowired
    private JarCapCompatibilityRepository jarCapCompatibilityRepository;

    @Autowired
    private PriceService priceService;

    @Autowired
    private ComboCapRepository comboCapRepository;

    public ComboResponse addCombo(ComboRequest comboRequest) {

        if (comboRepository.findByName(comboRequest.getName().trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un combo con el nombre: " + comboRequest.getName() + ".");
        }

        Combo combo = new Combo();
        combo.setName(comboRequest.getName().trim());

        Optional<Jar> jarOpt = jarRepository.getByName(comboRequest.getJarName().trim());
        if (!jarOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un tarro con el nombre: " + comboRequest.getJarName() + ".");
        } else {
            combo.setJar(jarOpt.get());
        }

        List<Cap> caps = new ArrayList<>();
        for (CapRequest capRequest : comboRequest.getCapRequests()) {
            Optional<Cap> capOpt = capRepository.findByNameAndDiameter(capRequest.getName().trim(), capRequest.getDiameter());
            if (!capOpt.isPresent()) {
                throw new IllegalArgumentException("No existe una tapa con el nombre: " + capRequest.getName() + ".");
            } else {
                caps.add(capOpt.get());
            }
        }

        if (comboRequest.getUnitPrice() == null || comboRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser 0 o negativo.");
        }

        combo.setUnitPrice(comboRequest.getUnitPrice());

        if (comboRequest.getDocenaPrice() < 0) {
            throw new IllegalArgumentException("El precio por docena no puede ser negativo.");
        }

        combo.setDocenaPrice(comboRequest.getDocenaPrice() != null ? comboRequest.getDocenaPrice() : 0.00);

        if (comboRequest.getCienPrice() < 0) {
            throw new IllegalArgumentException("El precio por cien no puede ser negativo.");
        }

        if (comboRequest.getPacaPrice() < 0) {
            throw new IllegalArgumentException("El precio por paca no puede ser negativo.");
        }

        for (Cap cap : caps) {
            Optional<JarCapCompatibility> compatibilityOpt = jarCapCompatibilityRepository.findByJarAndCap(jarOpt.get().getId(), cap.getId());
            if (!compatibilityOpt.isPresent()) {
                throw new IllegalArgumentException("La tapa: " + cap.getName() + " seleccionada no es compatible con el tarro: " + jarOpt.get().getName() + ".");
            } else if (!compatibilityOpt.get().isCompatible()) {
                throw new IllegalArgumentException("La tapa: " + cap.getName() + " no es compatible con el tarro: " + jarOpt.get().getName() + ".");
            }
        }


        combo.setCienPrice(comboRequest.getCienPrice() != null ? comboRequest.getCienPrice() : 0.00);
        combo.setDescription(comboRequest.getDescription() != null ? comboRequest.getDescription().trim() : "");
        combo.setUnitsInPaca(jarOpt.get().getUnitsInPaca());
        combo.setPacaPrice(comboRequest.getPacaPrice() != null ? comboRequest.getPacaPrice() : 0.00);
        combo.setCreatedAt(LocalDateTime.now());
        combo.setUpdatedAt(LocalDateTime.now());

        comboRepository.save(combo);

        for (Cap cap : caps) {
            Optional<ComboCap> comboCapOpt = comboCapRepository.findByComboIdAndCapId(combo.getId(), cap.getId());
            if (!comboCapOpt.isPresent()) {
                ComboCap comboCap = new ComboCap(combo, cap);
                comboCapRepository.save(comboCap);
            }
        }

        return new ComboResponse(combo);
    }

    public Page<ComboResponse> getAllCombos(Pageable pageable) {
        Page<Combo> combos = comboRepository.findAll(pageable);
        return combos.map(ComboResponse::new);
    }

    public ComboResponse getByName(String comboName) {
        Optional<Combo> combo = comboRepository.findByName(comboName);
        if (!combo.isPresent()) {
            throw new IllegalArgumentException("No existe un combo con el nombre: " + comboName + ".");
        }
        return new ComboResponse(combo.get());
    }

    public Page<ComboResponse> getLikeName(String comboName, Pageable pageable) {
        Page<Combo> combos = comboRepository.findByNameContaining(comboName, pageable);
        return combos.map(ComboResponse::new);
    }

    public Page<ComboResponse> getLikeNameActivo(String comboName, Pageable pageable) {
        Page<Combo> combos = comboRepository.findByNameContainingActive(comboName, pageable);
        return combos.map(ComboResponse::new);
    }

    public ComboResponse updateCombo(ComboRequest comboRequest) {
        Optional<Combo> existingComboOpt = comboRepository.findByName(comboRequest.getName().trim());
        if (!existingComboOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un combo con el nombre: " + comboRequest.getName() + ".");
        }

        Combo existingCombo = existingComboOpt.get();

        if (comboRequest.getUnitPrice() != null && comboRequest.getUnitPrice() > 0) {
            existingCombo.setUnitPrice(comboRequest.getUnitPrice());
        } else if (comboRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser 0 o negativo.");
        }

        if (comboRequest.getDocenaPrice() != null && comboRequest.getDocenaPrice() > 0) {
            existingCombo.setDocenaPrice(comboRequest.getDocenaPrice());
        } else if (comboRequest.getDocenaPrice() < 0) {
            throw new IllegalArgumentException("El precio por docena no puede ser 0 o negativo.");
        }

        if (comboRequest.getCienPrice() != null && comboRequest.getCienPrice() > 0) {
            existingCombo.setCienPrice(comboRequest.getCienPrice());
        } else if (comboRequest.getCienPrice() < 0) {
            throw new IllegalArgumentException("El precio por cien no puede ser 0 o negativo.");
        }

        if (comboRequest.getPacaPrice() != null && comboRequest.getPacaPrice() > 0) {
            existingCombo.setPacaPrice(comboRequest.getPacaPrice());
        } else if (comboRequest.getPacaPrice() < 0) {
            throw new IllegalArgumentException("El precio por paca no puede ser 0 o negativo.");
        }

        Set<Long> idsFront = comboRequest.getCapRequests()
                .stream().map(CapRequest::getId).collect(Collectors.toSet());

        Set<Long> idsBD = existingCombo.getCaps()
                .stream().map(cc -> cc.getCap().getId()).collect(Collectors.toSet());

        existingCombo.getCaps().removeIf(cc -> !idsFront.contains(cc.getCap().getId()));

        idsFront.stream()
                .filter(id -> !idsBD.contains(id)) // solo los nuevos
                .forEach(newCapId -> {
                    Cap capEntity = capRepository.findById(newCapId)
                            .orElseThrow(() -> new RuntimeException("Cap no encontrada con ID: " + newCapId));

                    ComboCap nuevaRelacion = new ComboCap();
                    nuevaRelacion.setCombo(existingCombo);
                    nuevaRelacion.setCap(capEntity);
                    existingCombo.getCaps().add(nuevaRelacion);
                });
        comboRepository.save(existingCombo);


        if (comboRequest.getDescription() != null) {
            existingCombo.setDescription(comboRequest.getDescription().trim());
        }

        existingCombo.setUpdatedAt(LocalDateTime.now());

        comboRepository.save(existingCombo);

        return new ComboResponse(existingCombo);
    }

    public ComboResponse activeCombo(String comboName) {
        Optional<Combo> existingComboOpt = comboRepository.findByNameAll(comboName.trim());
        if (!existingComboOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un combo con el nombre: " + comboName + ".");
        }

        Combo existingCombo = existingComboOpt.get();
        existingCombo.setActive(true);
        existingCombo.setUpdatedAt(LocalDateTime.now());

        comboRepository.save(existingCombo);

        return new ComboResponse(existingCombo);
    }

    public ComboResponse deactivateCombo(String comboName) {
        Optional<Combo> existingComboOpt = comboRepository.findByName(comboName.trim());
        if (!existingComboOpt.isPresent()) {
            throw new IllegalArgumentException("No existe un combo con el nombre: " + comboName + ".");
        }

        Combo existingCombo = existingComboOpt.get();
        existingCombo.setActive(false);
        existingCombo.setUpdatedAt(LocalDateTime.now());

        comboRepository.save(existingCombo);

        return new ComboResponse(existingCombo);
    }

    public Page<ComboResponse> getAllActiveCombos(Pageable pageable) {
        Page<Combo> activeCombos = comboRepository.findAllActiveCombos(pageable);
        return activeCombos.map(ComboResponse::new);
    }

    public Page<ComboResponse> getAllInactiveCombos(Pageable pageable) {
        Page<Combo> inactiveCombos = comboRepository.findAllInactiveCombos(pageable);
        return inactiveCombos.map(ComboResponse::new);
    }

    public Page<ComboResponse> getCombosByPriceRange(PriceSearchRequest priceSearchRequest, Pageable pageable) {
        boolean exactSearch = priceService.verifyPriceSearchRequest(priceSearchRequest);

        if (priceSearchRequest.getPriceDeal() == PriceDeals.PACA) {
            throw new IllegalArgumentException("El trato de precio 'PACA' no está implementado para combos.");
        }

        switch (priceSearchRequest.getPriceDeal()) {
            case CIEN:

                if (exactSearch) {
                    return comboRepository.findByCienPrice(priceSearchRequest.getExactPrice().doubleValue(), pageable).map(ComboResponse::new);
                } else {
                    return comboRepository.findByCienPriceBetween(priceSearchRequest.getMinPrice().doubleValue(), priceSearchRequest.getMaxPrice().doubleValue(), pageable).map(ComboResponse::new);
                }
            case DOCENA:
                if (exactSearch) {
                    return comboRepository.findByDocenaPrice(priceSearchRequest.getExactPrice().doubleValue(), pageable).map(ComboResponse::new);
                } else {
                    return comboRepository.findByDocenaPriceBetween(priceSearchRequest.getMinPrice().doubleValue(), priceSearchRequest.getMaxPrice().doubleValue(), pageable).map(ComboResponse::new);
                }
            case UNIDAD:
                if (exactSearch) {
                    return comboRepository.findByUnidadPrice(priceSearchRequest.getExactPrice().doubleValue(), pageable).map(ComboResponse::new);
                } else {
                    return comboRepository.findByUnidadPriceBetween(priceSearchRequest.getMinPrice().doubleValue(), priceSearchRequest.getMaxPrice().doubleValue(), pageable).map(ComboResponse::new);
                }
            default:
                throw new IllegalArgumentException("Tipo de trato de precio no válido: " + priceSearchRequest.getPriceDeal() + ".");
        }
    }

}
