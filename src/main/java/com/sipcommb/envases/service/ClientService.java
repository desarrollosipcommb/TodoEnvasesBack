package com.sipcommb.envases.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sipcommb.envases.dto.ClientDTO;
import com.sipcommb.envases.dto.ClientRequestDTO;
import com.sipcommb.envases.entity.Client;
import com.sipcommb.envases.repository.ClientRepository;

@Service
@Transactional
public class ClientService{

    @Autowired
    private ClientRepository clientRepository;

    public ClientDTO addClient(ClientDTO cliente){

        cliente.setName(cliente.getName().trim());
        
        if(clientRepository.findByName(cliente.getName().toLowerCase()).isPresent()){
            throw new IllegalArgumentException("El cliente "+ cliente.getName()+ " ya existe");
        }

        Client clienteNuevo = new Client(cliente.getName(), cliente.getAddress(), cliente.getPhone(), cliente.getDescription());

        clientRepository.save(clienteNuevo);

        return cliente;
    }

    public ClientDTO updateClient(ClientRequestDTO cliente){

        cliente.setNameOriginal(cliente.getNameOriginal().trim());
    
        Client clientOriginal = clientRepository.findByName(cliente.getNameOriginal())
                .orElseThrow(() -> new IllegalArgumentException("El cliente "+ cliente.getNameOriginal() +" no existe"));

        if(cliente.getNameNew() != null && !cliente.getNameNew().isEmpty()){
            if(clientRepository.findByName(cliente.getNameNew()).isPresent()){
                throw new IllegalArgumentException("Ya existe un cliente con el nombre "+cliente.getNameNew());
            }else{
                clientOriginal.setName(cliente.getNameNew());
            }
        }

        if(cliente.getAddress() !=null && !cliente.getAddress().isEmpty()){
            clientOriginal.setAddress(cliente.getAddress());
        }

        if(cliente.getDescription() !=null && !cliente.getDescription().isEmpty()){
            clientOriginal.setDescription(cliente.getDescription());
        }

        if(cliente.getPhone() != null && !cliente.getPhone().isEmpty()){
            clientOriginal.setPhone(cliente.getPhone());
        }

        if(cliente.getIsActive() !=null){
            clientOriginal.setIs_active(cliente.getIsActive());
        }

        clientRepository.save(clientOriginal);

        return new ClientDTO(clientOriginal);

    }

    public Page<ClientDTO> getAllClients(Pageable pageable){
        return clientRepository.findAll(pageable).map(ClientDTO::new);
    }

    public Page<ClientDTO> getAllClientsActive(Pageable pageable){
        return clientRepository.findAllActive(pageable, true).map(ClientDTO::new);
    }

    public Page<ClientDTO> getAllClientsInActive(Pageable pageable){
        return clientRepository.findAllActive(pageable, false).map(ClientDTO::new);
    }

    public Page<ClientDTO> getClientsLikeName(Pageable pageable, String name){
        return clientRepository.findLikeName(pageable, name).map(ClientDTO::new);
    }

    public Client getClientByName(String name){
        return clientRepository.findByName(name.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("El cliente "+ name +" no existe"));
    }

}