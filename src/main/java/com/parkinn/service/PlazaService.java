package com.parkinn.service;

import com.parkinn.repository.PlazaRepository;
import com.parkinn.model.Localizacion;
import com.parkinn.model.Plaza;
import com.parkinn.model.Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PlazaService {
    
    @Autowired
    private PlazaRepository repository;
   
    @Autowired
    RestTemplate restTemplate;

    
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

    // Si ya existe una plaza con la misma latitud y longitud, modificamos levemente las coordenadas para que 
    // no se pisen en el mapa
    public List<String> latitudLongitudDiferentes(String latitud, String longitud){
        List<String> nuevasCoordenadas= new ArrayList<String>();
        nuevasCoordenadas.add(latitud);
        nuevasCoordenadas.add(longitud);
        boolean coordenadasRepetidas = true;
        Double latitudDouble = Double.valueOf(latitud);
        Double longitudDouble = Double.valueOf(longitud);
        Double espiralLatitud= 0.0;
        Double espiralLongitud= 0.0;
        Double proporcionSumar= 0.0001;

        while(coordenadasRepetidas){

            coordenadasRepetidas= !repository.existsCoordenates(latitud, longitud).isEmpty();
            if (coordenadasRepetidas){
                Long ratioLatitud =Math.round(Math.cos(espiralLatitud));
                Long ratioLongitud =Math.round(Math.sin(espiralLongitud));
                
                latitudDouble += ratioLatitud*proporcionSumar;
                longitudDouble += ratioLongitud*proporcionSumar;
                latitud = String.valueOf(latitudDouble);
                longitud= String.valueOf(longitudDouble);
                proporcionSumar+=0.0001;
                espiralLatitud+=Math.PI/2;
                espiralLongitud+=Math.PI/2;
            }else{
                nuevasCoordenadas.set(0, latitud);
                nuevasCoordenadas.set(1, longitud);
                coordenadasRepetidas= false;
                break;

            }
            
        }
        
        return nuevasCoordenadas;
    }
 
    public Plaza findById(Long id){
        Plaza plaza = repository.findById(id).orElse(null);
        return plaza;
    }
    
    
    public List<Plaza> findUserById(Long id){
    	List<Plaza> plazas = repository.findByUserId(id);
        return plazas;
    }
    
    public void deleteById(Long id){
        repository.deleteById(id);
    }
    
    
    public Localizacion getLocalizacion(String query){
        String[] res = query.split(",");
        String calle = res[0];
        String numero = res[1];
        String ciudad = res[2];
        String provincia = res[3];
        @SuppressWarnings("unused")
		String cogigoPostal = res[4];
        
        String direccion = calle + "," + numero + "," + ciudad + "," + provincia;
        
        ResponseEntity<Localizacion[]> response = restTemplate.getForEntity("https://geocode.maps.co/search?q=" + direccion, Localizacion[].class);
        Localizacion[] localizaciones = response.getBody();
        List<Localizacion> l = Arrays.asList(localizaciones);
        Localizacion localizacion = l.get(0);    
        
        
    return  localizacion;
    }
    
    
    
    
}
