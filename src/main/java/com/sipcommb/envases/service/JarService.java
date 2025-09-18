package com.sipcommb.envases.service;

import com.sipcommb.envases.dto.CapDTO;
import com.sipcommb.envases.dto.JarDTO;
import com.sipcommb.envases.dto.JarRequestDTO;
import com.sipcommb.envases.dto.PriceSearchRequest;
import com.sipcommb.envases.entity.Cap;
import com.sipcommb.envases.entity.Jar;
import com.sipcommb.envases.entity.JarCapCompatibility;
import com.sipcommb.envases.repository.CapRepository;
import com.sipcommb.envases.repository.JarCapCompatibilityRepository;
import com.sipcommb.envases.repository.JarRepository;
import com.sipcommb.envases.repository.JarTypeRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class JarService {

    @Autowired
    private JarRepository jarRepository;

    @Autowired
    private JarTypeRepository jarTypeRepository;

    @Autowired
    private JarCapCompatibilityRepository jarCapCompatibilityRepository;

    @Autowired
    private CapRepository capRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PriceService priceService;

    public JarDTO addJar(JarRequestDTO jarRequest, String token) {

        if(jarRepository.getByName(jarRequest.getName().trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un envase con ese nombre.");
        }

        // hay que validar que el envase si tenga un diametro asociado, dado que algunos vienen con tapa propia asi que el diametro no es obligatorio
        if(!jarRequest.getDiameter().isEmpty() && !jarTypeRepository.getTypeByDiameter(jarRequest.getDiameter()).isPresent()) {
            throw new IllegalArgumentException("No existe un tipo de envase con ese diámetro.");
        }

        if(jarRequest.getUnitPrice() == null || jarRequest.getUnitPrice() <= 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
        }

        Jar jar = new Jar();

        if(!jarRequest.getDiameter().isEmpty()){
            jar.setJarType(jarTypeRepository.getTypeByDiameter(jarRequest.getDiameter()).get());
        }

        jar.setName(jarRequest.getName().trim().toLowerCase());
        jar.setDescription(jarRequest.getDescription().trim());
        jar.setQuantity(jarRequest.getQuantity());
        jar.setUnitPrice(BigDecimal.valueOf(jarRequest.getUnitPrice()));
        jar.setDocenaPrice(BigDecimal.valueOf(jarRequest.getDocenaPrice()));
        jar.setCienPrice(BigDecimal.valueOf(jarRequest.getCienPrice()));
        jar.setPacaPrice(BigDecimal.valueOf(jarRequest.getPacaPrice()));
        jar.setUnitsInPaca(jarRequest.getUnitsInPaca());
        jarRepository.save(jar);

        manageCompatibility(jarRequest.getCompatibleCaps(), jarRequest.getUnCompatibleCaps(), jar);

        inventoryService.newItem(jar.getId(), "jar", jar.getQuantity().intValue(), "restock", jwtService.getUserIdFromToken(token).intValue(), "Se añadio "+jar.getName()+" al inventario");

        return new JarDTO(jar);
    }

    //Re visa que si tenga un tipo de frasco asociado, dado que algunos vienen con tapa propia asi que el diametro no es obligatorio
    private void manageCompatibility(String[] compatibleCaps, String[] unCompatibleCaps, Jar jar) {
        if(jar.getJarType() != null) {
           if(compatibleCaps != null && compatibleCaps.length > 0) {

                //se crean dos listas, una con todas las tapas compatibles y otra con todas las tapas del diametro del frasco, le restamos las tapas compatibles a las del diametro del frasco
                // y asi tenemos las tapas incompatibles
                List<Cap> compatible = getCaps(compatibleCaps, jar);
                List<Cap> inCompatible = capRepository.getFromCapDiameter(jar.getJarType().getDiameter()).get();
                inCompatible.removeAll(compatible);
                addToCompatible(compatible, jar, true);
                addToCompatible(inCompatible, jar, false);


            }else if(unCompatibleCaps != null && unCompatibleCaps.length > 0) {

                List<Cap> unCompatible = getCaps(unCompatibleCaps, jar);

                List<Cap> compatible = capRepository.getFromCapDiameter(jar.getJarType().getDiameter()).get();

                compatible.removeAll(unCompatible);
                addToCompatible(compatible, jar, true);
                addToCompatible(unCompatible, jar, false);
            }
        }
    }

    //Trae todas las tapas que coincidan con los nombres y el diametro del frasco
    private List<Cap> getCaps(String[] caps, Jar jar) {
        Set<Cap> capList = new HashSet<>();

        if(caps != null && caps.length > 0) {
            for (String capName : caps) {
                Optional<List<Cap>> capOptional = capRepository.getFromNameAndDiameter(capName, jar.getJarType().getDiameter());
                if(capOptional.isPresent()) {
                    capList.addAll(capOptional.get());
                }
            }
        }

        return new ArrayList<Cap>(capList);

    }

    //Agrega las tapas a la lista de compatibilidad del frasco, si es compatible o no
    private void addToCompatible(List<Cap> compatible, Jar jar, boolean isCompatible) {
        for (Cap cap : compatible) {
            if(jarCapCompatibilityRepository.findByJarAndCap(jar.getId(), cap.getId()).isPresent()) {
                JarCapCompatibility existingCompatibility = jarCapCompatibilityRepository.findByJarAndCap(jar.getId(), cap.getId()).get();

                existingCompatibility.setCompatible(isCompatible);
                jarCapCompatibilityRepository.save(existingCompatibility);
                continue;
            }
            jarCapCompatibilityRepository.save(new JarCapCompatibility(jar, cap, isCompatible));
        }

    }


    public Page<JarDTO> getAllJars(Pageable pageable) {
        Page<Jar> jars = jarRepository.findAll(pageable);
        return jars.map(JarDTO::new);
    }

    public Page<JarDTO> getAllActiveJars(Pageable pageable) {
        Page<Jar> jars = jarRepository.getAllActiveJars(pageable).get();
        return jars.map(JarDTO::new);
    }

    public Page<JarDTO> getAllInactiveJars(Pageable pageable) {
        Page<Jar> jars = jarRepository.getAllInactiveJars(pageable).get();
        return jars.map(JarDTO::new);
    }

  public Page<JarDTO>  getFromNameLikeAndNameDiameter(String name, String diameter , Pageable pageable) {
    Page<Jar> jars = jarRepository.getFromNameLikeAndDiameter(name,diameter, pageable);
    return jars.map(JarDTO::new);
  }



  public JarDTO updateJar(JarRequestDTO jarRequestDTO, String token) {
        Optional<Jar> jarOptional = jarRepository.getByName(jarRequestDTO.getName().trim().toLowerCase());

        if(!jarOptional.isPresent()) {
            throw new IllegalArgumentException("No existe un frasco con ese id.");
        }
        Jar jar = jarOptional.get();

        if(jarRequestDTO.getDescription() != null){
            jar.setDescription(jarRequestDTO.getDescription().trim());
        }

        if(jarRequestDTO.getQuantity() != null){

            inventoryService.newItem(jar.getId(), "jar", jar.getQuantity().intValue(), "adjustment", jwtService.getUserIdFromToken(token).intValue(), "Se actualizo "+jar.getName()+" su inventario ahora es: "+jar.getQuantity());
            jar.setQuantity(jarRequestDTO.getQuantity());
        }

        if(jarRequestDTO.getUnitPrice() != null) {
            if(jarRequestDTO.getUnitPrice() <= 0) {
                throw new IllegalArgumentException("El precio unitario no puede ser negativo o cero.");
            }
            jar.setUnitPrice(BigDecimal.valueOf(jarRequestDTO.getUnitPrice()));
        }
        if(jarRequestDTO.getDocenaPrice() != null) {
            if(jarRequestDTO.getDocenaPrice() < 0) {
                throw new IllegalArgumentException("El precio de docena no puede ser negativo o cero.");
            }
            jar.setDocenaPrice(BigDecimal.valueOf(jarRequestDTO.getDocenaPrice()));
        }
        if(jarRequestDTO.getCienPrice() != null) {
            if(jarRequestDTO.getCienPrice() < 0) {
                throw new IllegalArgumentException("El precio de cien no puede ser negativo o cero.");
            }
            jar.setCienPrice(BigDecimal.valueOf(jarRequestDTO.getCienPrice()));
        }
        if(jarRequestDTO.getPacaPrice() != null) {
            if(jarRequestDTO.getPacaPrice() < 0) {
                throw new IllegalArgumentException("El precio de pacas no puede ser negativo o cero.");
            }
            jar.setPacaPrice(BigDecimal.valueOf(jarRequestDTO.getPacaPrice()));
        }
        if(jarRequestDTO.getUnitsInPaca() != null) {
            if(jarRequestDTO.getUnitsInPaca() <= 0) {
                throw new IllegalArgumentException("Las unidades en paca no pueden ser cero o negativas.");
            }
            jar.setUnitsInPaca(jarRequestDTO.getUnitsInPaca());
        }

        return new JarDTO(jarRepository.save(jar));

    }

    public JarDTO updateCompatible(String[] capNames, String jarName, boolean isCompatible) {
        Optional<Jar> jarOptional = jarRepository.getByName(jarName.trim());

        if(!jarOptional.isPresent()) {
            throw new IllegalArgumentException("No existe un frasco con ese nombre.");
        }

        //Okay, que pasa aqui,
        // Tenemos que trear todas las tapas nuevas que concidan con el arreglo de tapas que nos diernon, ese es el caps =getCaps(capNames, jar);
        // Luego tenemos que trear todas las tapas que no estan presentes en el arreglo pero que concidan en diametro
        // luego tenemos que trear todas las tapas que ya estan con la compatibilidad del frasco, porque no deseamos sobre escribirlas (a menos que esten en caps)
        // Luego tenemos que restar las tapas que ya estan presentes en el frasco, y las de caps
        // Y hacemos algo parecido al inicial, asumimos que todo es caps es lo que diga la variable isCompatible
        // y todo lo que no este en caps o ya en el frasco es lo contrario a isCompatible
        // de esta manera, si se añaden 30 tapas nuevas y solo 1 es compatible, se envia un arreglo solo con la tapa compatible y se asume que las otras 29 son incompatibles
        Jar jar = jarOptional.get();
        List<Cap> caps = getCaps(capNames, jar);
        List<Cap> capsNotPresent = capRepository.getFromCapDiameter(jar.getJarType().getDiameter()).get();
        capsNotPresent.removeAll(caps);
        List<Cap> existingCaps = jarCapCompatibilityRepository.findByJarId(jar.getId()).get();
        capsNotPresent.removeAll(existingCaps);

        jar.setUpdatedAt(LocalDateTime.now());
        addToCompatible(caps, jar, isCompatible);
        addToCompatible(capsNotPresent, jar, !isCompatible);
        return new JarDTO(jar);

    }

    public JarDTO activateJar(String jarName) {
        Optional<Jar> jarOptional = jarRepository.getByName(jarName.trim());

        if(!jarOptional.isPresent()) {
            throw new IllegalArgumentException("No existe un frasco con ese nombre.");
        }
        Jar jar = jarOptional.get();
        jar.setIsActive(true);
        return new JarDTO(jarRepository.save(jar));
    }

    public JarDTO deleteJar(String jarName) {
        Optional<Jar> jarOptional = jarRepository.getByName(jarName.trim());

        if(!jarOptional.isPresent()) {
            throw new IllegalArgumentException("No existe un frasco con ese nombre.");
        }
        Jar jar = jarOptional.get();
        jar.setIsActive(false);
        return new JarDTO(jarRepository.save(jar));
    }

    public JarDTO getJarByName(String jarName) {
        Optional<Jar> jarOptional = jarRepository.getByName(jarName.trim());

        if(!jarOptional.isPresent()) {
            throw new IllegalArgumentException("No existe un frasco con ese nombre.");
        }
        return new JarDTO(jarOptional.get());
    }

    public JarDTO changeInventory(JarRequestDTO jarRequestDTO, String token) {
        Optional<Jar> jarOptional = jarRepository.getByName(jarRequestDTO.getName().trim().toLowerCase());

        if(!jarOptional.isPresent()) {
            throw new IllegalArgumentException("No existe un frasco con ese nombre.");
        }
        Jar jar = jarOptional.get();

        if(jarRequestDTO.getQuantity() == null) {
            throw new IllegalArgumentException("La cantidad no puede ser nula.");
        }

        if(jarRequestDTO.getQuantity() < 0) {
            jar.setQuantity(jar.getQuantity() + jarRequestDTO.getQuantity());
            inventoryService.newItem(jar.getId(), "jar", jar.getQuantity().intValue(), "damage", jwtService.getUserIdFromToken(token).intValue(), "Se reporto un daño en "+jar.getName()+" su inventario ahora es: "+jar.getQuantity());
            return new JarDTO(jarRepository.save(jar));
        }

        jar.setQuantity(jarRequestDTO.getQuantity() + jar.getQuantity());
        inventoryService.newItem(jar.getId(), "jar", jar.getQuantity().intValue(), "restock", jwtService.getUserIdFromToken(token).intValue(), "Se actualizo "+jar.getName()+" su inventario ahora es: "+jar.getQuantity());

        return new JarDTO(jarRepository.save(jar));
    }

    public Page<JarDTO> getJarLikeName(String name, Pageable pageable) {
        Page<Jar> jars = jarRepository.getFromNameLike(name, pageable).get();
        return jars.map(JarDTO::new);
    }

    public Page<JarDTO> getPriceRange(PriceSearchRequest priceSearchRequest, Pageable pageable) {

        boolean exactSearch = priceService.verifyPriceSearchRequest(priceSearchRequest);

        switch (priceSearchRequest.getPriceDeal()) {
            case CIEN:
                if(exactSearch) {
                    return jarRepository.findByCienPrice(priceSearchRequest.getExactPrice(), pageable).map(JarDTO::new);
                } else {
                    return jarRepository.findByCienPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(JarDTO::new);
                }
            case DOCENA:
                if(exactSearch) {
                    return jarRepository.findByDocenaPrice(priceSearchRequest.getExactPrice(), pageable).map(JarDTO::new);
                } else {
                    return jarRepository.findByDocenaPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(JarDTO::new);
                }
            case UNIDAD:
                if(exactSearch) {
                    return jarRepository.findByUnidadPrice(priceSearchRequest.getExactPrice(), pageable).map(JarDTO::new);
                } else {
                    return jarRepository.findByUnidadPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(JarDTO::new);
                }
            case PACA:
                if(exactSearch) {
                    return jarRepository.findByPacaPrice(priceSearchRequest.getExactPrice(), pageable).map(JarDTO::new);
                } else {
                    return jarRepository.findByPacaPriceBetween(priceSearchRequest.getMinPrice(), priceSearchRequest.getMaxPrice(), pageable).map(JarDTO::new);
                }
            default:
                throw new IllegalArgumentException("Tipo de trato de precio no soportado.");
        }

    }

    public List<CapDTO> getCompatibleCaps(String jarName, Pageable pageable) {
        Optional<Jar> jarOptional = jarRepository.getByName(jarName.trim());

        if(!jarOptional.isPresent()) {
            throw new IllegalArgumentException("No existe un frasco con ese nombre.");
        }
        Jar jar = jarOptional.get();
        List<Cap> compatibilities = jarCapCompatibilityRepository.findByJarIdAndIsCompatibleUnique(jar.getId(), true);
        return compatibilities.stream().map(cap -> new CapDTO(cap)).collect(Collectors.toList());
    }

     public List<CapDTO> getIncompatibleCaps(String jarName, Pageable pageable) {
        Optional<Jar> jarOptional = jarRepository.getByName(jarName.trim());

        if(!jarOptional.isPresent()) {
            throw new IllegalArgumentException("No existe un frasco con ese nombre.");
        }

        Jar jar = jarOptional.get();

        Optional<List<Cap>> diameterCaps = capRepository.getFromCapDiameter(jar.getJarType().getDiameter());

        if(!diameterCaps.isPresent() || diameterCaps.get().isEmpty()) {
            throw new IllegalArgumentException("No existen tapas con el diámetro del frasco.");
        }

        List<Cap> allCaps = diameterCaps.get();

        List<Cap> compatibilities = jarCapCompatibilityRepository.findByJarIdAndIsCompatibleList(jar.getId(), true).get();

        allCaps.removeAll(compatibilities);

        List<Cap> finalIncompatibles = new ArrayList<>(allCaps);


         return new ArrayList<>(finalIncompatibles.stream()
                 .map(CapDTO::new)
                 .collect(Collectors.toMap(
                         CapDTO::getName,            // clave: el nombre
                         cap -> cap,                 // valor: el propio objeto
                         (existing, replacement) -> existing // si hay duplicado, conserva el primero
                 ))
                 .values());
    }


}
