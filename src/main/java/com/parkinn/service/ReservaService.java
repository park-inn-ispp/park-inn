package com.parkinn.service;

import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.ReservaRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.parkinn.model.Estado;
import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservaService {
    
    @Autowired
    private ReservaRepository repository;
    @Autowired
    private HorarioRepository horarioRepository;
    
    @Autowired
    private PlazaService plazaService;
    
    public Reserva guardarReserva(Reserva r){
        r.setEstado(Estado.aceptada);
        r.setFechaSolicitud(LocalDateTime.now());
        Double precio = Duration.between(r.getFechaInicio(), r.getFechaFin()).toHours() * r.getPlaza().getPrecioHora();
        r.setPrecioTotal(precio);
        Reserva reserva = repository.save(r);
        return reserva;
    }
    
    public List<Reserva> findPlazaById(Long id){
    	List<Reserva> reservas = repository.findByPlazaId(id);
        return reservas;
    }

    public List<Reserva> findByUserId(Long id){
        List<Reserva> reservas= repository.findByUserId(id);
        return reservas;
    }
    
    /*
    public List<Horario> horariosDisponibles(Long id){
       	Plaza plaza = plazaService.findById(id);
       	Horario horario = plaza.getHorario();
       	List<Reserva> lr = repository.findByPlazaId(id);
   		List<Horario> horarios = new ArrayList<Horario>();
       	if(!lr.isEmpty()) {
       		for(int i =0; i<lr.size(); i++) {
           		if(horario.getFechaInicio()!=lr.get(i).getFechaInicio()) {
           			Horario nuevoHorario = new Horario(horario.getFechaInicio(),lr.get(i).getFechaInicio());
           			horarios.add(nuevoHorario);
           		}
           		else if(horario.getFechaFin()!=lr.get(i).getFechaFin() && lr.get(i).getFechaFin()!=lr.get(i+1).getFechaInicio()) {
           			Horario nuevoHorario = new Horario(lr.get(i).getFechaFin(),lr.get(i+1).getFechaInicio());
           			horarios.add(nuevoHorario);
           		}
           		else if(horario.getFechaFin()!=lr.get(i).getFechaFin()){
           			Horario nuevoHorario = new Horario(lr.get(i).getFechaFin(),horario.getFechaFin());
           			horarios.add(nuevoHorario);
           		}
           	}
       		return horarios;
       	}
       	else {
       		horarios.add(horario);
       		return horarios;
       	}
       	    
       }*/
    
    public List<List<LocalDateTime>> horariosNoDisponibles(Long id){
    List<Reserva> lr = repository.findByPlazaId(id);
		List<List<LocalDateTime>> horarios = new ArrayList<>();
		if(!lr.isEmpty()) {
			for(int i =0; i<lr.size(); i++) {
       			List<LocalDateTime> HorarioOcupado = new ArrayList<LocalDateTime>();
       			HorarioOcupado.add(lr.get(i).getFechaInicio());
       			HorarioOcupado.add(lr.get(i).getFechaFin());
       			horarios.add(HorarioOcupado);
			}
			return horarios;
		}
		else{
			return horarios;
		}
    }
}
