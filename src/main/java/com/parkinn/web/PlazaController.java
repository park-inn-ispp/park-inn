package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.validation.Valid;

import com.parkinn.model.Horario;
import com.parkinn.model.Localizacion;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.Amount;
import com.parkinn.model.paypal.PayPalClasses;
import com.parkinn.model.paypal.PurchaseUnit;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.service.HorarioService;
import com.parkinn.service.MailService;
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
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/plazas")
public class PlazaController {

	final static String URL_CORREO = "https://parkinn-api-v3.herokuapp.com";
	
    @Autowired
    private PlazaService plazaService;
    @Autowired
    private ReservaService reservaService;
    @Autowired
	private HorarioService horarioService;
    @Autowired 
    private ReservaRepository reservaRepository;
    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private MailService mailService;
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/all")
    public Object getPlazas() {
        return plazaService.findAll();
    }
    

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @SuppressWarnings("rawtypes")
	@PostMapping()
    public ResponseEntity createPlaza(@RequestBody Plaza plaza) throws URISyntaxException {
        List<String> errores = new ArrayList<>();
        Map<String,Object> response = new HashMap<>();
        try {
            Localizacion localizacion = plazaService.getLocalizacion(plaza.getDireccion());
            // Se cambia ligeramente si ya existen esas coordenadas en otra plaza
            List<String> nuevasCoordenadas= plazaService.latitudLongitudDiferentes(localizacion.getLat(),localizacion.getLon());
            plaza.setLatitud(nuevasCoordenadas.get(0));
            plaza.setLongitud(nuevasCoordenadas.get(1));
          }
          catch(Exception e) {
            errores.add("La dirección insertada no existe o no es reconocida por el sistema. Por favor, indique el tipo (calle, avenida...) y nombre correcto de su dirección");
            response.put("plaza", plaza);
            response.put("errores",errores);
            return ResponseEntity.badRequest().body(response);
          }
        
       
        Plaza savedPlaza = plazaService.guardarPlaza(plaza);
        return ResponseEntity.created(new URI("/plazas/" + savedPlaza.getId())).body(savedPlaza);
    	
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
                // Si se cambia la dirección se recalculan las coordenadas, 
                // y si ya existen en otra plaza, se modifican ligeramente para no superponerse en el mapa
                if(!currentPlaza.getDireccion().equals(plaza.getDireccion())){
                    Localizacion localizacion = plazaService.getLocalizacion(plaza.getDireccion());
                    List<String> nuevasCoordenadas= plazaService.latitudLongitudDiferentes(localizacion.getLat(),localizacion.getLon());
                    plaza.setLatitud(nuevasCoordenadas.get(0));
                    plaza.setLongitud(nuevasCoordenadas.get(1));
                }
               
             
            } catch(Exception e) {
                errores.add("La dirección insertada no existe o no es reconocida por el sistema. Por favor, indique el tipo (calle, avenida...) y nombre correcto de su dirección");
                response.put("plaza", plaza);
                response.put("errores",errores);
                return ResponseEntity.badRequest().body(response);
            }

          
               
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
    	}else{
            
  			errores.add("No puedes editar una plaza que no es de tu propiedad");            
            response.put("errores", errores);
  			return ResponseEntity.badRequest().body(response);
        }
    	
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/{id}/horarios")
    public List<Horario> getHorarios(@PathVariable Long id ) throws URISyntaxException {
        return horarioRepository.findHorariosByPlazaId(id);
    }
    
  //@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
  	@DeleteMapping("/{id}")
      public ResponseEntity deletePlaza(@PathVariable Long id) {
      	Plaza currentPlaza = plazaService.findById(id);
          List<String> errores = new ArrayList<String>();
      	if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||currentPlaza.getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
              //List<String> errores1 = new ArrayList<String>();
              //Map<String,Object> response = new HashMap<>();
      		if(reservaRepository.findByPlazaId(id).isEmpty()) { 
      		
      			List<Horario> horarios = horarioRepository.findHorariosByPlazaId(id);
        		
        		for(int i = 0; i<horarios.size(); i++) {
        			Horario horario = horarios.get(i);
        			horarioRepository.delete(horario);
        		}
      			plazaService.deleteById(id);
      			return ResponseEntity.ok().build();
      		}else {
  				
      			List<Horario> horarios = horarioRepository.findHorariosByPlazaId(id);
        		
        		for(int i = 0; i<horarios.size(); i++) {
        			Horario horario = horarios.get(i);
        			horarioRepository.delete(horario);
        		}
      			reservaRepository.findByPlazaId(id).forEach(res->res.setPlaza(null));
      			plazaService.deleteById(id);
      			return ResponseEntity.ok().build();
      		}
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
        reserva.setPropietarioId(reserva.getPlaza().getAdministrador().getId());
		
		if(!currencyCode.equals("EUR")) {
			
			errores.add("La moneda de la transacción debe ser EUR y es " + currencyCode + ".");
	        response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
		
		// }else if(!reserva.getPrecioTotal().equals(Double.parseDouble(value)) ) {
		// 	errores.add("El precio de la reserva es " + reserva.getPrecioTotal() + " y el de la transacción " + value);
	    //     response.put("errores", errores);
		// 	return ResponseEntity.badRequest().body(response);
		
		}else{
			try {
			String subject = "Nueva solicitud de reserva ";
			String text = "Tiene una nueva solicitud de reserva para una de sus plazas.\nGestionela desde aquí: "+URL_CORREO+"/mis-reservas-de-mis-plazas/plaza/"+reserva.getPlaza().getId()+"\n\nGracias, el equipo de ParkInn.";
			mailService.sendEmail(reserva.getPlaza().getAdministrador().getEmail(), subject, text);
			}catch(MailException m) {
	            Reserva savedReserva = reservaService.guardarReserva(reserva);
	            return ResponseEntity.created(new URI("/reservas/" + savedReserva.getId())).body(savedReserva);
			}
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
			return p;
        }else if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || p.get(0).getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
            return p;
        }else{
            errores.add("Esta plaza no es de tu propiedad");
            response.put("errores", errores);
			return ResponseEntity.badRequest().body(response);
        }
    }
    

    @SuppressWarnings("rawtypes")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @PostMapping("/{id}/crearHorarios")
    public ResponseEntity createHorario(@Valid @RequestBody Horario horario, @PathVariable Long id ) throws URISyntaxException {
        List<String> errores = new ArrayList<>();
        Map<String,Object> response = new HashMap<>();
   	 	List<Horario> horariosPlaza = horarioRepository.findHorariosByPlazaId(horario.getPlaza().getId());
		Boolean horario_Igual = false;
   	 	for (Horario h: horariosPlaza){
			if((h.getFechaFin()).isAfter((ChronoLocalDateTime<LocalDate>) horario.getFechaInicio()) && (h.getFechaInicio().isBefore((ChronoLocalDateTime<LocalDate>) horario.getFechaFin()))){
				horario_Igual = true;
                break;
			}
		}
   	 	if(horario.getFechaInicio().isAfter(horario.getFechaFin())) {
   	 		errores.add("No puede existir una fecha de inicio posterior a la fecha de fin");
   	 		response.put("horario", horario);
   	 		response.put("errores",errores);
   	 		return ResponseEntity.badRequest().body(response);
   	 	}
   	 	else if(horario.getFechaInicio().isEqual(horario.getFechaFin())) {
   	 		errores.add("No puede existir un tramo horario cuya fecha de inicio y fecha de fin coincidan");
   	 		response.put("horario", horario);
   	 		response.put("errores",errores);
   	 		return ResponseEntity.badRequest().body(response);
   	 	}
   	 	else if(horario_Igual) {
   	 		errores.add("Este tramo horario entra en conflicto con otro. Seleccione otras fechas");
   	 		response.put("horario", horario);
   	 		response.put("errores",errores);
   	 		return ResponseEntity.badRequest().body(response);
   	 	}
   	 	else {
   	 		horario.setActivo(false);
   	 		Horario nuevoHorario = horarioService.guardarHorario(horario);
   	 		return ResponseEntity.created(new URI("/horarios/" + nuevoHorario.getId())).body(nuevoHorario);
   	 	}
    }
    
    
    @SuppressWarnings({ "rawtypes", "unused" })
   	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
       @PutMapping("/{id}/cambiarDisponibilidad/{disponibilidad}")
       public ResponseEntity updateDisponibilidad(@PathVariable Long id, @PathVariable Boolean disponibilidad) {
       	Map<String,Object> response = new HashMap<>();
       	List<String> errores = new ArrayList<String>();
           Plaza currentPlaza = plazaService.findById(id);

       	if(SecurityContextHolder.getContext().getAuthentication().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ||currentPlaza.getAdministrador().getEmail().equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
           	
           	currentPlaza.setTramos(disponibilidad);

           	currentPlaza = plazaService.guardarPlaza(currentPlaza);
               return ResponseEntity.ok(currentPlaza);

             
       	}else{
               
     			errores.add("No puedes editar una plaza que no es de tu propiedad");            
               response.put("errores", errores);
     			return ResponseEntity.badRequest().body(response);
           }
       }
}

