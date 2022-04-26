package com.parkinn.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkinn.model.Client;
import com.parkinn.model.Role;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.PlazaRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.repository.RoleRepository;
import com.parkinn.service.ClientService;
import com.parkinn.service.CustomUserDetailsService;
import com.parkinn.service.MailService;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static java.util.Arrays.asList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import java.util.Set;

import javax.sql.DataSource;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ClientsController.class)
public class ClientsControllerTest {

	@MockBean
	ClientService service;

	@MockBean
    org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;
    
	@MockBean
    PlazaRepository plazaRepository;
    
	@MockBean
    RoleRepository roleRepository;
    
	@MockBean
    HorarioRepository horarioRepository;
    
	@MockBean
    ReservaRepository reservasRepository;

	@MockBean
	MailService mailService;

	@MockBean
	CustomUserDetailsService customUserDetailsService;

	@MockBean
	DataSource dataSource;
	
	@Autowired
	MockMvc mockMvc;

	@Autowired
  	private ObjectMapper objectMapper;

	Client c1;
	Client c2;
	Client c3;
	Role rU;
	Role rA;

	@BeforeEach
	void setup() {
		c1 = new Client(1l);
		c1.setName("nombreC1");
		c1.setEmail("emailC1");
		c2 = new Client(2l);
		c3 = new Client(3l);
		rU = new Role();
		rU.setName("ROLE_USER");
		rA = new Role();
		rA.setName("ROLE_ADMIN");
		c3.setRoles(Set.of(rU));
		given(this.service.findAll()).willReturn(asList(c1,c2));
		given(this.service.findById(c1.getId())).willReturn(c1);
		given(this.service.findById(c2.getId())).willReturn(c2);
		given(this.service.findById(c3.getId())).willReturn(c3);
		given(this.service.findByEmail(c1.getEmail())).willReturn(c1);
		given(this.service.save(c1)).willReturn(c1);
		given(this.service.save(c2)).willReturn(c2);
		given(this.service.save(c3)).willReturn(c3);
		given(this.roleRepository.findByName(rU.getName())).willReturn(Optional.of(rU));
		given(this.roleRepository.findByName(rA.getName())).willReturn(Optional.of(rA));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetClients() throws Exception {
		mockMvc.perform(get("/clients")).andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetClient() throws Exception {
		mockMvc.perform(get("/clients/{id}",1)).andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("nombreC1"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetByEmail() throws Exception {
		mockMvc.perform(get("/clients/usuariopormail/{email}","emailC1")).andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("nombreC1"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testCreateClient() throws Exception {
		mockMvc.perform(post("/clients").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(c1)))
			.andExpect(status().isCreated()).andExpect(jsonPath("$.name").value("nombreC1"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUpdateClient() throws Exception {
		c2.setEmail("emailUpdate");
		mockMvc.perform(put("/clients/{id}/edit",c2.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(c2)))
			.andExpect(status().isOk()).andExpect(jsonPath("$.email").value(c2.getEmail()));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testConsultarPerfil() throws Exception {
		mockMvc.perform(get("/clients/{id}/perfil",1)).andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("nombreC1"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testBanClient() throws Exception {
		c3.setRoles(Set.of(rU));
		mockMvc.perform(put("/clients/{id}/banear",c3.getId())).andExpect(status().isOk())
			.andExpect(jsonPath("$.roles", Matchers.nullValue()));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUnbanClient() throws Exception {
		c3.setRoles(null);
		mockMvc.perform(put("/clients/{id}/desbanear",c3.getId())).andExpect(status().isOk())
			.andExpect(jsonPath("$.roles").value(Set.of(rU)));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testDeleteClient() throws Exception {
		mockMvc.perform(delete("/clients/{id}",c2.getId())).andExpect(status().isOk());
	}
}