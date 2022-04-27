package com.parkinn.web;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkinn.model.Comision;
import com.parkinn.service.ComisionService;
import com.parkinn.service.CustomUserDetailsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ComisionController.class)
public class ComisionControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
  	ObjectMapper objectMapper;
	  
	@MockBean
	CustomUserDetailsService customUserDetailsService;

	@MockBean
	ComisionService comisionService;

	@MockBean
	DataSource dataSource;

	Comision c1;
	Comision c3;

	@BeforeEach
	void setup() {
		c1 = new Comision(1l);
		c3 = new Comision(1l);
		c3.setPorcentaje(2);

		
		given(this.comisionService.findById(1l)).willReturn(c1);
		given(this.comisionService.findById(2l)).willReturn(null);
		given(this.comisionService.findById(3l)).willReturn(c3);


	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetComisionById() throws Exception {
		mockMvc.perform(get("/comision/1")).andExpect(status().isOk());
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetComisionByIdFail() throws Exception {
		mockMvc.perform(get("/comision/2")).andExpect(status().is4xxClientError());
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUpdateComision() throws Exception {
		mockMvc.perform(put("/comision/1/editar")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(c1))).andExpect(status().is2xxSuccessful());
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUpdateComisionFail() throws Exception {
		mockMvc.perform(put("/comision/3/editar")
		.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(c3))).andExpect(status().is4xxClientError());
	}


}