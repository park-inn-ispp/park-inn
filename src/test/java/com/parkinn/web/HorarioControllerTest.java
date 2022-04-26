package com.parkinn.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkinn.model.Client;
import com.parkinn.model.Comision;
import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.service.ComisionService;
import com.parkinn.service.CustomUserDetailsService;
import com.parkinn.service.HorarioService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = HorariosController.class)
public class HorarioControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
  	ObjectMapper objectMapper;
	  
	@MockBean
	CustomUserDetailsService customUserDetailsService;

	@MockBean
	HorarioService horarioService;

	@MockBean
	HorarioRepository horarioRepository;

	@MockBean
	DataSource dataSource;

	Horario h1;

	@BeforeEach
	void setup() {
		h1 = new Horario(1l);
		Client c1 = new Client(1l);
		c1.setEmail("prueba");
		Plaza plaza = new Plaza(1l, c1);
		h1.setPlaza(plaza);
		
		given(this.horarioService.findById(1l)).willReturn(h1);
		given(this.horarioService.guardarHorario(h1)).willReturn(h1);


	}

	

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUpdateHorario() throws Exception {
		mockMvc.perform(put("/horarios/1")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(h1))).andExpect(status().is2xxSuccessful());
	}

	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testUpdateHorarioFail() throws Exception {
		mockMvc.perform(put("/horarios/1")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(h1))).andExpect(status().is4xxClientError());
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testDeleteHorario() throws Exception {
		mockMvc.perform(delete("/horarios/1")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(h1))).andExpect(status().is2xxSuccessful());
	}

	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testDeleteHorarioFail() throws Exception {
		mockMvc.perform(delete("/horarios/1")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(h1))).andExpect(status().is4xxClientError());
	}


}