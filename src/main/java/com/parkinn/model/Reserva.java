package com.parkinn.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue
    private Long id;

    private Status estado;
    private Double precioTotal;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaSolicitud;
    private String comentarios;
    private String incidencias;

    @ManyToOne
    @JoinColumn(name="plaza_id")
    private Plaza plaza;

    @ManyToOne
    @JoinColumn(name="user_id")
    private Client user;

    public Long getId(){
        return this.getId();
    }

    public Status getStatus(){
        return this.getStatus();
    }

    public Double getPrecioTotal(){
        return this.getPrecioTotal();
    }

    public LocalDate getFechaInicio(){
        return this.getFechaInicio();
    }

    public LocalDate getFechaFin(){
        return this.getFechaFin();
    }

    public LocalDate getFechaSolicitud(){
        return this.getFechaSolicitud();
    }

    public String getComentarios(){
        return this.getComentarios();
    }

    public String getIncidencias(){
        return this.getIncidencias();
    }

    public Plaza getPlaza(){
        return this.getPlaza();
    }

    public Client getUser(){
        return this.getUser();
    }

    
    
}
