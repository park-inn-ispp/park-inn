package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
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
	    public Incidencia findById(@PathVariable Long id){
	    	return incidenciaService.findIncidenciaById(id);
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
			if(incidencia.getUser().getEmail() == null || incidencia.getUser() == null){
				response.put("error","La incidencia no tiene ningún usuario asociado");
				return ResponseEntity.badRequest().body(response);
			}else if(incidencia.getReserva() == null || incidencia.getReserva().getId() == null ){
				response.put("error","La incidencia no tiene ninguna reserva asociada");
				return ResponseEntity.badRequest().body(response);
			}else{
				Incidencia savedIncidencia = incidenciaService.guardarIncidencia(incidencia);
				return ResponseEntity.created(new URI("/incidencias/" + savedIncidencia.getId())).body(savedIncidencia);
			}
			
		}

		@PreAuthorize("hasRole('ROLE_ADMIN')")
		@PutMapping("/{id}")
		public ResponseEntity cerrarIncidencia(@PathVariable Long id) {
			
			Incidencia incidenciaActual = incidenciaService.findIncidenciaById(id);
			incidenciaActual.setEstado(EstadoIncidencia.resuelta);
	
			incidenciaActual = incidenciaService.guardarIncidencia(incidenciaActual);
	
			return ResponseEntity.ok(incidenciaActual);
		}

	
	}
