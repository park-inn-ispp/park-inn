package com.parkinn.service;

import com.parkinn.repository.PlazaRepository;
import com.parkinn.model.Localizacion;
import com.parkinn.model.Plaza;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public List<Plaza> findAll(){
        return repository.findAll();
    }

    public Plaza guardarPlaza(Plaza plaza){
        Plaza savedPlaza = repository.save(plaza);
        return savedPlaza;
    }

    public Plaza findById(Long id){
        Plaza plaza = repository.findById(id).orElseThrow(RuntimeException::new);
        return plaza;
    }
    
    public List<Plaza> findUserById(Long id){
    	List<Plaza> plazas = repository.findByUserId(id);
        return plazas;
    }
    
    public void deleteById(Long id){
        repository.deleteById(id);
    }
    
    @Autowired
    RestTemplate restTemplate;
    public Localizacion getLocalizacion(String direccion){
        ResponseEntity<Localizacion> response = restTemplate.getForEntity("https://geocode.maps.co/search?q=" + direccion, Localizacion.class);
        Localizacion localizacion = response.getBody();
     
    return  localizacion;
    }
    
    
    
    
}
