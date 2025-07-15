package com.sipcommb.envases.dto;

public class UpdateCompatibleCapsRequest {
    
    private String name;
    private String[] caps;
    private boolean active;

    public UpdateCompatibleCapsRequest() {
    }
    public UpdateCompatibleCapsRequest(String name, String[] caps, boolean active) {
        this.name = name;
        this.caps = caps;
        this.active = active;  
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String[] getCaps() {
        return caps;
    }

    public void setCaps(String[] caps) {
        this.caps = caps;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


}
