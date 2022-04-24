package com.parkinn.service;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkinn.model.Horario;
import com.parkinn.model.Reserva;
import com.parkinn.repository.HorarioRepository;

@Service
public class HorarioService {

	@Autowired
	private HorarioRepository repository;
	@Autowired
	private ReservaService reservaService;
	
	public Horario guardarHorario(Horario horario){
        Horario savedHorario = repository.save(horario);
        return savedHorario;
    }
	
	 public Horario findById(Long id){
	        Horario horario = repository.findById(id).orElse(null);
	        return horario;
	 }
	 
	 public void deleteById(Long id){
	        repository.deleteById(id);
	 }
	 
}
