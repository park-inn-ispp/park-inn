package com.parkinn.web;

import com.parkinn.configuration.SecurityConfiguration;
import com.parkinn.model.Client;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.PlazaRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.repository.RoleRepository;
import com.parkinn.service.ClientService;
import com.parkinn.service.CustomUserDetailsService;
import com.parkinn.service.MailService;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static java.util.Arrays.asList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.sql.DataSource;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.FilterType;

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

	@BeforeEach
	void setup() {
		Client c1 = new Client(1l);
		Client c2 = new Client(2l);
		given(this.service.findAll()).willReturn(asList(c1,c2));
	}

	// @WithMockUser(value = "spring")
    // @Test
	// void testGetClients() throws Exception {
	// 	mockMvc.perform(get("/clients")).andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.size()", Matchers.is(2)));
	// }
}