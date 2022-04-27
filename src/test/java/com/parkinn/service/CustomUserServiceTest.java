package com.parkinn.service;

import static org.mockito.Mockito.doReturn;

import java.util.Set;

import com.parkinn.model.Client;
import com.parkinn.model.Role;
import com.parkinn.repository.ClientRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;


@SpringBootTest
class CustomUserServiceTest {


	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@MockBean
	private ClientService clientService;

	@MockBean
	private ClientRepository clientRepository;


	@Test
	@DisplayName("Test findByUsername Success")
	void testFindByUsername() {
		Client client = new Client(1l);
		client.setEmail("prueba@prueba.com");
		client.setName("prueba@prueba.com");
		client.setPassword("prueba");
		Role rol = new Role();
		rol.setId(1l);
		rol.setName("ROLE_ADMIN");
		client.setRoles(Set.of(rol));
		doReturn(client).when(clientService).findByNameOrEmail("prueba@prueba.com","prueba@prueba.com");

		UserDetails userDetailsReturn = customUserDetailsService.loadUserByUsername("prueba@prueba.com");

		Assertions.assertNotNull(userDetailsReturn,"El User Details devuelto es null");
		Assertions.assertSame(userDetailsReturn.getUsername(),client.getEmail(), "El UserDetails obtenida no es igual a la esperada");

	}



	
	
}
