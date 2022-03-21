package com.parkinn.model;

import java.util.Collection;

import javax.persistence.*;

@Entity
@Table(name = "plazas")
public class Plaza {

    @Id
    @GeneratedValue
    private Long id;

    private String direccion;
    private Double precioHora;
    private Double fianza;
    private Double ancho;
    private Double largo;
    private Boolean estaDisponible;
    private Boolean esAireLibre;
    private String descripcion;

    /*@OneToMany(mappedBy="plaza", cascade = CascadeType.ALL)
    private Collection<Horario> horarios;
	*/
    /*
    @OneToOne(mappedBy = "plaza", cascade = CascadeType.ALL)
    private Horario horario;
    */
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
    public String getDireccion() {
        return this.direccion;
    }
    public void setDireccion(String direccion) {
        this.direccion=direccion;
    }
    public Double getPrecioHora() {
        return this.precioHora;
    }
    public void setPrecioHora(Double precioHora) {
        this.precioHora=precioHora;
    }
    public Double getFianza() {
        return this.fianza;
    }
    public void setFianza(Double fianza) {
        this.fianza=fianza;
    }
    public Double getAncho() {
        return this.ancho;
    }
    public void setAncho(Double ancho) {
        this.ancho=ancho;
    }
    public Double getLargo() {
        return this.largo;
    }
    public void setLargo(Double largo) {
        this.largo=largo;
    }
    public Boolean getEstaDisponible() {
        return this.estaDisponible;
    }
    public void setEstaDisponible(Boolean estaDisponible) {
        this.estaDisponible=estaDisponible;
    }
    public Boolean getEsAireLibre() {
        return this.esAireLibre;
    }
    public void setEsAireLibre(Boolean esAireLibre) {
        this.esAireLibre=esAireLibre;
    }
    public String getDescripcion() {
        return this.descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion=descripcion;
    }
    /*
    public Collection<Horario> getHorarios() {
        return this.horarios;
    }*/
    /*
    public Horario getHorario() {
    	return this.horario;
    }*/
}