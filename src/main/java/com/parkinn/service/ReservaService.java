package com.parkinn.service;

import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.ReservaRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
	public List<Reserva> findAll(){
        return repository.findAll();
    }

    public Reserva guardarReserva(Reserva r){
        r.setEstado(Estado.pendiente);
        r.setFechaSolicitud(LocalDateTime.now());
        Double precio = Duration.between(r.getFechaInicio(), r.getFechaFin()).toMinutes() * r.getPlaza().getPrecioHora()/60;
        r.setPrecioTotal(Math.round(precio*100.0)/100.0);
        Reserva reserva = repository.save(r);
        return reserva;
    }

	public Reserva aceptarReserva(Long id){
		Reserva r = findById(id);
        r.setEstado(Estado.aceptada);
        Reserva reserva = repository.save(r);
        return reserva;
    }

	public Reserva rechazarReserva(Long id){
		Reserva r = findById(id);
        r.setEstado(Estado.rechazada);
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
    public Reserva findById(Long id) {
    	Optional<Reserva> reserva = repository.findById(id);
    	return reserva.orElse(null);
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

	public Boolean reservaTieneColision(Reserva res){
		List<List<LocalDateTime>> horarios = horariosNoDisponibles(res.getPlaza().getId());
		for (List<LocalDateTime> h: horarios){
			if(h.get(1).isAfter(res.getFechaInicio()) && h.get(0).isBefore(res.getFechaFin())){
				return true;
			}
		}
		return false;
	}

	public Reserva confirmarServicio(Reserva r, Object user){
		if(user.equals(r.getUser().getEmail()) && !r.getEstado().equals(Estado.confirmadaPropietario)){
			r.setEstado(Estado.confirmadaUsuario);
		}else if(user.equals(r.getPlaza().getAdministrador().getEmail()) && !r.getEstado().equals(Estado.confirmadaUsuario)){
			r.setEstado(Estado.confirmadaPropietario);
		}else{
			r.setEstado(Estado.confirmadaAmbos);
			//Meter logica para devolver la fianza
		}
        Reserva reserva = repository.save(r);
        return reserva;
    }

	public Reserva denegarServicio(Reserva r){
		r.setEstado(Estado.denegada);
        Reserva reserva = repository.save(r);
        return reserva;
    }
}
