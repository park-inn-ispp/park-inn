package com.parkinn.web;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkinn.model.Client;
import com.parkinn.model.Estado;
import com.parkinn.model.Incidencia;
import com.parkinn.model.Reserva;
import com.parkinn.service.CustomUserDetailsService;
import com.parkinn.service.IncidenciaService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = IncidenciaController.class)
public class IncidenciaControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
  	ObjectMapper objectMapper;
	  
	@MockBean
	CustomUserDetailsService customUserDetailsService;

	@MockBean
	IncidenciaService incidenciaService;

	@MockBean
	DataSource dataSource;
	
	Incidencia i1;
	Incidencia i3;

	@BeforeEach
	void setup() {
		 i1 = new Incidencia(1l);
		Incidencia i2 = new Incidencia(2l);
		i3 =new Incidencia(3l);

		Client c1 = new Client(1l);
		Client c2 = new Client(2l);

		c1.setEmail("test@test.com");
		i1.setUser(c1);
		Reserva r1 = new Reserva(1l);
		r1.setEstado(Estado.confirmadaPropietario);
		i1.setIdReserva(r1);
		
		given(this.incidenciaService.findAll()).willReturn(asList(i1,i2));
		given(this.incidenciaService.findIncidenciaById(1l)).willReturn(i1);
		given(this.incidenciaService.findIncidenciaById(2l)).willReturn(null);
		given(this.incidenciaService.comprobarCliente(i1)).willReturn(true);
		given(this.incidenciaService.guardarIncidencia(any())).willReturn(i1);
		given(this.incidenciaService.comprobarConfirmacion(i1)).willReturn(false);
		given(this.incidenciaService.comprobarConfirmacion(i1)).willReturn(false);

		i3.setUser(c2);
		i3.setIdReserva(r1);
		i3.getUser().setEmail(null);



	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetIncidencias() throws Exception {
		mockMvc.perform(get("/incidencias/all")).andExpect(status().isOk())
			.andExpect(jsonPath("$.size()", Matchers.is(2)));
	}

	@WithMockUser(value = "spring")
    @Test
	void testGetIncidenciaById() throws Exception {
		mockMvc.perform(get("/incidencias/1")).andExpect(status().isOk())
			.andExpect(jsonPath("$.size()", Matchers.is(1)));
	}

	@WithMockUser(value = "spring")
    @Test
	void testGetIncidenciaByIdFail() throws Exception {
		mockMvc.perform(get("/incidencias/2")).andExpect(status().is4xxClientError());
	}

	@WithMockUser(value = "spring")
    @Test
	void testCreateIncidencia() throws Exception {
		mockMvc.perform(post("/incidencias")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(i1))).andExpect(status().is2xxSuccessful())
		.andExpect(jsonPath("$.id").value(1l));
	}

	
	@WithMockUser(value = "spring")
    @Test
	void testCreateIncidenciaFail() throws Exception {
		mockMvc.perform(post("/incidencias")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(i3))).andExpect(status().is4xxClientError());
	}


	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCerrarIncidencia() throws Exception {
		mockMvc.perform(put("/incidencias/1")).andExpect(status().isOk());
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCerrarIncidenciaFail() throws Exception {
		mockMvc.perform(put("/incidencias/2")).andExpect(status().is4xxClientError());
	}

}