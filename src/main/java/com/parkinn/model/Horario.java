package com.parkinn.model;

import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.type.TrueFalseType;

@Entity
@Table(name = "horarios")
public class Horario {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Boolean activo;

    public Horario() {}

    public Horario(Long id) {
        this.id = id;
    }

    
    public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Plaza getPlaza() {
		return plaza;
	}
	public void setPlaza(Plaza plaza) {
		this.plaza = plaza;
	}
	@ManyToOne(optional = true)
    @JoinColumn(name = "plaza_id")
    private Plaza plaza;
    /*
    @OneToOne
    @JoinColumn(name = "plaza_id")
    private Plaza plaza;
    */
    /*
    public Horario(LocalDateTime fechaInicio,LocalDateTime fechaFin) {
    	this.fechaInicio = fechaInicio;
    	this.fechaFin = fechaFin;
    }*/
    
	public LocalDateTime getFechaInicio() {
        return this.fechaInicio;
    }
    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio=fechaInicio;
    }
    public LocalDateTime getFechaFin() {
        return this.fechaFin;
    }
    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin=fechaFin;
    }
}
