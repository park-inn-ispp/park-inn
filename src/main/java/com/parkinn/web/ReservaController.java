package com.parkinn.web;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.javascript.host.fetch.Response;
import com.parkinn.model.Client;
import com.parkinn.model.Estado;
import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.service.ClientService;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.service.MailService;
import com.parkinn.service.PlazaService;
import com.parkinn.service.ReservaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservas")
public class ReservaController {
	
	final static String URL_CORREO = "https://park-inn-ispp-fe.herokuapp.com";

	@Autowired
	private ReservaService reservaService;
	
	@Autowired
	private PlazaService plazaService;
	
	@Autowired
	private ClientService clientService;
	
    @Autowired
    private MailService mailService;

	@Autowired
	private HorarioRepository horarioRepository;

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/usuario/{id}")
	public Object reservasUsuario(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		List<String> errores = new ArrayList<>();
		Client usuario = clientService.findById(id);
		if(usuario==null){
			errores.add("No se encuentra al usuario");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(usuario.getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal()) || SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			return reservaService.findByUserId(id);
		}
		errores.add("No puedes acceder a las reservas de otro usuario sin ser administrador");
		response.put("errores",errores);
		return ResponseEntity.badRequest().body(response);
	}
	    
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/plaza/{id}")
	public Object reservasPlaza(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		List<String> errores = new ArrayList<>();
		Plaza plaza = plazaService.findById(id);
		if(plaza==null){
			errores.add("No se encuentra la plaza");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(plaza.getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())  || SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			return reservaService.findByPlazaId(id);
		}
		errores.add("No puedes acceder a las reservas de una plaza que no es tuya sin ser administrador");
		response.put("errores",errores);
		return ResponseEntity.badRequest().body(response);
	}
		
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/all")
	public List<Reserva> findAll(){
		return reservaService.findAll();
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{id}/fechasNoDisponibles")
	public List<List<LocalDateTime>> horariosNoDisponibles(@PathVariable Long id) throws URISyntaxException {

		Plaza plazaAReservar= plazaService.findById(id);
		return reservaService.horariosNoDisponibles(id,plazaAReservar.getTramos());
		
		
	}
		
	


	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{id}")
	public Object detallesReserva(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		List<String> errores = new ArrayList<>();
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		Reserva r = reservaService.findById(id);
		boolean esPropietario= false;
		String nombrePropietario= "No disponible";
		String emailPropietario = "No disponible";
		if(r!=null){
			Long propietarioId= r.getPropietarioId();
			Client propietario = clientService.findById(propietarioId);
			if(propietario!=null){
				esPropietario  = propietario.getEmail().equals(user);
				nombrePropietario= propietario.getName();
				emailPropietario= propietario.getEmail();
			}
			
		}
		if(r==null){
			errores.add("No se encuentra la reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || r.getUser().getEmail().equals(user) || esPropietario){
			
			response.put("reserva", r);
			response.put("nombrePropietario",nombrePropietario);
			response.put("emailPropietario",emailPropietario);

			return ResponseEntity.ok().body(response);
		}else{
			errores.add("No estás involucrado en esta reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}
	}	

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{id}/aceptar")
	public Object aceptarReserva(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		List<String> errores = new ArrayList<>();
		Reserva reserva = reservaService.findById(id);
		boolean esPropietario= false;
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(reserva!=null){
			Long propietarioId= reserva.getPropietarioId();
			Client propietario = clientService.findById(propietarioId);
			if(propietario!=null){
				esPropietario  = propietario.getEmail().equals(user);
			}
			
		}

		if(reserva==null){
			errores.add("No se encuentra la reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || esPropietario){
			try {
				String subject = "Reserva aceptada";
				String text = "¡Enhorabuena! Su reserva ha sido aceptada.\nHaga clic en el siguiente enlace para ver los detalles: "+URL_CORREO+"/reservas/"+reserva.getId()+"\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(reserva.getUser().getEmail(), subject, text);
				}catch(MailException m) {
					return reservaService.aceptarReserva(id);
				}
			return reservaService.aceptarReserva(id);
		}else{
			errores.add("Esta reserva no es sobre una plaza de tu propiedad");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{id}/rechazar")
	public Object rechazarReserva(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		List<String> errores = new ArrayList<>();
		Reserva reserva = reservaService.findById(id);
		boolean esPropietario= false;
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if(reserva!=null){
			Long propietarioId= reserva.getPropietarioId();
			Client propietario = clientService.findById(propietarioId);
			if(propietario!=null){
				esPropietario = propietario.getEmail().equals(user);
			}
			
		}

		if(reserva==null){
			errores.add("No se encuentra la reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || esPropietario){
			try {
				String subject = "Reserva rechazada";
				String text = "¡Lo sentimos! Su solicitud reserva ha sido rechazada por el propietario.\nHaga clic en el siguiente enlace para ver los detalles: "+URL_CORREO+"/reservas/"+reserva.getId()+"\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(reserva.getUser().getEmail(), subject, text);
				}catch(MailException m) {
					return reservaService.rechazarReserva(id);
				}
			return reservaService.rechazarReserva(id);
		}else{
			errores.add("Esta reserva no es sobre una plaza de tu propiedad");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}
	}
	

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{id}/confirmar")
    public Object confirmarServicio(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		List<String> errores = new ArrayList<>();
		Reserva r = reservaService.findById(id);
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		boolean esPropietario= false;
		String emailPropietario= "No Disponible";

		if(r!=null){
			Long propietarioId= r.getPropietarioId();
			Client propietario = clientService.findById(propietarioId);
			if(propietario!=null){
				esPropietario  = propietario.getEmail().equals(user);
				emailPropietario= propietario.getEmail();
			}
		}
		

		if(r==null){
			errores.add("No se encuentra la reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(user.equals(r.getUser().getEmail()) || esPropietario){
			if(r.getFechaFin().isAfter(LocalDateTime.now())){
				errores.add("No puede confirmar esta reserva ya que todavía no ha finalizado");
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}else if(r.getEstado().equals(Estado.denegada) || r.getEstado().equals(Estado.confirmadaAmbos)){
				errores.add("Esta reserva está en un estado final, confirmada por ambos o denegada");
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}else{
				return reservaService.confirmarServicio(r, user,emailPropietario);
			}
		}else{
			errores.add("No estás involucrado en esta reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{id}/cancelar")
    public Object cancelarReserva(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		List<String> errores = new ArrayList<>();
		Reserva reserva = reservaService.findById(id);
		boolean esPropietario= false;
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String emailPropietario= "No disponible";
		if(reserva!=null){
			Long propietarioId= reserva.getPropietarioId();
			Client propietario = clientService.findById(propietarioId);
			if(propietario!=null){
				esPropietario  = propietario.getEmail().equals(user);
				emailPropietario= propietario.getEmail();
			}
			
		}

		if(reserva==null){
			errores.add("No se encuentra la reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}
		response.put("reserva", reserva);
		if(reserva.getEstado().equals(Estado.cancelada) || reserva.getEstado().equals(Estado.rechazada) || reserva.getEstado().equals(Estado.denegada)) {
			errores.add("No se puede cancelar una reserva que ya no está en proceso");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}
		
		Long periodo = Duration.between(LocalDateTime.now(), reserva.getFechaInicio()).toMinutes();
		
		if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || esPropietario){
			try {
				String subject = "Reserva cancelada";
				String text = "La reserva se ha cancelado correctamente.\nSe le reembolsará al cliente el importe íntegro pagado por el alquiler de la plaza.\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(emailPropietario, subject, text);
			}catch (MailException e) {
				return reservaService.devolverTodo(reserva);
			}
			try {
				String subject = "Reserva cancelada por propietario";
				String text = "Le informamos que el propietario de la plaza ha cancelado su reserva de la plaza "+reserva.getDireccion()+".\nSe le reembolsará el importe íntegro.\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(reserva.getUser().getEmail(), subject, text);
			}catch (MailException e) {
				return reservaService.devolverTodo(reserva);
			}
			
			return reservaService.devolverTodo(reserva);
		}else if(!reserva.getUser().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
			errores.add("No estás involucrado en esta reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(!LocalDateTime.now().isBefore(reserva.getFechaInicio())){
			errores.add("Esta reserva no se puede cancelar, la reserva ya ha empezado");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(periodo<1440) {
			try {
				String subject = "Reserva cancelada";
				String text = "La reserva se ha cancelado con menos de 24 horas de antelación.\nSe le reembolsará el importe pagado por la plaza sin la fianza.\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(reserva.getUser().getEmail(), subject, text);
			}catch (MailException e) {
				return reservaService.devolverSinFianza(reserva);
			}
			try {
				String subject = "Reserva de una de sus plazas cancelada";
				String text = "Le informamos que "+reserva.getUser().getName()+" "+reserva.getUser().getSurname()+" ha cancelado su reserva de la plaza "+reserva.getDireccion()+".\nComo se ha cancelado con menos de 24 horas de antelación, se le reembolsará el importe de la plaza, la fianza se le devolverá a usted segun la política de cancelaciones de la empresa.\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(reserva.getPlaza().getAdministrador().getEmail(), subject, text);
			}catch (MailException e) {
				return reservaService.devolverSinFianza(reserva);
			}
				return reservaService.devolverSinFianza(reserva);
		}else {
			try {
				String subject = "Reserva cancelada";
				String text = "La reserva se ha cancelado correctamente.\nSe le reembolsará el importe integro pagado por el alquiler de la plaza.\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(reserva.getUser().getEmail(), subject, text);
			}catch (MailException e) {
				return reservaService.devolverTodo(reserva);
			}
			try {
				String subject = "Reserva de una de sus plazas cancelada";
				String text = "Le informamos que "+reserva.getUser().getName()+" "+reserva.getUser().getSurname()+" ha cancelado su reserva de la plaza "+ reserva.getDireccion() +".\nComo se ha cancelado con más de 24 horas de antelación, se le reembolsará el importe integro pagado por el alquiler de la plaza siguiendo la política de devoluciones de la empresa.\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(emailPropietario, subject, text);
			}catch (MailException e) {
				return reservaService.devolverTodo(reserva);
			}
			return reservaService.devolverTodo(reserva);
		}
	}
    
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	@GetMapping("/{id}/denegar")
    public Object denegarServicio(@PathVariable Long id){
		Map<String,Object> response = new HashMap<>();
		List<String> errores = new ArrayList<>();
		Reserva r = reservaService.findById(id);
		Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		boolean esPropietario= false;
		
		String emailPropietario= "No disponible";
		if(r!=null){
			Long propietarioId= r.getPropietarioId();
			Client propietario = clientService.findById(propietarioId);
			if(propietario!=null){
				esPropietario  = propietario.getEmail().equals(user);
				emailPropietario= propietario.getEmail();
				
			}
			
		}
		if(r==null){
			errores.add("No se encuentra la reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}else if(user.equals(r.getUser().getEmail()) || esPropietario){
			if(r.getFechaFin().isBefore(LocalDateTime.now())){
				return reservaService.denegarServicio(r);
			}else{
				errores.add("No puede denegar esta reserva ya que todavía no ha finalizado");
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}

		}else{
			errores.add("No estás involucrado en esta reserva");
			response.put("errores",errores);
			return ResponseEntity.badRequest().body(response);
		}
    }
}
