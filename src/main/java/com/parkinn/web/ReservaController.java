package com.parkinn.web;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import com.parkinn.model.Reserva;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
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

		//METER SEGURIDAD PARA COMPROBAR QUE ESTA LOGUEADO EL PROPIETARIO DE LA PLAZA
		@GetMapping("/{id}/aceptar")
	    public Reserva aceptarReserva(@PathVariable Long id){
	    	return reservaService.aceptarReserva(id);
	    }

		//METER SEGURIDAD PARA COMPROBAR QUE ESTA LOGUEADO EL PROPIETARIO DE LA PLAZA
		@GetMapping("/{id}/rechazar")
	    public Reserva rechazarReserva(@PathVariable Long id){
	    	return reservaService.rechazarReserva(id);
	    }
	}
