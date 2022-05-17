package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkinn.model.Descuento;
import com.parkinn.service.DescuentoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/descuento")
public class DescuentoController {


    @Autowired
    private final DescuentoService descuentoService;

    public DescuentoController(DescuentoService descuentoService) {
        this.descuentoService = descuentoService;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public List<Descuento> findAll(){
        return descuentoService.findAll();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public Object getDescuentoById(@PathVariable Long id){
        Descuento  descuento = descuentoService.findById(id);
        List<String> errores = new ArrayList<String>();
        Map<String,Object> response = new HashMap<>();
        
        
        if(descuento == null){
			errores.add("Estás intentando acceder a un descuento que no existe");
			response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else{
            return descuento;

        }
    }

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/name/{name}")
    public Object getDescuentoByName(@PathVariable String name){
        Descuento  descuento = descuentoService.findByName(name);
        List<String> errores = new ArrayList<String>();
        Map<String,Object> response = new HashMap<>();
        
        
        if(descuento == null){
			errores.add("Estás intentando acceder a un descuento que no existe");
			response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else{
            return descuento;

        }
    }

    @SuppressWarnings("rawtypes")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteDescuento(@PathVariable Long id) {
        List<String> errores = new ArrayList<String>();
    	if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))){
    		 descuentoService.deleteById(id);
    	     return ResponseEntity.ok().build();

    	}else{
            Map<String,Object> response = new HashMap<>();
  			errores.add("No eres administrador para borrar descuentos");            
            response.put("error", errores);
  			return ResponseEntity.badRequest().body(response);
        }
        
    }

        @SuppressWarnings("rawtypes")
        @PreAuthorize("hasRole('ROLE_ADMIN')")
		@PostMapping
		public ResponseEntity guardarDescuento(@RequestBody Descuento descuento) throws URISyntaxException {
			Map<String,Object> response = new HashMap<>();
        	response.put("descuento", descuento);
			List<String> errores = new ArrayList<>();

			if(descuento.getName() == null){
				errores.add("El descuento debe tener un nombre asociado");
			}
			if(descuento.getDescuento() <= 0 || descuento.getDescuento() > 100){
				errores.add("El descuento debe ser > 0 y <= 100");
			}
			

			if(errores.size() != 0){
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}
			else{
				Descuento savedDescuento = descuentoService.guardarDescuento(descuento);
				return ResponseEntity.created(new URI("/descuento/" + savedDescuento.getId())).body(savedDescuento);
			}
			
		}
        
    
}
