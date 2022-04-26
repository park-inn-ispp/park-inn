package com.parkinn.web;

import com.parkinn.configuration.SecurityConfiguration;
import com.parkinn.model.Client;
import com.parkinn.model.Plaza;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.PlazaRepository;
import com.parkinn.repository.ReservaRepository;
import com.parkinn.repository.RoleRepository;
import com.parkinn.service.ClientService;
import com.parkinn.service.CustomUserDetailsService;
import com.parkinn.service.HorarioService;
import com.parkinn.service.MailService;
import com.parkinn.service.PlazaService;
import com.parkinn.service.ReservaService;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.FilterType;

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
    private ReservaRepository reservasRepository;

	@MockBean
	MailService mailService;

	@MockBean
	DataSource dataSource;


	// @MockBean
    // private AuthenticationManagerResolver auth;
	

	/* @MockBean
    private CustomUserDetailsService UserDetailsService;

	@MockBean
	DataSource dataSource;
 */

	@BeforeEach
	void setup() {
		Client c1 = new Client(1l);
		Plaza p1 = new Plaza(1l,c1);
		Plaza p2 = new Plaza(2l,c1);
		given(this.plazaService.findAll()).willReturn(asList(p1,p2));
	}

	@WithMockUser(value = "spring")
    @Test
	void testGetPlazas() throws Exception {
		mockMvc.perform(get("/plazas/all")).andExpect(status().isOk())
			.andExpect(jsonPath("$.size()", Matchers.is(2)));
	}

}