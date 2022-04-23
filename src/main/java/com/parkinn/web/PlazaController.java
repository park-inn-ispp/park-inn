package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/plazas")
public class PlazaController {

    @Autowired
    private PlazaService plazaService;
    @Autowired
    private ReservaService reservaService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping()
    public List<Plaza> filtrarPlazas(@RequestParam(name = "maxPrecioHora", required=false) Double maxPrecioHora, @RequestParam(name = "fechaInicio", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
        @RequestParam(name = "fechaFin", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin, @RequestParam(name = "zona", required=false) String zona) {
        return plazaService.filtrarPlazas(maxPrecioHora, fechaInicio, fechaFin, zona);
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/all")
    public Object getPlazas() {
        return plazaService.findAll();
    }
    
    @SuppressWarnings("rawtypes")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PostMapping
    public ResponseEntity createPlaza(@RequestBody Plaza plaza) throws URISyntaxException {
        List<String> errores = new ArrayList<>();
        Map<String,Object> response = new HashMap<>();
        try {
            Localizacion localizacion = plazaService.getLocalizacion(plaza.getDireccion());
            plaza.setLatitud(localizacion.getLat());
            plaza.setLongitud(localizacion.getLon());
          }
          catch(Exception e) {
            errores.add("La dirección insertada no existe. Por favor, indique el tipo (calle, avenida...) y nombre correcto de su dirección");
            response.put("plaza", plaza);
            response.put("errores",errores);
            return ResponseEntity.badRequest().body(response);
          }
          
        
       if(plazaService.comprobarPlazasIguales(plaza.getDireccion(),plaza.getAdministrador())){
        
        errores.add("Esta plaza ya existe. Intenta añadir una plaza con una dirección diferente");
        response.put("plaza", plaza);
        response.put("errores",errores);
        return ResponseEntity.badRequest().body(response);
       }else{
        Plaza savedPlaza = plazaService.guardarPlaza(plaza);
        return ResponseEntity.created(new URI("/plazas/" + savedPlaza.getId())).body(savedPlaza);
       }
    	    	
    	
    }

    @SuppressWarnings("rawtypes")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PutMapping("/{id}")
    public ResponseEntity updatePlaza(@PathVariable Long id, @RequestBody Plaza plaza) {
    	Map<String,Object> response = new HashMap<>();
    	List<String> errores = new ArrayList<String>();
        Plaza currentPlaza = plazaService.findById(id);

    	if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||currentPlaza.getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            try{
                Localizacion localizacion = plazaService.getLocalizacion(plaza.getDireccion());
                currentPlaza.setLatitud(localizacion.getLat());
                currentPlaza.setLongitud(localizacion.getLon());
            } catch(Exception e) {
                errores.add("La dirección insertada no existe o no es reconocida por el sistema. Por favor, indique el tipo (calle, avenida...) y nombre correcto de su dirección");
                response.put("plaza", plaza);
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
            }

            if(plazaService.comprobarPlazasIgualesEditar(plaza.getDireccion(),currentPlaza.getAdministrador(),id)){
        
                errores.add("Esta plaza ya existe en tu colección. Intenta añadir una plaza con una dirección diferente");
                response.put("plaza", plaza);
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
               }else{
               
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
           
           

    	}else{
            
  			errores.add("No puedes editar una plaza que no es de tu propiedad");            
            response.put("errores", errores);
  			return ResponseEntity.badRequest().body(response);
        }
    	
    }
    
    @SuppressWarnings("rawtypes")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity deletePlaza(@PathVariable Long id) {
    	Plaza currentPlaza = plazaService.findById(id);
        List<String> errores = new ArrayList<String>();
    	if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||currentPlaza.getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
    		 plazaService.deleteById(id);
    	     return ResponseEntity.ok().build();

    	}else{
            Map<String,Object> response = new HashMap<>();
  			errores.add("Esta plaza no es de tu propiedad");            
            response.put("error", errores);
  			return ResponseEntity.badRequest().body(response);
        }
        
    }

    @SuppressWarnings("rawtypes")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/{id}/validateReservaAntesPago")
    public ResponseEntity validateReservaAntesPago(@Valid @RequestBody Reserva reserva, @PathVariable Long id) throws URISyntaxException {
        Map<String,Object> response = new HashMap<>();
        response.put("reserva", reserva);

        List<String> errores = reservaService.erroresNuevaReservaAntesDelPago(reserva);
       
        if(errores.isEmpty()){
            return ResponseEntity.ok().build();
        }else{
            response.put("errores", errores);
            return ResponseEntity.badRequest().body(response);
        }        
    	    	
    }
    
    @SuppressWarnings("rawtypes")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/{id}/reservar") //Tras realizar el pago
    public ResponseEntity createReserva(@Valid @RequestBody Reserva reserva, @PathVariable Long id) throws URISyntaxException {
        Map<String,Object> response = new HashMap<>();
        List<Reserva> reservas = reservaService.findAll();
        response.put("reserva", reserva);
        
        List<String> errores = reservaService.erroresNuevaReservaAntesDelPago(reserva); 
        
       
       if(!errores.isEmpty()){ //Alguien ha reservado lo mismo que tú justo antes de que pagaras o la fecha inicio ya es pasado
            response.put("errores", errores);
            return ResponseEntity.badRequest().body(response);
       }
    
       for (Reserva r : reservas){
            String paypal_order_BD= r.getPaypal_order_id();
            String paypal_order_Nuevo= reserva.getPaypal_order_id();
            
            if(paypal_order_Nuevo.equals(paypal_order_BD)){
                errores.add("La transacción ya existía en la base de datos");
                response.put("errores", errores);
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
			
			errores.add("La moneda de la transacción debe ser EUR y es " + currencyCode + ".");
	        response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
		
		}else if(!reserva.getPrecioTotal().equals(Double.parseDouble(value)) ) {
			errores.add("El precio de la reserva es " + reserva.getPrecioTotal() + " y el de la transacción " + value);
	        response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
		
		}else{
            Reserva savedReserva = reservaService.guardarReserva(reserva);
            return ResponseEntity.created(new URI("/reservas/" + savedReserva.getId())).body(savedReserva);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}")
    public Object infoPlazaYCliente(@PathVariable Long id){
        Plaza p = plazaService.findById(id);
        List<String> errores = new ArrayList<String>();
        Map<String,Object> response = new HashMap<>();
        
        
        if(p==null){
			errores.add("Esta plaza no existe");
			response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else{
            return p;

        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}/formularioEditar")
    public Object infoPlazaYClienteFormularioEditar(@PathVariable Long id){
        Plaza p = plazaService.findById(id);
        List<String> errores = new ArrayList<String>();
        Map<String,Object> response = new HashMap<>();
        
        if(p==null){
            errores.add("Esta plaza no existe");
            response.put("errores", errores);
            return ResponseEntity.badRequest().body(response);
        }


        if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||p.getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
        
                return p;

            
         }else{
            errores.add("No puedes visualizar ni editar una plaza que no es de tu propiedad");            
            response.put("errores", errores);
  			return ResponseEntity.badRequest().body(response);
    }
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/plazasDelUsuario/{id}")
    public Object plazasCliente(@PathVariable Long id){
        List<Plaza> p = plazaService.findUserById(id);
        Map<String,Object> response = new HashMap<>();
        List<String> errores = new ArrayList<String>();
        if(p.isEmpty()){
            errores.add("Este usuario no tiene ninguna plaza");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || p.get(0).getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            return p;
        }else{
            errores.add("Esta plaza no es de tu propiedad");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }
    }
   	}
