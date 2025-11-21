package com.sipcommb.envases.service;

import java.util.List;

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
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public ClientDTO addClient(ClientDTO cliente) {

        if(cliente.getName() == null || cliente.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        cliente.setName(cliente.getName().trim());

        if (cliente.getDocument() == null || cliente.getDocument().isEmpty()) {
            throw new IllegalArgumentException("El documento no puede estar vacío");
        }

        cliente.setDocument(cliente.getDocument().trim());
        if (clientRepository.findByDocument(cliente.getDocument()).isPresent()) {
            throw new IllegalArgumentException(
                    "El documento " + cliente.getDocument() + " ya se encuentra registrado");
        }

        Client clienteNuevo = new Client(cliente.getName(), cliente.getAddress(), cliente.getPhone(),
                cliente.getDescription(), cliente.getDocument());

        clientRepository.save(clienteNuevo);

        return cliente;
    }

    public ClientDTO updateClient(ClientRequestDTO cliente) {

        cliente.setNameOriginal(cliente.getNameOriginal().trim());

        Client clientOriginal = clientRepository.findByDocument(cliente.getDocument())
                .orElseThrow(
                        () -> new IllegalArgumentException("El cliente " + cliente.getNameOriginal() + " con el document " + cliente.getDocument() + " no existe"));

        if (cliente.getNameNew() != null && !cliente.getNameNew().isEmpty()) {
            if (clientRepository.findByName(cliente.getNameNew()).isPresent()) {
                throw new IllegalArgumentException("Ya existe un cliente con el nombre " + cliente.getNameNew());
            } else {
                clientOriginal.setName(cliente.getNameNew());
            }
        }

        if (cliente.getAddress() != null && !cliente.getAddress().isEmpty()) {
            clientOriginal.setAddress(cliente.getAddress());
        }

        if (cliente.getDescription() != null && !cliente.getDescription().isEmpty()) {
            clientOriginal.setDescription(cliente.getDescription());
        }

        if (cliente.getPhone() != null && !cliente.getPhone().isEmpty()) {
            clientOriginal.setPhone(cliente.getPhone());
        }

        if (cliente.getIsActive() != null) {
            clientOriginal.setIs_active(cliente.getIsActive());
        }

        if (cliente.getDocument() != null && !cliente.getDocument().isEmpty()) {
            clientOriginal.setDocument(cliente.getDocument());
        }

        clientRepository.save(clientOriginal);

        return new ClientDTO(clientOriginal);

    }

    public ClientDTO changeState(String document, Boolean state) {

        String trimmedDocument = document.trim();

        Client client = clientRepository.findByDocument(trimmedDocument)
                .orElseThrow(() -> new IllegalArgumentException("El cliente " + trimmedDocument + " no existe"));

        if (client.getIs_active().equals(state)) {
            String estado = state ? "activo" : "inactivo";
            throw new IllegalArgumentException("El cliente " + trimmedDocument + " ya se encuentra " + estado);
        }

        client.setIs_active(state);

        clientRepository.save(client);

        return new ClientDTO(client);
    }

    public Page<ClientDTO> getAllClients(Pageable pageable, String name) {
        return clientRepository.findAllByName(pageable, name).map(ClientDTO::new);
    }

    public Page<ClientDTO> getAllClientsActive(Pageable pageable) {
        return clientRepository.findAllActive(pageable, true).map(ClientDTO::new);
    }

    public Page<ClientDTO> getAllClientsInActive(Pageable pageable) {
        return clientRepository.findAllActive(pageable, false).map(ClientDTO::new);
    }

    public Page<ClientDTO> getClientsLikeName(Pageable pageable, String name) {
        return clientRepository.findLikeName(pageable, name).map(ClientDTO::new);
    }

    public Client getClientByName(String name) {
        return clientRepository.findByName(name.toLowerCase().trim())
                .orElseThrow(() -> new IllegalArgumentException("El cliente " + name + " no existe"));
    }

    public List<Client> getClientsLikeName(String name) {
        return clientRepository.findLikeName(name.toLowerCase().trim());
    }

}