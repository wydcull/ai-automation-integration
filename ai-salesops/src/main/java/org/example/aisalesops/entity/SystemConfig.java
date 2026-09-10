package org.example.aisalesops.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "system_config")
public class SystemConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "key", nullable = false, unique = true, length = 64)
    private String key;


    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;


    public SystemConfig() {
    }


    public SystemConfig(String key, String value) {
        this.key = key;
        this.value = value;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }
}