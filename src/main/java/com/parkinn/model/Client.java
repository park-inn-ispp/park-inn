package com.parkinn.model;

import java.util.List;

import javax.persistence.*;


@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;

    @OneToMany(mappedBy = "administrador")
    private List<Plaza> plazas;

    public Long getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getEmail() {
        return this.email;
    }
    public void setName(String name) {
        this.name=name;
    }
    public void setEmail(String email) {
        this.email=email;
    }


    // getter, setters, contructors
}