package com.parkinn.service;

import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.PlazaRepository;
import com.parkinn.repository.ComisionRepository;
import com.parkinn.repository.ReservaRepository;

import java.net.URISyntaxException;
import java.text.DecimalFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.parkinn.model.Estado;
import com.parkinn.model.Horario;
import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.PayPalAccesToken;
import com.parkinn.model.paypal.PayPalClasses;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReservaService {

	
	
	final static String URL_CORREO = "https://parkinn-api-v3.herokuapp.com";

    @Autowired
    RestTemplate restTemplate;
	
    @Autowired
    private ReservaRepository repository;
    
    @Autowired
    private HorarioRepository horarioRepository;
    
    @Autowired
    private PlazaService plazaService;
    
    @Autowired
    private PlazaRepository plazaRepository;
  
	@Autowired
	private ComisionRepository comisionRepository;

    
    @Autowired
    private MailService mailService;

   
    
	public List<Reserva> findAll(){
        return repository.findAll();
    }
	
	

	
    public Reserva guardarReserva(Reserva r){

        r.setEstado(Estado.pendiente);
        r.setFechaSolicitud(LocalDateTime.now());
		r.setComision(comisionRepository.getById((long) 1).getPorcentaje());
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
		System.out.println(Math.round((r.getPrecioTotal() - r.getFianza())*100.0)/100.0);
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
    
    
        
   
    
    public List<List<LocalDateTime>> horariosNoDisponibles(Long id,boolean tramosPersonalizados){
    	List<Reserva> lr = repository.findByPlazaId(id); //Reservas de esa plaza
		List<List<LocalDateTime>> horarios = new ArrayList<>();
		// Horarios ocupados por otras reservas
		if(!lr.isEmpty()) {
			for(int i =0; i<lr.size(); i++) {
       			List<LocalDateTime> HorarioOcupado = new ArrayList<LocalDateTime>();
       			HorarioOcupado.add(lr.get(i).getFechaInicio());
       			HorarioOcupado.add(lr.get(i).getFechaFin());
       			horarios.add(HorarioOcupado);
			}
			
		}
		// Fechas pasadas
		List<LocalDateTime> horarioNoValidoAntiguo = new ArrayList<LocalDateTime>();
		LocalDateTime fechaAntigua=LocalDateTime.of(2000, 1, 1, 12, 0, 0);
		LocalDateTime hoy = LocalDateTime.now();
		horarioNoValidoAntiguo.add(fechaAntigua);
		horarioNoValidoAntiguo.add(hoy);
		horarios.add(horarioNoValidoAntiguo);

		if(tramosPersonalizados){
			//Horarios no disponibles especificados por el propietario a partir de hoy (todos excepto horarios disponibles seleccionados)
		
		List<Horario> horariosDisponibles= horarioRepository.findHorariosByPlazaIdSortedByFechaInicio(id).stream().filter(horario -> horario.getFechaFin().isAfter(LocalDateTime.now())).collect(Collectors.toList());
		if (horariosDisponibles.size()==0){
            hoy = LocalDateTime.now();
            List<LocalDateTime> horarioNoValido = new ArrayList<LocalDateTime>();
            LocalDateTime fechaLejana=LocalDateTime.of(3000, 1, 1, 12, 0, 0);
            horarioNoValido.add(hoy);
            horarioNoValido.add(fechaLejana);
            horarios.add(horarioNoValido);
        }
		for (int i=0;i<horariosDisponibles.size();i++) {

			if(i==0){

				Horario horarioDisponible1 = horariosDisponibles.get(i);
				
				hoy = LocalDateTime.now();

				if(horarioDisponible1.getFechaInicio().isAfter(hoy)){ // Fecha de hoy hasta la fecha de inicio del horario
					List<LocalDateTime> horarioNoValidoInicio = new ArrayList<LocalDateTime>();
					horarioNoValidoInicio.add(hoy);
					horarioNoValidoInicio.add(horarioDisponible1.getFechaInicio());
					horarios.add(horarioNoValidoInicio);
				}

				if (i==horariosDisponibles.size()-1){ // Solo hay 1 elemento en la lista
					List<LocalDateTime> horarioNoValido = new ArrayList<LocalDateTime>();
					LocalDateTime fechaLejana=LocalDateTime.of(3000, 1, 1, 12, 0, 0);
					horarioNoValido.add(horarioDisponible1.getFechaFin());
					horarioNoValido.add(fechaLejana);
					horarios.add(horarioNoValido);

				}else{ // Hay más horarios en la lista
					List<LocalDateTime> horarioNoValido = new ArrayList<LocalDateTime>();

					Horario horarioSiguiente = horariosDisponibles.get(i+1);
					horarioNoValido.add(horarioDisponible1.getFechaFin());
					horarioNoValido.add(horarioSiguiente.getFechaInicio());
					horarios.add(horarioNoValido);

				}
				


			}else if(i==horariosDisponibles.size()-1){ // Último horario de la lista, y en la lista hay más de 1 elemento
				List<LocalDateTime> horarioNoValido = new ArrayList<LocalDateTime>();

				Horario horarioDisponible1 = horariosDisponibles.get(i);
				LocalDateTime fechaLejana=LocalDateTime.of(3000, 1, 1, 12, 0, 0);
				horarioNoValido.add(horarioDisponible1.getFechaFin());
				horarioNoValido.add(fechaLejana);
				horarios.add(horarioNoValido);

			}else{ // Hay más horarios en la lista y no se trata del último elemento

				Horario horarioDisponible1 = horariosDisponibles.get(i);
				Horario horarioDisponibleSiguiente = horariosDisponibles.get(i+1);

				List<LocalDateTime> horarioNoValido = new ArrayList<LocalDateTime>();
				horarioNoValido.add(horarioDisponible1.getFechaFin());
				horarioNoValido.add(horarioDisponibleSiguiente.getFechaInicio());
				horarios.add(horarioNoValido);
				
				}
			}
		}

		
		return horarios;
		
		
    }

	public Boolean reservaTieneColision(Reserva res){
			long idPlaza = res.getPlaza().getId();
			List<Horario> horarios=horarioRepository.findHorariosByPlazaId(idPlaza);
			for (Horario h: horarios){
				if(h.getFechaFin().isAfter(res.getFechaInicio()) && h.getFechaInicio().isBefore(res.getFechaFin())){
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
            errores.add("Este horario no está disponible o está ocupado por otra reserva");
           
        }
		return errores;
	}
	
	public Object confirmarServicio(Reserva r, Object user, String emailPropietario){
		if(user.equals(r.getUser().getEmail()) && !r.getEstado().equals(Estado.confirmadaPropietario)){
			try {
				String subject = "Servicio confirmado por parte del cliente";
				String text = "El cliente ha indicado que la reserva ha sido exitosa.\nPorfavor, si desea confirmarlo o poner una incidencia haga clic en el siguiente enlace: "+URL_CORREO+"/reservas/"+r.getId()+"\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(emailPropietario, subject, text);
			}catch(MailException e) {
				r.setEstado(Estado.confirmadaUsuario);
			}
			r.setEstado(Estado.confirmadaUsuario);
		}else if(user.equals(emailPropietario) && !r.getEstado().equals(Estado.confirmadaUsuario)){
			try {
				String subject = "Servicio confirmado por parte del propietario";
				String text = "El propietario de la plaza ha indicado que la reserva ha sido exitosa.\nPorfavor, si desea confirmarlo o poner una incidencia haga clic en el siguiente enlace: "+URL_CORREO+"/reservas/"+r.getId()+"\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(r.getUser().getEmail(), subject, text);
			}catch (MailException e) {
				r.setEstado(Estado.confirmadaPropietario);
			}
			r.setEstado(Estado.confirmadaPropietario);
		}else{
			try {
				String subject = "Servicio confirmado";
				String text = "Se ha confirmado que la reserva ha sido exitosa.\n¡Esperamos volver a verte!\n\nGracias, el equipo de ParkInn.";
				mailService.sendEmail(r.getUser().getEmail(), subject, text);
				mailService.sendEmail(emailPropietario, subject, text);
			}catch (MailException e) {
				r.setEstado(Estado.confirmadaAmbos);
			}
			r.setEstado(Estado.confirmadaAmbos);
			DecimalFormat df = new DecimalFormat("#.00");
			df.setMaximumFractionDigits(2);
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
			Integer am = 15;
			//amount.put("value", (double)Math.round((r.getPlaza().getFianza()) * 100d) / 100d);
			amount.put("value", am);
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
			senderHeader_p.put("email_subject", "Ingresos de la plaza " + r.getDireccion());
			senderHeader_p.put("email_message", "Se te ha ingresado el dinero ganado sobre tu plaza " + r.getDireccion());

			Map<String,Object> item_p = new HashMap<>();
			Map<String,Object> amount_p = new HashMap<>();

			amount_p.put("value", Math.round((r.getPrecioTotal() - r.getFianza() - r.getComision()*(r.getPrecioTotal()- r.getFianza()))*100.0)/100.0);

		
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
