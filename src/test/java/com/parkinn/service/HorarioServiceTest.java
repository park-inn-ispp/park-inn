package com.parkinn.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import java.util.Optional;

import com.parkinn.model.Horario;
import com.parkinn.repository.HorarioRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class HorarioServiceTest {


	@Autowired
	private HorarioService horarioService;

	@MockBean
	private HorarioRepository horarioRepository;


	@Test
	@DisplayName("Test findByID Success")
	void testFindById() {
		Horario horario = new Horario(1l);
		doReturn(Optional.of(horario)).when(horarioRepository).findById(1l);

		Horario horarioReturn = horarioService.findById(1l);

		Assertions.assertNotNull(horarioReturn,"El horario devuelto es null");
		Assertions.assertSame(horario,horarioReturn, "El horario obtenida no es igual a la esperada");

	}

	@Test
	@DisplayName("Test findByID fail")
	void testFindByIdFail() {

		doReturn(Optional.empty()).when(horarioRepository).findById(1l);

		Horario horarioReturn = horarioService.findById(1l);

		Assertions.assertNull(horarioReturn,"El horario devuelto no es null");

	}

	

	@Test
	@DisplayName("Test save horario")
	void testSave() {
		Horario horario = new Horario(1l);

		doReturn(horario).when(horarioRepository).save(any());

		Horario horarioReturn = horarioService.guardarHorario(horario);

		Assertions.assertNotNull(horarioReturn,"El horario devuelto  es null");
		Assertions.assertEquals(1l, horarioReturn.getId(),"El id del horario debe ser 1");

	}

	@Test
	@DisplayName("Test delete horario")
	void testDelete() {
		
		doNothing().when(horarioRepository).deleteById(1l);

		horarioService.deleteById(1l);

		Mockito.verify(horarioRepository, Mockito.times(1)).deleteById(1l);
	}

	
}
