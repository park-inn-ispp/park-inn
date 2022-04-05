package com.parkinn.web;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkinn.model.Reserva;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

	@RestController
	@RequestMapping("/reservas")
	public class ReservaController {

		@Autowired
		private HorarioRepository horarioRepository;
		
	    @Autowired
	    private ReservaService reservaService;

	    @GetMapping("/usuario/{id}")
	    public List<Reserva> reservasUsuario(@PathVariable Long id){
	    	return reservaService.findByUserId(id);
	    }
	    
	    @GetMapping("/plaza/{id}")
	    public List<Reserva> ReservasPlaza(@PathVariable Long id){
	    	return reservaService.findPlazaById(id);
	    }
		
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
		@GetMapping("/all")
	    public List<Reserva> findAll(){
	    	return reservaService.findAll();
	    }
	    @GetMapping("/{id}/fechasNoDisponibles")
	    public List<List<LocalDateTime>> horariosNoDisponibles(@PathVariable Long id) throws URISyntaxException {
	    	return reservaService.horariosNoDisponibles(id);
	    }
	    /*
	    @GetMapping("/{id}/disponibilidad")
	    public List<Horario> horariosPlaza(@PathVariable Long id) throws URISyntaxException {
	    	return reservaService.horariosDisponibles(id);
	    }*/
	    @GetMapping("/{id}")
	    public Object detallesReserva(@PathVariable Long id){
			Reserva r = reservaService.findById(id);
			Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains("ROLE_ADMIN") || r.getUser().getEmail().equals(user) || r.getPlaza().getAdministrador().getEmail().equals(user)){
				return r;
			}else{
				Map<String,Object> response = new HashMap<>();
				response.put("error","No estás involucrado en esta reserva");
				return ResponseEntity.badRequest().body(r);
			}
	    }	

		@GetMapping("/{id}/aceptar")
	    public Object aceptarReserva(@PathVariable Long id){
			Reserva reserva = reservaService.findById(id);
			if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains("ROLE_ADMIN") || reserva.getPlaza().getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
				return reservaService.aceptarReserva(id);
			}else{
				Map<String,Object> response = new HashMap<>();
        		response.put("reserva", reserva);
				response.put("error","Esta reserva no es sobre una plaza de tu propiedad");
				return ResponseEntity.badRequest().body(response);
			}
	    }

		@GetMapping("/{id}/rechazar")
	    public Object rechazarReserva(@PathVariable Long id){
			Reserva reserva = reservaService.findById(id);
			if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains("ROLE_ADMIN") ||reserva.getPlaza().getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
				return reservaService.rechazarReserva(id);
			}else{
				Map<String,Object> response = new HashMap<>();
        		response.put("reserva", reserva);
				response.put("error","Esta reserva no es sobre una plaza de tu propiedad");
				return ResponseEntity.badRequest().body(response);
			}
	    }
	}
