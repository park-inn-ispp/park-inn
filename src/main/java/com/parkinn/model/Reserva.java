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
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue
    private Long id;

    private String paypal_order_id;

    private Long propietarioId;
   
  
    private Estado estado;
    private Double precioTotal;
    @NotNull
    private LocalDateTime fechaInicio;
    @NotNull
    private LocalDateTime fechaFin;
    private LocalDateTime fechaSolicitud;
    private String comentarios;
    private Double fianza;
    private String direccion;
    
    public Double getFianza() {
		return fianza;
	}
    private Float comision;


	public void setFianza(Double fianza) {
		this.fianza = fianza;
	}

    public Long getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(Long propietarioId) {
        this.propietarioId = propietarioId;
    }

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	@ManyToOne(optional = true)
    @JoinColumn(name="plaza_id")
    private Plaza plaza;

    @ManyToOne(optional = true)
    @JoinColumn(name="user_id")
    private Client user;

	public Long getId() {
        return id;
    }

    public Float getComision() {
        return comision;
    }

    public void setComision(Float comision) {
        this.comision = comision;
    }

    public Estado getEstado() {
        return estado;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public Client getUser() {
        return user;
    }

    public void setUser(Client u) {
        this.user = u;
    }

    public Plaza getPlaza() {
        return plaza;
    }

    public void setPlaza(Plaza plaza) {
        this.plaza = plaza;
    }
    
    public String getPaypal_order_id() {
		return paypal_order_id;
	}

	public void setPaypal_order_id(String paypal_order_id) {
		this.paypal_order_id = paypal_order_id;
	}

}