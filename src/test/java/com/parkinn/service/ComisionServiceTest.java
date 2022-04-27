package com.parkinn.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import com.parkinn.model.Comision;
import com.parkinn.repository.ComisionRepository;
import com.parkinn.repository.ReservaRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class ComisionServiceTest {


	@Autowired
	private ComisionService comisionService;

	@MockBean
	private ComisionRepository comisionRepository;

	@MockBean
	private ReservaRepository reservaRepository;

	@Test
	@DisplayName("Test findByID Success")
	void testFindById() {
		Comision comision = new Comision(1l);
		doReturn(Optional.of(comision)).when(comisionRepository).findById(1l);

		Comision comisionReturn = comisionService.findById(1l);

		Assertions.assertNotNull(comisionReturn,"La comision devuelta es null");
		Assertions.assertSame(comision,comisionReturn, "La comision obtenida no es igual a la esperada");

	}

	@Test
	@DisplayName("Test findByID faiil")
	void testFindByIdFail() {

		doReturn(Optional.empty()).when(comisionRepository).findById(1l);

		Comision comisionReturn = comisionService.findById(1l);

		Assertions.assertNull(comisionReturn,"La comision devuelta no es null");

	}

	

	@Test
	@DisplayName("Test save comision")
	void testSave() {
		Comision comision = new Comision(1l);

		doReturn(comision).when(comisionRepository).save(any());

		Comision comisionReturn = comisionService.save(comision);

		Assertions.assertNotNull(comisionReturn,"La comision devuelta  es null");
		Assertions.assertEquals(1l, comisionReturn.getId(),"El id de la comision debe ser 1");

	}


	
}
