package com.parkinn.web;

import java.time.LocalDateTime;
import java.util.List;

import com.parkinn.model.Plaza;
import com.parkinn.service.PlazaService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/plazas")
public class PlazaController {

    @Autowired
    private PlazaService plazaService;

    @GetMapping()
    public List<Plaza> filtrarPlazas(@RequestParam(name = "maxPrecioHora", required=false) Double maxPrecioHora, @RequestParam(name = "fechaInicio", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
        @RequestParam(name = "fechaFin", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin, @RequestParam(name = "zona", required=false) String zona) {
        return plazaService.filtrarPlazas(maxPrecioHora, fechaInicio, fechaFin, zona);
    }

}
