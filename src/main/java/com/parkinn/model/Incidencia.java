package com.parkinn.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "incidencias")
public class Incidencia {

    @Id
    @GeneratedValue
    private Long id;
    private EstadoIncidencia estado;
    @NotNull
    private LocalDateTime fecha;
    private String titulo;
    private String descripcion;
   
    @ManyToOne(optional = true)
    @JoinColumn(name="reserva_id")
    private Reserva idReserva;

    @ManyToOne(optional = true)
    @JoinColumn(name="user_id")
    private Client email;

    
    public Incidencia(){

    }
    public Incidencia(Long id) {
        this.id = id;
    }

    public Reserva getIdReserva() {
		return idReserva;
	}

	public void setIdReserva(Reserva idReserva) {
		this.idReserva = idReserva;
	}

	public Client getEmail() {
		return email;
	}

	public void setEmail(Client email) {
		this.email = email;
	}

	public Reserva getReserva() {
        return this.idReserva;
    }

    public void setReserva(Reserva reserva) {
        this.idReserva = reserva;
    }

    public Client getUser() {
        return this.email;
    }

    public void setUser(Client user) {
        this.email = user;
    }
 
    public Long getId() {
        return this.id;
    }


    public EstadoIncidencia getEstado() {
        return this.estado;
    }

    public void setEstado(EstadoIncidencia estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return this.fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString(){
        return this.descripcion + " | " + this.titulo + " | " + this.estado + " | " + this.email + " | " + this.idReserva;   
     }    
      
}