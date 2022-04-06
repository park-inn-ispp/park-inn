package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkinn.model.EstadoIncidencia;
import com.parkinn.model.Incidencia;
import com.parkinn.service.IncidenciaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

	@RestController
	@RequestMapping("/incidencias")
	public class IncidenciaController {

	    @Autowired
	    private IncidenciaService incidenciaService;

		@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	    @GetMapping("/{id}")
	    public ResponseEntity<Map<String, Object>> findById(@PathVariable Long id){
			Incidencia incidencia = incidenciaService.findIncidenciaById(id);
			List<String> errores = new ArrayList<>();
	    	Map<String,Object> response = new HashMap<>();
			if(incidencia == null){
				errores.add("La incidencia no existe");
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}else{
				response.put("incidencia", incidencia);
				return ResponseEntity.ok(response);

			}
	    }
		
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
		@GetMapping("/all")
	    public List<Incidencia> findAll(){
	    	return incidenciaService.findAll();
	    }
	  
		@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
		@PostMapping
		public ResponseEntity guardarIncidencia(@RequestBody Incidencia incidencia) throws URISyntaxException {
			Map<String,Object> response = new HashMap<>();
        	response.put("incidencia", incidencia);
			List<String> errores = new ArrayList<>();

			if(incidencia.getUser().getEmail() == null || incidencia.getUser() == null){
				errores.add("La incidencia no tiene ningún usuario asociado");
			}
			if(incidencia.getReserva() == null || incidencia.getReserva().getId() == null ){
				errores.add("La incidencia no tiene ninguna reserva asociada");
			}
			if((incidencia.getReserva() == null && incidencia.getReserva().getId() == null)){
				
				if(!incidenciaService.comprobarCliente(incidencia)){
					errores.add("El usuario no es ni el dueño de la plaza ni el cliente de la reserva");
				}
				if(incidenciaService.comprobarConfirmacion(incidencia)){
					errores.add("La reserva ya ha sido confirmada por ambos");
				}
			}
			

			if(errores.size() != 0){
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}
			else{
				Incidencia savedIncidencia = incidenciaService.guardarIncidencia(incidencia);
				return ResponseEntity.created(new URI("/incidencias/" + savedIncidencia.getId())).body(savedIncidencia);
			}
			
		}

		@PreAuthorize("hasRole('ROLE_ADMIN')")
		@PutMapping("/{id}")
		public ResponseEntity cerrarIncidencia(@PathVariable Long id) {
			
			Incidencia incidenciaActual = incidenciaService.findIncidenciaById(id);
			Incidencia incidencia = incidenciaService.findIncidenciaById(id);
	    	Map<String,Object> response = new HashMap<>();
			List<String> errores = new ArrayList<>();

			if(incidencia == null){
				errores.add("La incidencia no existe");
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}else{
				incidenciaActual.setEstado(EstadoIncidencia.resuelta);
				incidenciaActual = incidenciaService.guardarIncidencia(incidenciaActual);

				return ResponseEntity.ok(incidenciaActual);

			}	
	
		}

	
	}
