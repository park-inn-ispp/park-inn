package com.parkinn.model;

import java.util.List;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    private String latitud;
    private String longitud;
    private Boolean estaDisponible;
    private Boolean esAireLibre;
    private String descripcion;
    //private List<List<String>> horarios;
    private Boolean tramos;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Client administrador;

	public Boolean getTramos() {
		return tramos;
	}

	public void setTramos(Boolean tramos) {
		this.tramos = tramos;
	}
	/*
	public List<List<String>> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<List<String>> horarios) {
		this.horarios = horarios;
	}
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

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}
}