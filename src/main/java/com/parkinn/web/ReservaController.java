package com.parkinn.web;

	import java.net.URI;
	import java.net.URISyntaxException;
	import java.time.LocalDateTime;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	import javax.validation.Valid;

import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
	import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.Amount;
import com.parkinn.model.paypal.PayPalClasses;
import com.parkinn.model.paypal.PurchaseUnit;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.service.PlazaService;
	import com.parkinn.service.ReservaService;


	import org.springframework.web.bind.annotation.DeleteMapping;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.PathVariable;
	import org.springframework.web.bind.annotation.PostMapping;
	import org.springframework.web.bind.annotation.PutMapping;
	import org.springframework.web.bind.annotation.RequestBody;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RequestParam;
	import org.springframework.web.bind.annotation.RestController;
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.format.annotation.DateTimeFormat;
	import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

	@RestController
	@RequestMapping("/reservas")
	public class ReservaController {

		@Autowired
		private HorarioRepository horarioRepository;
		
	    @Autowired
	    private ReservaService reservaService;

	    @GetMapping("/usuario/{id}")
	    public List<Reserva> reservasUsuario(@PathVariable Long id){
	    	return reservaService.findByUserId(id);
	    }
	    
	    @GetMapping("/plaza/{id}")
	    public List<Reserva> ReservasPlaza(@PathVariable Long id){
	    	return reservaService.findPlazaById(id);
	    }
		
	    @PreAuthorize("hasRole('ROLE_ADMIN')")
		@GetMapping("/all")
	    public List<Reserva> findAll(){
	    	return reservaService.findAll();
	    }
	    @GetMapping("/{id}/fechasNoDisponibles")
	    public List<List<LocalDateTime>> horariosNoDisponibles(@PathVariable Long id) throws URISyntaxException {
	    	return reservaService.horariosNoDisponibles(id);
	    }
	    /*
	    @GetMapping("/{id}/disponibilidad")
	    public List<Horario> horariosPlaza(@PathVariable Long id) throws URISyntaxException {
	    	return reservaService.horariosDisponibles(id);
	    }*/
	    @GetMapping("/{id}")
	    public Reserva detallesReserva(@PathVariable Long id){
	    	return reservaService.findById(id);
	    }
	    

	   
		@PutMapping("/{paypal_order_id}")
	    public ResponseEntity pagoReserva(@PathVariable String paypal_order_id, @RequestBody Reserva reserva) throws URISyntaxException {
	    	Map<String,Object> response = new HashMap<>();
	    	
	    	List<Reserva> reservas = reservaService.findAll();
	    	    	
	    	if(reservas.stream().anyMatch(x->x.getPaypal_order_id()==paypal_order_id)) {
	    		response.put("error","La transacción ya existía en la base de datos");
				return ResponseEntity.badRequest().body(response);
	    	}
	    	PayPalClasses paypal = reservaService.getPayPal(paypal_order_id);
			PurchaseUnit purchase = paypal.getPurchaseUnits().get(0);
			Amount amount = purchase.getAmount();
			
			String value = amount.getValue();
			String currencyCode = amount.getCurrencyCode();
	    	
			if(purchase == null) {
				response.put("error","La transacción no existe");
				return ResponseEntity.badRequest().body(response);
			
			}else if(currencyCode != "EUR") {
				response.put("error","La moneda de la transacción debe ser el Euro");
		        return ResponseEntity.badRequest().body(response);
			
			}else if(String.valueOf(reserva.getPrecioTotal()) != value ) {
				response.put("error","El precio de la reserva no coincide con el valor de la transacción");
		        return ResponseEntity.badRequest().body(response);
			
			}else {
				reserva.setPaypal_order_id(paypal_order_id);
				reserva = reservaService.guardarReserva(reserva);
		        return ResponseEntity.ok(reserva);

			}
	    	
	    }
	    

	    
	}
