package com.parkinn.service;

import com.parkinn.repository.PlazaRepository;
import com.parkinn.model.Plaza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;  

import java.util.List;

@Service
public class PlazaService {
    
    @Autowired
    private PlazaRepository repository;

    public List<Plaza> filtrarPorPrecio(Double max){
        List<Plaza> plazas = repository.findByPrecioHoraLessThanEqual(max);
        return plazas;
    }
}
