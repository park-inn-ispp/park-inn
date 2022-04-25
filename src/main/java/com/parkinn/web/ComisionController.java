package com.parkinn.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkinn.model.Comision;
import com.parkinn.repository.ComisionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comision")
public class ComisionController {


    @Autowired
    private final ComisionRepository comisionRepository;

    public ComisionController(ComisionRepository comisionRepository) {
        this.comisionRepository = comisionRepository;
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	    @PutMapping("/{id}/editar")
	    public ResponseEntity editar(@PathVariable Long id, @RequestBody Comision comision){
            List<String> errores = new ArrayList<>();
            Map<String,Object> response = new HashMap<>();
			if(comision.getPorcentaje() < 0){
				errores.add("El Porcentaje no puede ser negativo");
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}else if(comision.getPorcentaje() > 1){
				errores.add("El Porcentaje no puede ser mayor que 1");
				response.put("errores",errores);
				return ResponseEntity.badRequest().body(response);
			}else{

                
                Comision currentComision = comisionRepository.findById(id).orElseThrow(RuntimeException::new);
                currentComision.setPorcentaje(comision.getPorcentaje());
                currentComision = comisionRepository.save(comision);
            
                return ResponseEntity.ok(currentComision);
                
            }
	    }
    
}
