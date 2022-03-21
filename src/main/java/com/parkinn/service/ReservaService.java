package com.parkinn.service;

import com.parkinn.repository.ReservaRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.parkinn.model.Estado;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservaService {
    
    @Autowired
    private ReservaRepository repository;

    public Reserva guardarReserva(Reserva r){
        r.setEstado(Estado.aceptada);
        r.setFechaSolicitud(LocalDateTime.now());
        Double precio = Duration.between(r.getFechaInicio(), r.getFechaFin()).toHours() * r.getPlaza().getPrecioHora();
        r.setPrecioTotal(precio);
        Reserva reserva = repository.save(r);
        return reserva;
    }
    
    public List<Reserva> findPlazaById(Long id){
    	List<Reserva> reservas = repository.findByPlazaId(id);
        return reservas;
    }

    public List<Reserva> findByUserId(Long id){
        List<Reserva> reservas= repository.findByUserId(id);
        return reservas;
        
    }
    public Reserva findById(Long id) {
    	Optional<Reserva> reserva = repository.findById(id);
    	return reserva.orElse(null);
    }
} 
