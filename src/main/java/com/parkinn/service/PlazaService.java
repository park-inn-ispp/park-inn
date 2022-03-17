package com.parkinn.service;

import com.parkinn.repository.PlazaRepository;
import com.parkinn.model.Plaza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlazaService {
    
    @Autowired
    private PlazaRepository repository;

    public List<Plaza> filtrarPlazas(Double max, LocalDateTime inicio, LocalDateTime fin, String zona){
        List<Plaza> plazas = repository.filter(max, inicio, fin, zona);
        return plazas;
    }
}
