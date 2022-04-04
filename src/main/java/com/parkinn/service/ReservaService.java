package com.parkinn.service;

import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.ReservaRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
        r.setPrecioTotal(r.getPlaza().getFianza() + Math.round(precio*100.0)/100.0);
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
  
	public Object confirmarServicio(Reserva r, Object user){
		if(user.equals(r.getUser().getEmail()) && !r.getEstado().equals(Estado.confirmadaPropietario)){
			r.setEstado(Estado.confirmadaUsuario);
		}else if(user.equals(r.getPlaza().getAdministrador().getEmail()) && !r.getEstado().equals(Estado.confirmadaUsuario)){
			r.setEstado(Estado.confirmadaPropietario);
		}else{
			r.setEstado(Estado.confirmadaAmbos);

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set("Authorization", "Bearer A21AAJNZKOugw3p1gIoKwWs-ga-HnIe-Og2NqZuhl-8j4IAFL6pZ2BDuMpgVZOOxHdi8B2cP7cN5GwqLMnIZS2cLGrbE1cacA");

			//--------------- Devolver fianza ---------------
			Map<String,Object> body = new HashMap<>();
			Map<String,Object> senderHeader = new HashMap<>();
			//senderHeader.put("sender_batch_id", "");
			senderHeader.put("recipient_type", "EMAIL");
			senderHeader.put("email_subject", "Devolución de la fianza");
			senderHeader.put("email_message", "Se te devuele la fianza de la plaza que reservaste en park-inn");

			Map<String,Object> item = new HashMap<>();
			Map<String,Object> amount = new HashMap<>();
			amount.put("value", r.getPlaza().getFianza());
			amount.put("currency","EUR");

			item.put("amount", amount);
			//item.put("sender_item_id", "");
			item.put("recipient_wallet", "PAYPAL");
			item.put("receiver", "sb-ah4x115239563@personal.example.com");//r.getUser().getEmail()

			List<Map<String,Object>> items = new ArrayList<>();
			items.add(item);

			body.put("sender_batch_header", senderHeader);
			body.put("items", items);
			HttpEntity<Object> entity = new HttpEntity<>(body, headers);
			ResponseEntity<Object> response = restTemplate.exchange("https://api-m.sandbox.paypal.com/v1/payments/payouts", HttpMethod.POST, entity, Object.class);

			//--------------- Pagar al propietario ---------------
			Map<String,Object> body_p = new HashMap<>();
			Map<String,Object> senderHeader_p = new HashMap<>();
			//senderHeader_p.put("sender_batch_id", "");
			senderHeader_p.put("recipient_type", "EMAIL");
			senderHeader_p.put("email_subject", "Ingresos de la plaza " + r.getPlaza());
			senderHeader_p.put("email_message", "Se te ha ingredado el dinero ganado sobre tu plaza " + r.getPlaza());

			Map<String,Object> item_p = new HashMap<>();
			Map<String,Object> amount_p = new HashMap<>();
			amount_p.put("value", r.getPrecioTotal() - r.getPlaza().getFianza() - 0.1*r.getPrecioTotal());//Poner la comisión como atributo
			amount_p.put("currency","EUR");

			item_p.put("amount", amount_p);
			//item_p.put("sender_item_id", "");
			item_p.put("recipient_wallet", "PAYPAL");
			item_p.put("receiver", "sb-ah4x115239563@personal.example.com");//r.getPlaza().getAdministrador().getEmail()

			List<Map<String,Object>> items_p = new ArrayList<>();
			items_p.add(item_p);

			body_p.put("sender_batch_header", senderHeader_p);
			body_p.put("items", items_p);
			HttpEntity<Object> entity_p = new HttpEntity<>(body_p, headers);
			ResponseEntity<Object> response_p = restTemplate.exchange("https://api-m.sandbox.paypal.com/v1/payments/payouts", HttpMethod.POST, entity_p, Object.class);

			if(!response.getStatusCode().is2xxSuccessful() || !response_p.getStatusCode().is2xxSuccessful()){
				Map<String,Object> res = new HashMap<>();
				res.put("error","Ha ocurrido un error inesperado con los pagos finales");
				return ResponseEntity.badRequest().body(res);
			} 
		}
        Reserva reserva = repository.save(r);
        return reserva;
    }

	public Reserva denegarServicio(Reserva r){
		r.setEstado(Estado.denegada);
        Reserva reserva = repository.save(r);
        return reserva;
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
