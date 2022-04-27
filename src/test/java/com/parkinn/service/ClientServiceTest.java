package com.parkinn.service;

import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.parkinn.model.Client;
import com.parkinn.repository.ClientRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest 
public class ClientServiceTest {
    @Autowired
	private ClientService service;

	@MockBean
	private ClientRepository repository;

    @Test
    @DisplayName("Test listar todos los clientes")
    public void testFindAll(){
        Client c = new Client(1l);
        Client r2 = new Client(2l);

		doReturn(Arrays.asList(c,r2)).when(repository).findAll();

		List<Client> res = service.findAll();

		Assertions.assertEquals(2, res.size(),"El tamaño de la lista devuelta debe ser 2");
    }

    @Test
    @DisplayName("Test obtener cliente por id")
    public void testFindById(){
        Client c = new Client(1l);

		doReturn(Optional.of(c)).when(repository).findById(1L);

		Client res = service.findById(1L);

        Assertions.assertNotNull(res,"El cliente devuelto es null");
		Assertions.assertEquals(1L, res.getId(),"El id del cilente no es el esperado");
    }

    @Test
    @DisplayName("Test obtener cliente por email")
    public void testFindByEmail(){
        Client c = new Client(1l);
        c.setEmail("emailTest");

		doReturn(Optional.of(c)).when(repository).findByEmail("emailTest");

		Client res = service.findByEmail("emailTest");

        Assertions.assertNotNull(res,"El cliente devuelto es null");
		Assertions.assertEquals("emailTest", res.getEmail(),"El email del cilente no es el esperado");
    }

    @Test
    @DisplayName("Test obtener cliente por email o nombre")
    public void testFindByNameOrEmail(){
        Client c = new Client(1l);
        c.setName("nombreTest");

		doReturn(Optional.of(c)).when(repository).findByNameOrEmail("nombreTest","emailTest");

		Client res = service.findByNameOrEmail("nombreTest","emailTest");

        Assertions.assertNotNull(res,"El cliente devuelto es null");
		Assertions.assertEquals("nombreTest", res.getName(),"El nombre del cilente no es el esperado");
    }

    @Test
    @DisplayName("Test comprobar si existe por email")
    public void testExistsByEmail(){
        Client c = new Client(1l);
        c.setEmail("emailTest");

		doReturn(true).when(repository).existsByEmail("emailTest");

		Boolean res = service.existsByEmail("emailTest");

		Assertions.assertTrue(res,"El cliente no existe");
    }

    @Test
    @DisplayName("Test comprobar si existe por telefono")
    public void testExistsByPhone(){
        Client c = new Client(1l);
        c.setEmail("1212121");

		doReturn(true).when(repository).existsByPhone("1212121");

		Boolean res = service.existsByPhone("1212121");

		Assertions.assertTrue(res,"El cliente no existe");
    }

    @Test
    @DisplayName("Test guardar cliente")
    public void testSave(){
        Client c = new Client(1l);

		doReturn(c).when(repository).save(c);

		Client res = service.save(c);

		Assertions.assertEquals(c, res,"No se ha guardado el cliente");
    }

}
