package org.example.aisalesops.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, length = 120)
    private String name;


    @Column(length = 64)
    private String model;


    @Column(length = 64)
    private String category;


    @Column(nullable = false)
    private Boolean active = true;


    public Product() {
    }


    public Product(
            String name,
            String model,
            String category,
            Boolean active
    ) {
        this.name = name;
        this.model = model;
        this.category = category;
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


    public String getModel() {
        return model;
    }


    public void setModel(String model) {
        this.model = model;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    public Boolean getActive() {
        return active;
    }


    public void setActive(Boolean active) {
        this.active = active;
    }
}