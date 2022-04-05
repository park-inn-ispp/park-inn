package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.parkinn.model.Localizacion;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.Amount;
import com.parkinn.model.paypal.PayPalClasses;
import com.parkinn.model.paypal.PurchaseUnit;
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
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/plazas")
public class PlazaController {

    @Autowired
    private PlazaService plazaService;
    @Autowired
    private ReservaService reservaService;


    @GetMapping()
    public List<Plaza> filtrarPlazas(@RequestParam(name = "maxPrecioHora", required=false) Double maxPrecioHora, @RequestParam(name = "fechaInicio", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
        @RequestParam(name = "fechaFin", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin, @RequestParam(name = "zona", required=false) String zona) {
        return plazaService.filtrarPlazas(maxPrecioHora, fechaInicio, fechaFin, zona);
    }

    @GetMapping("/all")
    public List<Plaza> getPlazas() {
        return plazaService.findAll();
    }

    @PostMapping
    public ResponseEntity createPlaza(@RequestBody Plaza plaza) throws URISyntaxException {
       Localizacion localizacion = plazaService.getLocalizacion(plaza.getDireccion());
       plaza.setLatitud(localizacion.getLat());
       plaza.setLongitud(localizacion.getLon());

       if(plazaService.comprobarPlazasIguales(plaza)){
        Map<String,Object> response = new HashMap<>();
        response.put("plaza", plaza);
        response.put("error","Esta plaza ya existe");
        return ResponseEntity.badRequest().body(response);
       }else{
        Plaza savedPlaza = plazaService.guardarPlaza(plaza);
        return ResponseEntity.created(new URI("/plazas/" + savedPlaza.getId())).body(savedPlaza);
       }
    	    	
    	
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePlaza(@PathVariable Long id, @RequestBody Plaza plaza) {
    	Localizacion localizacion = plazaService.getLocalizacion(plaza.getDireccion());
        
    	Plaza currentPlaza = plazaService.findById(id);
    	currentPlaza.setLatitud(localizacion.getLat());
        currentPlaza.setLongitud(localizacion.getLon());
        currentPlaza.setDireccion(plaza.getDireccion());
        currentPlaza.setDescripcion(plaza.getDescripcion());
        currentPlaza.setAncho(plaza.getAncho());
        currentPlaza.setLargo(plaza.getLargo());
        currentPlaza.setEstaDisponible(plaza.getEstaDisponible());
        currentPlaza.setEsAireLibre(plaza.getEsAireLibre());
        currentPlaza.setFianza(plaza.getFianza());
        currentPlaza.setPrecioHora(plaza.getPrecioHora());

        currentPlaza = plazaService.guardarPlaza(currentPlaza);

        return ResponseEntity.ok(currentPlaza);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePlaza(@PathVariable Long id) {
        plazaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reservar")
    public ResponseEntity createReserva(@Valid @RequestBody Reserva reserva, @PathVariable Long id) throws URISyntaxException {
        Map<String,Object> response = new HashMap<>();
        List<Reserva> reservas = reservaService.findAll();
        response.put("reserva", reserva);
    
       for (Reserva r : reservas){
            String paypal_order_BD= r.getPaypal_order_id();
            String paypal_order_Nuevo= reserva.getPaypal_order_id();
            
            if(paypal_order_Nuevo.equals(paypal_order_BD)){
                response.put("error","La transacción ya existía en la base de datos");
                return ResponseEntity.badRequest().body(response);
            }
        }
        
        PayPalClasses paypal = reservaService.getPayPal(reserva.getPaypal_order_id());
        PurchaseUnit purchase = paypal.getPurchaseUnits().get(0);
		Amount amount = purchase.getAmount();
		
		String value = amount.getValue();
		String currencyCode = amount.getCurrencyCode();
		Double precio = Duration.between(reserva.getFechaInicio(), reserva.getFechaFin()).toMinutes() * reserva.getPlaza().getPrecioHora()/60;
		precio = precio + reserva.getPlaza().getFianza();
		reserva.setPrecioTotal(Math.round(precio*100.0)/100.0);
		
		if(!currencyCode.equals("EUR")) {
			
			response.put("error","La moneda de la transacción debe ser EUR y es " + currencyCode + ".");
	        return ResponseEntity.badRequest().body(response);
		
		}else if(!reserva.getPrecioTotal().equals(Double.parseDouble(value)) ) {
			response.put("error","El precio de la reserva es " + reserva.getPrecioTotal() + " y el de la transacción " + value);
	        return ResponseEntity.badRequest().body(response);
		
		}
        
        if(reserva.getFechaInicio().isAfter(reserva.getFechaFin())){
            response.put("error","La fecha de inicio debe ser anterior a la fecha de fin");
            return ResponseEntity.badRequest().body(response);
        }else if(reserva.getFechaInicio().isBefore(LocalDateTime.now())){
            response.put("error","No se pueden realizar reservas en el pasado");
            return ResponseEntity.badRequest().body(response);
        }else if(reservaService.reservaTieneColision(reserva)){
            response.put("error","Este horario está ocupado por otra reserva");
            return ResponseEntity.badRequest().body(response);
        }else{
            Reserva savedReserva = reservaService.guardarReserva(reserva);
            return ResponseEntity.created(new URI("/reservas/" + savedReserva.getId())).body(savedReserva);
        }
        
    	
    	
    	    	
    }

    
    @GetMapping("/{id}")
    public Object infoPlazaYCliente(@PathVariable Long id){
        Plaza p = plazaService.findById(id);
        Map<String,Object> response = new HashMap<>();
        if(p==null){
			response.put("error","Esta plaza no existe");
			return ResponseEntity.badRequest().body(response);
        }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains("ROLE_ADMIN") || p.getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            return p;
        }else{
			response.put("error","Esta plaza no es de tu propiedad");
			return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/plazasDelUsuario/{id}")
    public Object plazasCliente(@PathVariable Long id){
        List<Plaza> p = plazaService.findUserById(id);
        if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains("ROLE_ADMIN") || p.get(0).getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            return p;
        }else{
            Map<String,Object> response = new HashMap<>();
			response.put("error","Esta plaza no es de tu propiedad");
			return ResponseEntity.badRequest().body(response);
        }
    }
    
    
    
}
