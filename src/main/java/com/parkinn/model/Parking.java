package com.parkinn.model;

import javax.persistence.*;

@Entity
@Table(name = "parking")
public class Parking {

    @Id
    @GeneratedValue
    private Long id;

    private String direccion;
    private Integer precioHora;
    private Integer fianza;
    private double ancho;
    private double largo;
    private boolean disponible;
    private boolean aireLibre;
    private String descripcion;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Client administrador;

    public Client getAdministrador() {
        return this.administrador;
    }

    public void setAdministrador(Client administrador) {
        this.administrador = administrador;
    }
    
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdress() {
        return this.direccion;
    }

    public void setAdress(String adress) {
        this.direccion = adress;
    }

    public Integer getPrecioHora() {
        return this.precioHora;
    }

    public void setPrecioHora(Integer precioHora) {
        this.precioHora = precioHora;
    }

    public Integer getFianza() {
        return this.fianza;
    }

    public void setFianza(Integer fianza) {
        this.fianza = fianza;
    }

    public double getAncho() {
        return this.ancho;
    }

    public void setAncho(double ancho) {
        this.ancho = ancho;
    }

    public double getLargo() {
        return this.largo;
    }

    public void setLargo(double largo) {
        this.largo = largo;
    }

    public boolean isDisponible() {
        return this.disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public boolean isAireLibre() {
        return this.aireLibre;
    }

    public void setAireLibre(boolean aireLibre) {
        this.aireLibre = aireLibre;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



  


    // getter, setters, contructors
}