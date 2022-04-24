package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parkinn.model.Horario;
import com.parkinn.model.Localizacion;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.service.HorarioService;

@RestController
@RequestMapping("/horarios")
public class HorariosController {

	@Autowired
	private HorarioService horarioService;
	@Autowired
	private HorarioRepository horarioRepository;
	
	

	
	@SuppressWarnings({ "rawtypes", "unused" })
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity updateHorario(@PathVariable Long id, @RequestBody Horario horario) {
    	Map<String,Object> response = new HashMap<>();
    	List<String> errores = new ArrayList<String>();
        Horario currentHorario = horarioService.findById(id);

    	if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||currentHorario.getPlaza().getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
		
            currentHorario.setActivo(horario.getActivo());
            currentHorario = horarioService.guardarHorario(currentHorario);
            return ResponseEntity.ok(currentHorario);
               
          
    	}else{
            
  			errores.add("No puedes editar un horario que no es de tu propiedad");            
            response.put("errores", errores);
  			return ResponseEntity.badRequest().body(response);
        }
    }
	
	 
    @SuppressWarnings("rawtypes")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteHorario(@PathVariable Long id) {
    	Horario currentHorario = horarioService.findById(id);
        List<String> errores = new ArrayList<String>();
    	if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||currentHorario.getPlaza().getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
    		 horarioService.deleteById(id);
    	     return ResponseEntity.ok().build();

    	}else{
            Map<String,Object> response = new HashMap<>();
  			errores.add("Este horario no es de tu propiedad");            
            response.put("error", errores);
  			return ResponseEntity.badRequest().body(response);
        }
        
    }
}
