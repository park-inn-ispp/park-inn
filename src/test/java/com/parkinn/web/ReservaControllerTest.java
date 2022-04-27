package com.parkinn.web;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkinn.model.Client;
import com.parkinn.model.Estado;
import com.parkinn.model.Horario;
import com.parkinn.model.Localizacion;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.Amount;
import com.parkinn.model.paypal.PayPalClasses;
import com.parkinn.model.paypal.PurchaseUnit;
import com.parkinn.repository.ClientRepository;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.service.ClientService;
import com.parkinn.service.CustomUserDetailsService;
import com.parkinn.service.HorarioService;
import com.parkinn.service.MailService;
import com.parkinn.service.PlazaService;
import com.parkinn.service.ReservaService;

@WebMvcTest(controllers = ReservaController.class)
public class ReservaControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	CustomUserDetailsService customUserDetailsService;

	@MockBean
	PlazaService plazaService;

	@MockBean
	ReservaService reservaService;

	@MockBean
	HorarioService horarioService;

	@MockBean
    private HorarioRepository horarioRepository;
    
	@MockBean
    private ClientRepository clientRepository;

	@MockBean
	MailService mailService;

	@MockBean
	DataSource dataSource;
	
	@Autowired
  	private ObjectMapper objectMapper;
	
	Client c1;
	Client c2;
	Client c3;
	Client c4;
	Plaza p1;
	Plaza p2;
	Localizacion l1;
	Localizacion l2;
	Reserva r1;
	PayPalClasses paypal;
	PurchaseUnit purchase;
	Amount amount;
	LocalDateTime fecha = LocalDateTime.now();
	Horario h1;
	
	@BeforeEach
	void setup() {
		c1 = new Client(1l);
		c2 = new Client(2l);
		p1 = new Plaza(1l,c1);
		c1.setEmail("email@email.com");
		c2.setEmail("otro@gmail.com");
		p1.setFianza(10.0);
		p1.setAncho(2.0);
		p1.setLargo(3.0);
		p1.setDireccion("Calle Real,5,Carmona,Sevilla,41520");
		p1.setPrecioHora(5.0);
		p1.setEstaDisponible(true);
		l1 = new Localizacion();
		String latitud = "22,22";
		String longitud = "33,33";
		List<String> coordenadas = new ArrayList<String>();
		coordenadas.add(latitud);
		coordenadas.add(longitud);
		l1.setLat(latitud);
		l1.setLon(longitud);
		p2 = new Plaza(2l,c1);
		p2.setFianza(10.0);
		p2.setAncho(2.0);
		p2.setLargo(3.0);
		p2.setDireccion("mal");
		p2.setPrecioHora(5.0);
		l2 = new Localizacion();
		l2.setLat(latitud);
		l2.setLon(longitud);
		r1 = new Reserva(1l);
		r1.setFechaInicio(fecha.plusHours(1l));
		r1.setFechaFin(fecha);
		r1.setPaypal_order_id("id");
		r1.setPlaza(p1);
		r1.setUser(c1);
		List<Reserva> reservas = new ArrayList<Reserva>();
		paypal = new PayPalClasses();
		purchase = new PurchaseUnit();
		amount = new Amount();
		amount.setCurrencyCode("EUR");
		amount.setValue("10.0");
		purchase.setAmount(amount);
		List<PurchaseUnit> purchases = new ArrayList<PurchaseUnit>();
		purchases.add(purchase);
		paypal.setPurchaseUnits(purchases);
		h1 = new Horario(1l);
		h1.setFechaFin(fecha);
		h1.setFechaInicio(fecha.plusHours(1l));
		List<Horario> horarios = new ArrayList<Horario>();
		h1.setPlaza(p1);
		given(this.clientRepository.findById(1l)).willReturn(Optional.of(c1));
		given(this.plazaService.findAll()).willReturn(asList(p1,p2));
		given(this.reservaService.findById(1l)).willReturn(r1);
		given(this.reservaService.findAll()).willReturn(reservas);
		given(this.reservaService.findByUserId(1l)).willReturn(reservas);
		given(this.plazaService.findById(p1.getId())).willReturn(p1);
		given(this.plazaService.getLocalizacion(p1.getDireccion())).willReturn(l1);
		given(this.plazaService.latitudLongitudDiferentes(l1.getLat(), l1.getLon())).willReturn(coordenadas);
		given(this.plazaService.guardarPlaza(p1)).willReturn(p1);
		given(this.horarioRepository.findHorariosByPlazaId(1l)).willReturn(horarios);
		given(this.horarioService.guardarHorario(h1)).willReturn(h1);
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetResevasCliente() throws Exception {
	
		mockMvc.perform(get("/reservas/usuario/{id}",1)).andExpect(status().isOk());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetResevasClienteNoExiste() throws Exception {
		given(this.clientRepository.findById(1l)).willReturn(Optional.empty());
		mockMvc.perform(get("/reservas/usuario/{id}",1L)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se encuentra al usuario"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetResevasPlaza() throws Exception {
		given(this.plazaService.findById(p1.getId())).willReturn(null);
		mockMvc.perform(get("/reservas/plaza/{id}",1)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se encuentra la plaza"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetResevasPlazaNoExiste() throws Exception {
	
		mockMvc.perform(get("/reservas/plaza/{id}",1)).andExpect(status().isOk());
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetResevasAll() throws Exception {
	
		mockMvc.perform(get("/reservas/all")).andExpect(status().isOk());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetDetallesReserva() throws Exception {
	
		mockMvc.perform(get("/reservas/{id}",1)).andExpect(status().isOk());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetDetallesReservaNoExiste() throws Exception {
		given(this.reservaService.findById(1l)).willReturn(null);
		mockMvc.perform(get("/reservas/{id}",1)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se encuentra la reserva"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testAceptarReservaNoSeEncuentra() throws Exception {
		
		mockMvc.perform(get("/reservas/{id}/aceptar",3)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se encuentra la reserva"));
	}
	
	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testAceptarReservaFail() throws Exception {
		
		mockMvc.perform(get("/reservas/{id}/aceptar",1)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("Esta reserva no es sobre una plaza de tu propiedad"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testRechazarReservaNoSeEncuentra() throws Exception {
		
		mockMvc.perform(get("/reservas/{id}/rechazar",3)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se encuentra la reserva"));
	}
	
	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testRechazarReservaFail() throws Exception {
		
		mockMvc.perform(get("/reservas/{id}/rechazar",1)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("Esta reserva no es sobre una plaza de tu propiedad"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testConfirmarReservaNoSeEncuentra() throws Exception {
		
		mockMvc.perform(get("/reservas/{id}/confirmar",3)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se encuentra la reserva"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCancelarReservaNoSeEncuentra() throws Exception {
		
		mockMvc.perform(get("/reservas/{id}/cancelar",3)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se encuentra la reserva"));
	}
	
	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testCancelarReservaCancelada() throws Exception {
		r1.setEstado(Estado.cancelada);
		mockMvc.perform(get("/reservas/{id}/cancelar",1)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se puede cancelar una reserva que ya no está en proceso"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testDenegarReservaNoSeEncuentra() throws Exception {
		
		mockMvc.perform(get("/reservas/{id}/denegar",3)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No se encuentra la reserva"));
	}
	
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCancelarReserva() throws Exception {
		r1.setEstado(Estado.aceptada);
		mockMvc.perform(get("/reservas/{id}/cancelar",1)).andExpect(status().isOk());
	}
}
