package com.parkinn.service;

import com.parkinn.repository.ReservaRepository;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkinn.model.Estado;
import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.PayPalAccesToken;
import com.parkinn.model.paypal.PayPalClasses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReservaService {

    @Autowired
    RestTemplate restTemplate;
	
    @Autowired
    private ReservaRepository repository;
   
	public List<Reserva> findAll(){
        return repository.findAll();
    }

    public Reserva guardarReserva(Reserva r){
        r.setEstado(Estado.pendiente);
        r.setFechaSolicitud(LocalDateTime.now());
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
    
	public Reserva cancelarReserva(Long id){
		Reserva r = findById(id);
        r.setEstado(Estado.cancelada);
        Reserva reserva = repository.save(r);
        return reserva;
    }
	
	public Object devolverSinFianza(Reserva r){
		
		HttpHeaders headers1 = new HttpHeaders();
		headers1.set("Content-Type", "application/x-www-form-urlencoded");
		headers1.set("Authorization", "Basic QWR1NGpVdFRrYUp4TkZxdWZoenRvTnAtQ1F1WldKTGt2VjVGRG5fYUlwa2hiV2xTdm5Qd1NxMlRORHNUNHZGWnQtX3VFbUZfcnRIODlNdms6RUxIYWZIQWMtMFpQclJXZVo1MFBqeFQ0TmtWNDg5UDNnZno3Q3RvWU9yLWVvQVQxekhzcVZuTlZrYm5WRkE4S21RdVFpQVNkSlU2ZzgxN3M=");
		HttpEntity<String> entity1 = new HttpEntity<String>("parameters", headers1);
		ResponseEntity<PayPalAccesToken> response1 = restTemplate.exchange("https://api-m.sandbox.paypal.com/v1/oauth2/token?grant_type=client_credentials",HttpMethod.POST,entity1, PayPalAccesToken.class);
		String token = response1.getBody().getAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Authorization", "Bearer " + token);

		//--------------- Devolver todo ---------------
		Map<String,Object> body = new HashMap<>();
		Map<String,Object> senderHeader = new HashMap<>();
		//senderHeader.put("sender_batch_id", "");
		senderHeader.put("recipient_type", "EMAIL");
		senderHeader.put("email_subject", "Devolución del importe sin la fianza de la reserva");
		senderHeader.put("email_message", "Se te devuele el importe sin la fianza de la reserva de park-inn");

		Map<String,Object> item = new HashMap<>();
		Map<String,Object> amount = new HashMap<>();
		amount.put("value", Math.round((r.getPrecioTotal() - r.getPlaza().getFianza())*100.0)/100.0);
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
	
		if(!response.getStatusCode().is2xxSuccessful()){
			Map<String,Object> res = new HashMap<>();
			res.put("error","Ha ocurrido un error inesperado con la devolución");
			return ResponseEntity.badRequest().body(res);
		} else {
			r.setEstado(Estado.cancelada);
	        repository.save(r);
		Map<String,Object> res = new HashMap<>();
		res.put("info","Reserva cancelada con éxito. Se ha devuelto el importe total sin la fianza al cliente");
		return ResponseEntity.accepted().body(res);
		}
		
}
	
	public Object devolverTodo(Reserva r){
			
			HttpHeaders headers1 = new HttpHeaders();
			headers1.set("Content-Type", "application/x-www-form-urlencoded");
			headers1.set("Authorization", "Basic QWR1NGpVdFRrYUp4TkZxdWZoenRvTnAtQ1F1WldKTGt2VjVGRG5fYUlwa2hiV2xTdm5Qd1NxMlRORHNUNHZGWnQtX3VFbUZfcnRIODlNdms6RUxIYWZIQWMtMFpQclJXZVo1MFBqeFQ0TmtWNDg5UDNnZno3Q3RvWU9yLWVvQVQxekhzcVZuTlZrYm5WRkE4S21RdVFpQVNkSlU2ZzgxN3M=");
			HttpEntity<String> entity1 = new HttpEntity<String>("parameters", headers1);
			ResponseEntity<PayPalAccesToken> response1 = restTemplate.exchange("https://api-m.sandbox.paypal.com/v1/oauth2/token?grant_type=client_credentials",HttpMethod.POST,entity1, PayPalAccesToken.class);
			String token = response1.getBody().getAccessToken();

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set("Authorization", "Bearer " + token);

			//--------------- Devolver todo ---------------
			Map<String,Object> body = new HashMap<>();
			Map<String,Object> senderHeader = new HashMap<>();
			//senderHeader.put("sender_batch_id", "");
			senderHeader.put("recipient_type", "EMAIL");
			senderHeader.put("email_subject", "Devolución total del importe de la reserva");
			senderHeader.put("email_message", "Se te devuele el importe de la reserva de park-inn");

			Map<String,Object> item = new HashMap<>();
			Map<String,Object> amount = new HashMap<>();
			amount.put("value", Math.round((r.getPrecioTotal())*100.0)/100.0);
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
		
			if(!response.getStatusCode().is2xxSuccessful()){
				Map<String,Object> res = new HashMap<>();
				res.put("error","Ha ocurrido un error inesperado con la devolución");
				return ResponseEntity.badRequest().body(res);
			} else {
				r.setEstado(Estado.cancelada);
		        repository.save(r);
			Map<String,Object> res = new HashMap<>();
			res.put("info","Reserva cancelada con éxito. Se ha devuelto el importe total al cliente");
			return ResponseEntity.accepted().body(res);
			}
			
	}
		
	
    public List<Reserva> findPlazaById(Long id){
    	List<Reserva> reservas = repository.findByPlazaId(id);
        return reservas;
    }

    public List<Reserva> findByUserId(Long id){
        List<Reserva> reservas= repository.findByUserId(id);
        return reservas;
        
    }
    public Reserva findById(Long id){
        Reserva reserva = repository.findById(id).orElse(null);
        return reserva;
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
	public List<String> erroresNuevaReservaAntesDelPago(Reserva reserva){
		List<String> errores = new ArrayList<String>();
		if(reserva.getFechaInicio().isAfter(reserva.getFechaFin())){
            errores.add("La fecha de inicio debe ser anterior a la fecha de fin");
            
        }else if(reserva.getFechaInicio().isBefore(LocalDateTime.now())){
            errores.add("No se pueden realizar reservas en el pasado");
         
        }else if(reservaTieneColision(reserva)){
            errores.add("Este horario está ocupado por otra reserva");
           
        }
		return errores;
	}

  
	public Object confirmarServicio(Reserva r, Object user){
		if(user.equals(r.getUser().getEmail()) && !r.getEstado().equals(Estado.confirmadaPropietario)){
			r.setEstado(Estado.confirmadaUsuario);
		}else if(user.equals(r.getPlaza().getAdministrador().getEmail()) && !r.getEstado().equals(Estado.confirmadaUsuario)){
			r.setEstado(Estado.confirmadaPropietario);
		}else{
			r.setEstado(Estado.confirmadaAmbos);
			
			HttpHeaders headers1 = new HttpHeaders();
			headers1.set("Content-Type", "application/x-www-form-urlencoded");
			headers1.set("Authorization", "Basic QWR1NGpVdFRrYUp4TkZxdWZoenRvTnAtQ1F1WldKTGt2VjVGRG5fYUlwa2hiV2xTdm5Qd1NxMlRORHNUNHZGWnQtX3VFbUZfcnRIODlNdms6RUxIYWZIQWMtMFpQclJXZVo1MFBqeFQ0TmtWNDg5UDNnZno3Q3RvWU9yLWVvQVQxekhzcVZuTlZrYm5WRkE4S21RdVFpQVNkSlU2ZzgxN3M=");
			HttpEntity<String> entity1 = new HttpEntity<String>("parameters", headers1);
			ResponseEntity<PayPalAccesToken> response1 = restTemplate.exchange("https://api-m.sandbox.paypal.com/v1/oauth2/token?grant_type=client_credentials",HttpMethod.POST,entity1, PayPalAccesToken.class);
			String token = response1.getBody().getAccessToken();

			HttpHeaders headers = new HttpHeaders();
			headers.set("Content-Type", "application/json");
			headers.set("Authorization", "Bearer " + token);

			//--------------- Devolver fianza ---------------
			Map<String,Object> body = new HashMap<>();
			Map<String,Object> senderHeader = new HashMap<>();
			//senderHeader.put("sender_batch_id", "");
			senderHeader.put("recipient_type", "EMAIL");
			senderHeader.put("email_subject", "Devolución de la fianza");
			senderHeader.put("email_message", "Se te devuele la fianza de la plaza que reservaste en park-inn");

			Map<String,Object> item = new HashMap<>();
			Map<String,Object> amount = new HashMap<>();
			amount.put("value", Math.round((r.getPlaza().getFianza())*100.0)/100.0);
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
			amount_p.put("value", Math.round((r.getPrecioTotal() - r.getPlaza().getFianza() - 0.1*r.getPrecioTotal())*100.0)/100.0);//Poner la comisión como atributo
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
        
		HttpHeaders headers1 = new HttpHeaders();
		headers1.set("Content-Type", "application/x-www-form-urlencoded");
		headers1.set("Authorization", "Basic QWR1NGpVdFRrYUp4TkZxdWZoenRvTnAtQ1F1WldKTGt2VjVGRG5fYUlwa2hiV2xTdm5Qd1NxMlRORHNUNHZGWnQtX3VFbUZfcnRIODlNdms6RUxIYWZIQWMtMFpQclJXZVo1MFBqeFQ0TmtWNDg5UDNnZno3Q3RvWU9yLWVvQVQxekhzcVZuTlZrYm5WRkE4S21RdVFpQVNkSlU2ZzgxN3M=");
		HttpEntity<String> entity1 = new HttpEntity<String>("parameters", headers1);
		ResponseEntity<PayPalAccesToken> response1 = restTemplate.exchange("https://api-m.sandbox.paypal.com/v1/oauth2/token?grant_type=client_credentials",HttpMethod.POST,entity1, PayPalAccesToken.class);
		String token = response1.getBody().getAccessToken();
		
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		headers.set("Authorization", "Bearer " + token);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		
		ResponseEntity<PayPalClasses> response = restTemplate.exchange("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + query,HttpMethod.GET,entity, PayPalClasses.class);


        PayPalClasses paypal = response.getBody();    
       
        
    return  paypal;
    }
	
	
	

}
