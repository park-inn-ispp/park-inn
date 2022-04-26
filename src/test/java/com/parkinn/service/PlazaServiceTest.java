package com.parkinn.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.parkinn.model.Client;
import com.parkinn.model.Plaza;
import com.parkinn.repository.PlazaRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest 
class PlazaServiceTest {


	@Autowired
	private PlazaService plazaService;

	@MockBean
	private PlazaRepository plazaRepository;

	@Test
	@DisplayName("Test findByID Success")
	void testFindById() {
		Plaza plaza = new Plaza(1l,new Client(1l));
		doReturn(Optional.of(plaza)).when(plazaRepository).findById(1l);

		Plaza plazaReturn = plazaService.findById(1l);

		Assertions.assertNotNull(plazaReturn,"La plaza devuelta es null");
		Assertions.assertSame(plaza,plazaReturn, "La plaza obtenida no es igual a la esperada");

	}

	@Test
	@DisplayName("Test findByID fail")
	void testFindByIdFail() {

		doReturn(Optional.empty()).when(plazaRepository).findById(1l);

		Plaza plazaReturn = plazaService.findById(1l);

		Assertions.assertNull(plazaReturn,"La plaza devuelta no es null");

	}

	
	@Test
	@DisplayName("Test findAll Succes")
	void testFindAllSuccess() {
		Plaza plaza = new Plaza(1l,new Client(1l));
		Plaza plaza2 = new Plaza(2l,new Client(1l));

		doReturn(Arrays.asList(plaza,plaza2)).when(plazaRepository).findAll();

		List<Plaza> plazaReturn = plazaService.findAll();

		Assertions.assertEquals(2, plazaReturn.size(),"El tamaño de la lista devuelta debe ser 2");

	}

	@Test
	@DisplayName("Test save plaza")
	void testSave() {
		Plaza plaza = new Plaza(1l, new Client(1l));

		doReturn(plaza).when(plazaRepository).save(any());

		Plaza plazaReturn = plazaService.guardarPlaza(plaza);

		Assertions.assertNotNull(plazaReturn,"La plaza devuelta  es null");
		Assertions.assertEquals(1l, plazaReturn.getId(),"El id de la plaza debe ser 1");

	}

	@Test
	@DisplayName("Test buscar plazas por usuario")
	void testFindByUserId() {
		Client client = new Client(1l);
		Plaza plaza = new Plaza(1l,client);
		Plaza plaza2 = new Plaza(2l,client);
		Plaza plaza3 = new Plaza(3l,client);


		doReturn(Arrays.asList(plaza,plaza2,plaza3)).when(plazaRepository).findByUserId(1l);

		List<Plaza> plazaReturn = plazaService.findUserById(1l);

		Assertions.assertEquals(3, plazaReturn.size(),"El tamaño de la lista devuelta debe ser 3");

	}

	@Test
	@DisplayName("Test buscar plazas por usuario fail")
	void testFindByUserIdFail() {

		doReturn(null).when(plazaRepository).findByUserId(2l);

		List<Plaza> plazaReturn = plazaService.findUserById(2l);

		Assertions.assertNull(plazaReturn,"El resultado devuelto debe ser null");

	}

	/* @Test
	@DisplayName("Test comprobar plazas iguales True")
	void testComprobarPlazasIgualesTrue() {
		Client client = new Client(1l);
		Plaza plaza = new Plaza(1l,client);

		doReturn(Arrays.asList(plaza)).when(plazaRepository).findByDireccionAndAdministrador("TestTrue", client);

		boolean existe = plazaService.comprobarPlazasIguales("TestTrue", client);

		Assertions.assertTrue(existe,"El resultado esperado es que SÍ existe la plaza");

	}

	@Test
	@DisplayName("Test comprobar plazas iguales False")
	void testComprobarPlazasIgualesFalse() {
		Client client = new Client(1l);
		
		doReturn(Arrays.asList()).when(plazaRepository).findByDireccionAndAdministrador("TestFalse", client);

		boolean existe = plazaService.comprobarPlazasIguales("TestFalse", client);

		Assertions.assertFalse(existe,"El resultado esperado es que NO existe la plaza");

	} */

}
