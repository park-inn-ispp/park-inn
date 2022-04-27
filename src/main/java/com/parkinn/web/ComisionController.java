package com.parkinn.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkinn.model.Comision;
import com.parkinn.service.ComisionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/comision")
public class ComisionController {


    @Autowired
    private final ComisionService comisionService;

    public ComisionController(ComisionService comisionService) {
        this.comisionService = comisionService;
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public Object getComision(@PathVariable Long id){
        Comision  comision = comisionService.findById(id);
        List<String> errores = new ArrayList<String>();
        Map<String,Object> response = new HashMap<>();
        
        
        if(comision == null){
			errores.add("Estás intentando acceder a una comisión inválida que no existe");
			response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else{
            return comision;

        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{id}/editar")
    public ResponseEntity editar(@PathVariable Long id, @RequestBody Comision comision){
        List<String> errores = new ArrayList<>();
        Map<String,Object> response = new HashMap<>();
        if(comision.getPorcentaje() < 0){
            errores.add("El ratio de comisión no puede ser negativo");
            response.put("errores",errores);
            return ResponseEntity.badRequest().body(response);
        }else if(comision.getPorcentaje() > 1){
            errores.add("El ratio de comisión no puede ser mayor que 1");
            response.put("errores",errores);
            return ResponseEntity.badRequest().body(response);
        }else{

            
            Comision currentComision = comisionService.findById(id);
            if(currentComision != null){
                Comision comisionExistente= currentComision;
                comisionExistente.setPorcentaje(comision.getPorcentaje());
                comisionExistente = comisionService.save(comisionExistente);
                return ResponseEntity.ok(comisionExistente);

            }else{
                errores.add("Estás intentando editar una comisión inválida que no existe");
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
            }
            
            
        }
	}
        
    
}
