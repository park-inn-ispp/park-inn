package com.parkinn.model;
import java.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="parks")
public class Park {
    
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String calle;
    @NotBlank
    private String numero;
    @NotBlank
    private String ciudad;
    @NotBlank
    private String provincia;
    @NotBlank
    private String codigoPostal;

    @NotBlank
    private Double precioHora;
    @NotBlank
    private Integer fianza;
    @NotBlank
    private Double ancho;
    @NotBlank
    private Double largo;
    @NotBlank
    private Boolean exterior;
    @NotBlank
    private String descripcion;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern =  "yyyy-MM-dd HH:mm" , timezone = "GMT+1")
    private LocalDateTime fechaInicio;

     
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern =  "yyyy-MM-dd HH:mm" , timezone = "GMT+1")
    private LocalDateTime fechaFin;

    public Long getId() {
        return this.id;
    }
    public String getCalle() {
        return this.calle;
    }
    public String getNumero() {
        return this.numero;
    }
    public String getCiudad() {
        return this.ciudad;
    }
    public String getProvincia() {
        return this.provincia;
    }
    public String getCodigoPostal() {
        return this.codigoPostal;
    }
    public Double getPrecioHora(){
        return this.precioHora;
    }
    public Integer getFianza(){
        return this.fianza;
    }
    public Double getAncho(){
        return this.ancho;
    }
    public Double getLargo(){
        return this.largo;
    }
    public Boolean getExterior(){
        return this.exterior;
    }
    public String getDescripcion(){
        return this.descripcion;
    }
    public void setDireccion(String calle) {
        this.calle=calle;
    }
    public void setPrecioHora(Double precioHora) {
        this.precioHora=precioHora;
    } 
    public void setFianza(Integer fianza) {
        this.fianza=fianza;
    }
    public void setAncho(Double ancho) {
        this.ancho=ancho;
    }
    public void setLargo(Double largo) {
        this.largo=largo;
    }
    public void setExterior(Boolean exterior) {
        this.exterior=exterior;
    } 
    public void setDescripcion(String descripcion) {
        this.descripcion=descripcion;
    }

    @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Client client;

    
}