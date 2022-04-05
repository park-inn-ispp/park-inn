package com.parkinn.service;

import com.parkinn.repository.ClientRepository;
import com.parkinn.repository.IncidenciaRepository;

import java.time.LocalDateTime;
import java.util.List;

import com.parkinn.model.Estado;
import com.parkinn.model.Incidencia;
import com.parkinn.model.Reserva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaService {
    
    @Autowired
    private IncidenciaRepository repository;

	@Autowired
    private ReservaService reservaService;

	@Autowired
    private ClientRepository clientRepository;
    
    
	public List<Incidencia> findAll(){
        return repository.findAll();
    }

	  public Incidencia findIncidenciaById(Long id){
    	Incidencia incidencias = repository.findById(id).orElse(null);
        return incidencias;
    }

	public boolean comprobarConfirmacion(Incidencia r){
		Reserva reserva = reservaService.findById(r.getReserva().getId());
		return reserva.getEstado() == Estado.confirmadaAmbos;

	}
	public boolean comprobarCliente(Incidencia r){
		String creadorIncidenciaFromFront  = r.getUser().getEmail();
		String creadorIncidenciaByToken  = SecurityContextHolder.getContext().getAuthentication().getName();
		Reserva reserva = reservaService.findById(r.getReserva().getId());

		String duenoPlaza = reserva.getPlaza().getAdministrador().getEmail();
		String clientePlaza = reserva.getUser().getEmail();

		//Compruebo que el user que me manda el front es el mismo que tiene sesión iniciada
		if(!creadorIncidenciaByToken.equals(creadorIncidenciaFromFront)){
			return false;
		}

		//Compruebo que el usuario que realiza la incidencia o es el dueño o es el cliente.
		
		return creadorIncidenciaFromFront.equals(duenoPlaza) || creadorIncidenciaFromFront.equals(clientePlaza);
	}


    public Incidencia guardarIncidencia(Incidencia r){
        r.setFecha(LocalDateTime.now());
		String email = (r.getUser() == null) ? "sinmail" : r.getUser().getEmail();
		Long idReserva = (r.getReserva() == null) ? -1 : r.getReserva().getId();
		if(!email.equals("sinmail")){
			r.setUser(clientRepository.findByEmail(email).get());
		}
		if(idReserva != -1){
			r.setReserva(reservaService.findById(idReserva));
		}
        Incidencia incidencia = repository.save(r);
        return incidencia;
    }

}
