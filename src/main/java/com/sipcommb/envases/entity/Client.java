package com.sipcommb.envases.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clientes")
public class Client {

    @Id
    private Long client_id;

    private String name;

    private String address;

    private String phone;

    private String description; 

    private Boolean is_active = true;

    public Client() { }

    public Client(Long client_id, String name, String address, String phone, String description) {
        this.client_id = client_id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.description = description;
    }

    public Long getClient_id() { return client_id; }
    public void setClient_id(Long client_id) { this.client_id = client_id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIs_active() { return is_active; }
    public void setIs_active(Boolean is_active) { this.is_active = is_active; }

    
}
