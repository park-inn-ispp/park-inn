package com.parkinn.web;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.parkinn.model.Estado;
import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.Amount;
import com.parkinn.model.paypal.PayPalClasses;
import com.parkinn.model.paypal.PurchaseUnit;

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
	    public Reserva detallesReserva(@PathVariable Long id){
	    	return reservaService.findById(id);
	    }	

		@GetMapping("/{id}/aceptar")
	    public Object aceptarReserva(@PathVariable Long id){
			Reserva reserva = reservaService.findById(id);
			if(reserva.getPlaza().getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
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
			if(reserva.getPlaza().getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
				return reservaService.rechazarReserva(id);
			}else{
				Map<String,Object> response = new HashMap<>();
        		response.put("reserva", reserva);
				response.put("error","Esta reserva no es sobre una plaza de tu propiedad");
				return ResponseEntity.badRequest().body(response);
			}
	    }
		



	@GetMapping("/{id}/confirmar")
    public Object confirmarServicio(@PathVariable Long id){
		Reserva r = reservaService.findById(id);
		Map<String,Object> response = new HashMap<>();
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(user.equals(r.getUser().getEmail()) || user.equals(r.getPlaza().getAdministrador().getEmail())){
			if(r.getFechaFin().isAfter(LocalDateTime.now())){
				response.put("reserva", r);
				response.put("error","No puede confirmar esta reserva ya que todavía no ha finalizado");
				return ResponseEntity.badRequest().body(response);
			}else if(r.getEstado().equals(Estado.denegada) || r.getEstado().equals(Estado.confirmadaAmbos)){
				response.put("reserva", r);
				response.put("error","Esta reserva está en un estado final");
				return ResponseEntity.badRequest().body(response);
			}else{
				return reservaService.confirmarServicio(r, user);
			}
		}else{
			response.put("reserva", r);
			response.put("error","No estás involucrado en esta reserva");
			return ResponseEntity.badRequest().body(response);
		}
    }
	
	@GetMapping("/{id}/cancelar")
    public Object cancelarReserva(@PathVariable Long id){
		
		Reserva reserva = reservaService.findById(id);
		Map<String,Object> response = new HashMap<>();
		response.put("reserva", reserva);
		if(reserva.getEstado().equals(Estado.cancelada) || reserva.getEstado().equals(Estado.rechazada) || reserva.getEstado().equals(Estado.denegada)) {
			response.put("error", "No se puede cancelar una reserva que ya no está en proceso");
			return ResponseEntity.badRequest().body(response);
		}
		
		Long periodo = Duration.between(LocalDateTime.now(), reserva.getFechaInicio()).toMinutes();
		
		if(reserva.getPlaza().getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
			return reservaService.devolverTodo(reserva);
		}else if(!reserva.getUser().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
			response.put("error","No estás involucrado en esta reserva");
			return ResponseEntity.badRequest().body(response);
		}else if(!LocalDateTime.now().isBefore(reserva.getFechaInicio())){
				response.put("error","Esta reserva no se puede cancelar, la reserva ya ha empezado");
				return ResponseEntity.badRequest().body(response);
		}else if(periodo<1440) {
				return reservaService.devolverSinFianza(reserva);
		}else {
			return reservaService.devolverTodo(reserva);
				}
		}
    

	@GetMapping("/{id}/denegar")
    public Object denegarServicio(@PathVariable Long id){
		Reserva r = reservaService.findById(id);
		Map<String,Object> response = new HashMap<>();
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(user.equals(r.getUser().getEmail()) || user.equals(r.getPlaza().getAdministrador().getEmail())){
			if(r.getFechaFin().isBefore(LocalDateTime.now())){
				return reservaService.denegarServicio(r);
			}else{
				response.put("reserva", r);
				response.put("error","No puede denegar esta reserva ya que todavía no ha finalizado");
				return ResponseEntity.badRequest().body(response);
			}

		}else{
			response.put("reserva", r);
			response.put("error","No estás involucrado en esta reserva");
			return ResponseEntity.badRequest().body(response);
		}
    }
}
