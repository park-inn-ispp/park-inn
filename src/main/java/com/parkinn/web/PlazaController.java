package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;

import com.parkinn.model.Plaza;
import com.parkinn.service.PlazaService;

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

    @GetMapping("/all")
    public List<Plaza> getPlazas() {
        return plazaService.findAll();
    }

    @PostMapping
    public ResponseEntity createPlaza(@RequestBody Plaza plaza) throws URISyntaxException {
        Plaza savedPlaza = plazaService.guardarPlaza(plaza);
        return ResponseEntity.created(new URI("/plazas/" + savedPlaza.getId())).body(savedPlaza);
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePlaza(@PathVariable Long id, @RequestBody Plaza plaza) {
        Plaza currentPlaza = plazaService.findById(id);
        currentPlaza.setDireccion(plaza.getDireccion());
        currentPlaza.setDescripcion(plaza.getDescripcion());
        currentPlaza.setAncho(plaza.getAncho());
        currentPlaza.setLargo(plaza.getLargo());
        currentPlaza.setEstaDisponible(plaza.getEstaDisponible());
        currentPlaza.setEsAireLibre(plaza.getEsAireLibre());
        currentPlaza.setFianza(plaza.getFianza());
        currentPlaza.setPrecioHora(plaza.getPrecioHora());

        currentPlaza = plazaService.guardarPlaza(plaza);

        return ResponseEntity.ok(currentPlaza);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePlaza(@PathVariable Long id) {
        plazaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
