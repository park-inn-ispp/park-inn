package com.parkinn.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkinn.model.Client;
import com.parkinn.model.Role;
import com.parkinn.repository.ClientRepository;
import com.parkinn.repository.ComisionRepository;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.PlazaRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.repository.RoleRepository;
import com.parkinn.service.ClientService;
import com.parkinn.service.CustomUserDetailsService;
import com.parkinn.service.MailService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static java.util.Arrays.asList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.sql.DataSource;

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
    ClientRepository clientRepository;

	@MockBean
    RoleRepository roleRepository;
    
	@MockBean
    HorarioRepository horarioRepository;
    
	@MockBean
    ReservaRepository reservasRepository;

	@MockBean
    ComisionRepository comisionRepository;

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
	Client c4;
	Role rU;
	Role rA;
	Role rB;

	@BeforeEach
	void setup() {
		c1 = new Client(1l);
		c1.setName("nombreC1");
		c1.setEmail("emailC1");
		c2 = new Client(2l);
		c3 = new Client(3l);
		c4 = new Client(4l);
		rU = new Role();
		rU.setName("ROLE_USER");
		rA = new Role();
		rA.setName("ROLE_ADMIN");
		rB = new Role();
		rB.setName("ROLE_BANNED");
		Set<Role> roles = new HashSet<>();
		roles.add(rU);
		c3.setRoles(roles);
		roles = new HashSet<>();
		roles.add(rB);
		c4.setRoles(roles);

		given(this.clientRepository.findAll()).willReturn(asList(c1,c2));
		given(this.clientRepository.findById(c1.getId())).willReturn(Optional.of(c1));
		given(this.clientRepository.findById(c1.getId())).willReturn(Optional.of(c1));
		given(this.clientRepository.findById(c2.getId())).willReturn(Optional.of(c2));
		given(this.clientRepository.findById(c3.getId())).willReturn(Optional.of(c3));
		given(this.clientRepository.findById(c4.getId())).willReturn(Optional.of(c4));
		given(this.clientRepository.findByEmail(c1.getEmail())).willReturn(Optional.of(c1));
		given(this.clientRepository.save(c1)).willReturn(c1);
		given(this.clientRepository.save(c2)).willReturn(c2);
		given(this.clientRepository.save(c3)).willReturn(c3);
		given(this.clientRepository.save(c4)).willReturn(c4);
		given(this.roleRepository.findByName(rU.getName())).willReturn(Optional.of(rU));
		given(this.roleRepository.findByName(rA.getName())).willReturn(Optional.of(rA));
		given(this.roleRepository.findByName(rB.getName())).willReturn(Optional.of(rB));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetClients() throws Exception {
		mockMvc.perform(get("/clients")).andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(2));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetClientsEmpty() throws Exception {
		given(this.clientRepository.findAll()).willReturn(asList());
		mockMvc.perform(get("/clients")).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("No existen usuarios en la base de datos"));
	}

	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testGetClientsDenied() throws Exception {
		mockMvc.perform(get("/clients")).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("No tienes acceso"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetClient() throws Exception {
		mockMvc.perform(get("/clients/{id}",1)).andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("nombreC1"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetClientNotFound() throws Exception {
		given(this.clientRepository.findById(c1.getId())).willReturn(Optional.empty());
		mockMvc.perform(get("/clients/{id}", c1.getId())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("Este usuario no existe"));
	}

	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testGetClientDenied() throws Exception {
		mockMvc.perform(get("/clients/{id}", c1.getId())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("Solo puedes acceder a tus datos"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetByEmail() throws Exception {
		mockMvc.perform(get("/clients/usuariopormail/{email}",c1.getEmail())).andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("nombreC1"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testGetByEmailNotFound() throws Exception {
		given(this.clientRepository.findByEmail(c1.getEmail())).willReturn(Optional.empty());
		mockMvc.perform(get("/clients/usuariopormail/{email}", c1.getEmail())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("Este usuario no existe"));
	}

	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testGetByEmailDenied() throws Exception {
		mockMvc.perform(get("/clients/usuariopormail/{email}", c1.getEmail())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("No tienes acceso"));
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
	void testUpdateClientNotFound() throws Exception {
		c2.setEmail("emailUpdate");
		given(this.clientRepository.findById(c2.getId())).willReturn(Optional.empty());
		mockMvc.perform(put("/clients/{id}/edit",c2.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(c2))).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("Este usuario no existe"));
	}

	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testUpdateClientDenied() throws Exception {
		c2.setEmail("emailUpdate");
		mockMvc.perform(put("/clients/{id}/edit",c2.getId()).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(c2))).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("Solo puedes editar los datos de tu perfil"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testConsultarPerfil() throws Exception {
		mockMvc.perform(get("/clients/{id}/perfil",1)).andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("nombreC1"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testConsultarPerfilNotFound() throws Exception {
		given(this.clientRepository.findById(c1.getId())).willReturn(Optional.empty());
		mockMvc.perform(get("/clients/{id}/perfil", c1.getId())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("Este usuario no existe"));
	}

	@WithMockUser(authorities  = "ROLE_USER")
    @Test
	void testConsultarPerfilDenied() throws Exception {
		mockMvc.perform(get("/clients/{id}/perfil", c1.getId())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("No tienes acceso a este perfil"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testBanClient() throws Exception {
		mockMvc.perform(put("/clients/{id}/banear",c3.getId())).andExpect(status().isOk())
			.andExpect(jsonPath("$.roles[0].name").value(rB.getName()));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testBanClientNotFound() throws Exception {
		given(this.clientRepository.findById(c3.getId())).willReturn(Optional.empty());
		mockMvc.perform(put("/clients/{id}/banear",c3.getId())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("No puedes banear a un usuario que no existe"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUnbanClient() throws Exception {
		mockMvc.perform(put("/clients/{id}/desbanear",c4.getId())).andExpect(status().isOk())
			.andExpect(jsonPath("$.roles[0].name").value(rU.getName()));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testUnbanClientNotFound() throws Exception {
		given(this.clientRepository.findById(c4.getId())).willReturn(Optional.empty());
		mockMvc.perform(put("/clients/{id}/desbanear",c4.getId())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("No puedes desbanear a un usuario que no existe"));
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testDeleteClient() throws Exception {
		mockMvc.perform(delete("/clients/{id}",c2.getId())).andExpect(status().isOk());
	}

	@WithMockUser(authorities  = "ROLE_ADMIN")
    @Test
	void testDeleteClientNotFound() throws Exception {
		given(this.clientRepository.findById(c2.getId())).willReturn(Optional.empty());
		mockMvc.perform(delete("/clients/{id}",c2.getId())).andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errores[0]").value("No puedes eliminar a un usuario que no existe"));
	}
}