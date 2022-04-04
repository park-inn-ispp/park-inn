package com.parkinn.service;

import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.ReservaRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.parkinn.model.Estado;
import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.PayPalClasses;

import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReservaService {
    
    @Autowired
    RestTemplate restTemplate;
	
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
	

	public PayPalClasses getPayPal(String query) throws URISyntaxException{
        
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Authorization", "Bearer A21AAJNZKOugw3p1gIoKwWs-ga-HnIe-Og2NqZuhl-8j4IAFL6pZ2BDuMpgVZOOxHdi8B2cP7cN5GwqLMnIZS2cLGrbE1cacA");
		System.out.println(headers);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		
		ResponseEntity<PayPalClasses> response = restTemplate.exchange("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + query,HttpMethod.GET,entity, PayPalClasses.class);

        
        PayPalClasses paypal = response.getBody();    
        
        
    return  paypal;
    }
	
	
	
	
}
