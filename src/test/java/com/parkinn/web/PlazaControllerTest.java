package com.parkinn.web;

import static java.util.Arrays.asList;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.sql.DataSource;

import com.parkinn.model.Client;
import com.parkinn.model.Plaza;
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
    private ReservaRepository reservasRepository;

	@MockBean
	MailService mailService;

	@MockBean
	DataSource dataSource;

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