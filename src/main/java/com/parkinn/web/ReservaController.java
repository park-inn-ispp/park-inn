package com.parkinn.web;

	import java.net.URI;
	import java.net.URISyntaxException;
	import java.time.LocalDateTime;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	import javax.validation.Valid;

import com.parkinn.model.Estado;
import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
	import com.parkinn.model.Reserva;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.service.PlazaService;
	import com.parkinn.service.ReservaService;


	import org.springframework.web.bind.annotation.DeleteMapping;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.PutMapping;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.RestController;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.format.annotation.DateTimeFormat;
	import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

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

		@GetMapping("/{id}/confirmar")
	    public Object confirmarServicio(@PathVariable Long id){
			Reserva r = reservaService.findById(id);
			Map<String,Object> response = new HashMap<>();
			Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(user.equals(r.getUser().getEmail()) || user.equals(r.getPlaza().getAdministrador().getEmail())){
				if(r.getFechaFin().isAfter(LocalDateTime.now())){
					return reservaService.confirmarServicio(r, user);
				}else{
					response.put("reserva", r);
					response.put("error","No puede confirmar esta reserva ya que todavía no ha finalizado");
					return ResponseEntity.badRequest().body(response);
				}
			}else{
				response.put("reserva", r);
				response.put("error","No estás involucrado en esta reserva");
				return ResponseEntity.badRequest().body(response);
			}
			
	    }
	}
