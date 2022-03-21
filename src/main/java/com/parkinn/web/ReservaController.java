package com.parkinn.web;

	import java.net.URI;
	import java.net.URISyntaxException;
	import java.time.LocalDateTime;
	import java.util.HashMap;
	import java.util.List;
	import java.util.Map;

	import javax.validation.Valid;

	import com.parkinn.model.Plaza;
	import com.parkinn.model.Reserva;
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

	@RestController
	@RequestMapping("/reservas")
	public class ReservaController {

	
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
	    
	    @GetMapping("/{id}")
	    public Reserva detallesReserva(@PathVariable Long id){
	    	return reservaService.findById(id);
	    }
	
	}
