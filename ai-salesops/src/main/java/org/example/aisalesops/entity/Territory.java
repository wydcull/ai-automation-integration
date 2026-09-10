package org.example.aisalesops.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "territories")
public class Territory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "region_code", nullable = false, unique = true, length = 64)
    private String regionCode;

    @Column(nullable = false)
    private Boolean active = true;


    public Territory() {
    }


    public Territory(String name, String regionCode, Boolean active) {
        this.name = name;
        this.regionCode = regionCode;
        this.active = active;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getRegionCode() {
        return regionCode;
    }


    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }


    public Boolean getActive() {
        return active;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }
}