package com.sipcommb.envases.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "jar_cap_compatibility")
public class JarCapCompatibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jar_id", nullable = false)
    private Jar jar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cap_id", nullable = false)
    private Cap cap;

    @Column(name = "is_compatible", nullable = false)
    private boolean isCompatible;

    // Constructors
    public JarCapCompatibility() {
    }
    public JarCapCompatibility(Jar jar, Cap cap, boolean isCompatible) {
        this.jar = jar;
        this.cap = cap;
        this.isCompatible = isCompatible;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Jar getJar() {
        return jar;
    }

    public void setJar(Jar jar) {
        this.jar = jar;
    }

    public Cap getCap() {
        return cap;
    }

    public void setCap(Cap cap) {
        this.cap = cap;
    }

    public boolean isCompatible() {
        return isCompatible;
    }

    public void setCompatible(boolean compatible) {
        isCompatible = compatible;
    }

}


