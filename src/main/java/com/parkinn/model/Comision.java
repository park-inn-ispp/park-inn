package com.parkinn.model;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;



@Entity
@Table(name = "comision")
public class Comision {
    
    @Id
    private Long id;

    @Min(0)
    @Max(1)
    private float porcentaje;

    public Comision(){
    }

    public Comision(long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(float porcentaje) {
        this.porcentaje = porcentaje;
    }

    @Override
    public String toString() {
        return "Comision [id=" + id + ", porcentaje=" + porcentaje + "]";
    }


    

}