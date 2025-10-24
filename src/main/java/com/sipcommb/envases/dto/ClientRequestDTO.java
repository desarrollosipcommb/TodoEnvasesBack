package com.sipcommb.envases.dto;

import com.sipcommb.envases.entity.Client;

public class ClientRequestDTO {
    
    private String nameOriginal;

    private String nameNew;

    private String address;

    private String phone;

    private String description;

    private Boolean isActive;

    public ClientRequestDTO(){

    }

    public ClientRequestDTO(Client client){
        this.nameOriginal = client.getName();
        this.address = client.getAddress();
        this.phone = client.getPhone();
        this.description = client.getDescription();
        this.isActive = client.getIs_active();
    }

    public String getNameOriginal(){
        return this.nameOriginal;
    }

    public void setNameOriginal(String name){
        this.nameOriginal = name;
    }

    public String getNameNew(){
        return this.nameNew;
    }

    public void setNameNew(String name){
        this.nameNew = name;
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

    public Boolean getIsActive(){
        return this.isActive;
    }

    public void setIsActive(boolean isActive){
        this.isActive = isActive;
    }
}
