package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.parkinn.model.Plaza;
import com.parkinn.service.PlazaService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/plaza")
public class PlazaController {

    @Autowired
    private PlazaService plazaService;

    @GetMapping()
    public List<Plaza> getClient(@RequestParam(name = "max") Double max) {
        return plazaService.filtrarPorPrecio(max);
    }

}
