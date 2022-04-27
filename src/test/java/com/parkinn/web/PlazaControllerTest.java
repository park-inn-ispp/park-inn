package com.parkinn.web;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkinn.model.Client;
import com.parkinn.model.Horario;
import com.parkinn.model.Localizacion;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.model.Role;
import com.parkinn.model.paypal.Amount;
import com.parkinn.model.paypal.PayPalClasses;
import com.parkinn.model.paypal.PurchaseUnit;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.service.CustomUserDetailsService;
import com.parkinn.service.HorarioService;
import com.parkinn.service.MailService;
import com.parkinn.service.PlazaService;
import com.parkinn.service.ReservaService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PlazaController.class)
public class PlazaControllerTest {

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
    private ReservaRepository reservaRepository;

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
		given(this.plazaService.findAll()).willReturn(asList(p1,p2));
		given(this.reservaService.findAll()).willReturn(reservas);
		given(this.plazaService.findById(p1.getId())).willReturn(p1);
		given(this.plazaService.getLocalizacion(p1.getDireccion())).willReturn(l1);
		given(this.plazaService.latitudLongitudDiferentes(l1.getLat(), l1.getLon())).willReturn(coordenadas);
		given(this.plazaService.guardarPlaza(p1)).willReturn(p1);
		given(this.horarioRepository.findHorariosByPlazaId(1l)).willReturn(horarios);
		given(this.horarioService.guardarHorario(h1)).willReturn(h1);
	}

	@WithMockUser(value = "spring")
    @Test
	void testGetPlazas() throws Exception {
		mockMvc.perform(get("/plazas/all")).andExpect(status().isOk())
			.andExpect(jsonPath("$.size()", Matchers.is(2)));
	}
	

	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCreatePlazaError() throws Exception {
		p1.setDireccion("mal");
		mockMvc.perform(post("/plazas").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p1)))
		.andExpect(status().isBadRequest());
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUpdatePlaza() throws Exception {
		p1.setFianza(12.0);
		mockMvc.perform(put("/plazas/{id}/",p1.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p1)))
			.andExpect(status().isOk()).andExpect(jsonPath("$.fianza").value(12.0));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUpdatePlazaWrongDirection() throws Exception {
		given(this.plazaService.findById(p2.getId())).willReturn(p1);
		mockMvc.perform(put("/plazas/{id}/",p2.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p2)))
		.andExpect(status().isBadRequest()).andExpect(jsonPath("$.errores[0]").value("La dirección insertada no existe o no es reconocida por el sistema. Por favor, indique el tipo (calle, avenida...) y nombre correcto de su dirección"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testDeletePlaza() throws Exception {
		mockMvc.perform(delete("/plazas/{id}",p2.getId())).andExpect(status().isOk());
	}
	

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCreateReservaPrecioMal() throws Exception {
		amount.setValue("15.0");
		List<String> errores = new ArrayList<String>();
		given(this.reservaService.erroresNuevaReservaAntesDelPago(r1)).willReturn(errores);
		given(this.reservaService.getPayPal(r1.getPaypal_order_id())).willReturn(paypal);
		mockMvc.perform(post("/plazas/{id}/reservar",p1.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
			.andExpect(status().isBadRequest());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCreateReservaMonedaMal() throws Exception {
		amount.setCurrencyCode("USD");
		List<String> errores = new ArrayList<String>();
		given(this.reservaService.erroresNuevaReservaAntesDelPago(r1)).willReturn(errores);
		given(this.reservaService.getPayPal(r1.getPaypal_order_id())).willReturn(paypal);
		mockMvc.perform(post("/plazas/{id}/reservar",p1.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
			.andExpect(status().isBadRequest());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetPlaza() throws Exception {
		mockMvc.perform(get("/plazas/{id}",1)).andExpect(status().isOk())
			.andExpect(jsonPath("$.fianza").value("10.0"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetPlazaNoExiste() throws Exception {
		given(this.plazaService.findById(3l)).willReturn(null);
		mockMvc.perform(get("/plazas/{id}",3)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("Esta plaza no existe"));
	}
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetPlazaFormulario() throws Exception {
		mockMvc.perform(get("/plazas/{id}/formularioEditar",1)).andExpect(status().isOk());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetPlazaFormularioNoExiste() throws Exception {
		given(this.plazaService.findById(3l)).willReturn(null);
		mockMvc.perform(get("/plazas/{id}/formularioEditar",3)).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("Esta plaza no existe"));
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetPlazasCliente() throws Exception {
		List<Plaza> plazas = new ArrayList<Plaza>();
		plazas.add(p1);
		given(this.plazaService.findUserById(c1.getId())).willReturn(plazas);
		mockMvc.perform(get("/plazas/plazasDelUsuario/{id}",1)).andExpect(status().isOk());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testValidateReservaAntesDePago() throws Exception {
		List<String> errores = new ArrayList<String>();
		given(this.reservaService.erroresNuevaReservaAntesDelPago(r1)).willReturn(errores);
		given(this.reservaService.getPayPal(r1.getPaypal_order_id())).willReturn(paypal);
		mockMvc.perform(post("/plazas/{id}/validateReservaAntesPago",p1.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(r1)))
		.andExpect(status().isOk());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUpdateDisponibilidad() throws Exception {
		p1.setFianza(12.0);
		mockMvc.perform(put("/plazas/{id}/cambiarDisponibilidad/{disponibilidad}",p1.getId(),false).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p1)))
			.andExpect(status().isOk());
	}
	
	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCreateHorario() throws Exception {
		List<String> errores = new ArrayList<String>();
		given(this.reservaService.erroresNuevaReservaAntesDelPago(r1)).willReturn(errores);
		given(this.reservaService.getPayPal(r1.getPaypal_order_id())).willReturn(paypal);
		mockMvc.perform(post("/plazas/{id}/crearHorarios",p1.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(h1)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.errores[0]").value("No puede existir una fecha de inicio posterior a la fecha de fin"));
	}
	
}