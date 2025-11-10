package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Client;

public class ClientDTO {

    private String name;

    private String address;

    private String phone;

    private String description;

    private boolean isActive;

    private String document;

    public ClientDTO(){

    }

    public ClientDTO(Client client){
        this.name = client.getName();
        this.address = client.getAddress();
        this.phone = client.getPhone();
        this.description = client.getDescription();
        this.isActive = client.getIs_active();
        this.document = client.getDocument();
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getAddress(){
        return this.address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getPhone(){
        return this.phone;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public boolean getIsActive(){
        return this.isActive;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }
    
}
