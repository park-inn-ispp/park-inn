package com.parkinn.model;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "horarios")
public class Horario {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    /*
    @ManyToOne()
    @JoinColumn(name = "plaza_id")
    private Plaza plaza;
	*/
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