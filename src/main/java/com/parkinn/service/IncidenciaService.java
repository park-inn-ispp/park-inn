package com.parkinn.service;

import com.parkinn.repository.ClientRepository;
import com.parkinn.repository.IncidenciaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.parkinn.model.Incidencia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncidenciaService {
    
    @Autowired
    private IncidenciaRepository repository;

	@Autowired
    private ReservaService reservaService;

	@Autowired
    private ClientRepository clientRepository;
    
    
	public List<Incidencia> findAll(){
        return repository.findAll();
    }

	  public Incidencia findIncidenciaById(Long id){
    	Optional<Incidencia> incidencias = repository.findById(id);
        return incidencias.get();
    }


    public Incidencia guardarIncidencia(Incidencia r){
        r.setFecha(LocalDateTime.now());
		r.setUser(clientRepository.findByEmail(r.getUser().getEmail()).get());
		r.setReserva(reservaService.findById(r.getReserva().getId()));
        Incidencia incidencia = repository.save(r);
        return incidencia;
    }

}
