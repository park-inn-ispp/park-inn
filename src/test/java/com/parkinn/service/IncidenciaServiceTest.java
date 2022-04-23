package com.parkinn.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.parkinn.model.Estado;
import com.parkinn.model.Incidencia;
import com.parkinn.model.Reserva;
import com.parkinn.repository.IncidenciaRepository;
import com.parkinn.repository.ReservaRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class IncidenciaServiceTest {


	@Autowired
	private IncidenciaService incidenciaService;

	@MockBean
	private IncidenciaRepository incidenciaRepository;

	@MockBean
	private ReservaRepository reservaRepository;

	@Test
	@DisplayName("Test findByID Success")
	void testFindById() {
		Incidencia incidencia = new Incidencia(1l);
		doReturn(Optional.of(incidencia)).when(incidenciaRepository).findById(1l);

		Incidencia incidenciaReturn = incidenciaService.findIncidenciaById(1l);

		Assertions.assertNotNull(incidenciaReturn,"La plaza devuelta es null");
		Assertions.assertSame(incidencia,incidenciaReturn, "La plaza obtenida no es igual a la esperada");

	}

	@Test
	@DisplayName("Test findByID faiil")
	void testFindByIdFail() {

		doReturn(Optional.empty()).when(incidenciaRepository).findById(1l);

		Incidencia incidenciaReturn = incidenciaService.findIncidenciaById(1l);

		Assertions.assertNull(incidenciaReturn,"La plaza devuelta no es null");

	}

	
	@Test
	@DisplayName("Test findAll Success")
	void testFindAll() {
		Incidencia incidencia = new Incidencia(1l);
		Incidencia incidencia2 = new Incidencia(2l);

		doReturn(Arrays.asList(incidencia,incidencia2)).when(incidenciaRepository).findAll();

		List<Incidencia> incidenciaReturn = incidenciaService.findAll();

		Assertions.assertEquals(2, incidenciaReturn.size(),"El tamaño de la lista devuelta debe ser 2");

	}

	@Test
	@DisplayName("Test save incidencia")
	void testSave() {
		Incidencia incidencia = new Incidencia(1l);

		doReturn(incidencia).when(incidenciaRepository).save(any());

		Incidencia incidenciaReturn = incidenciaService.guardarIncidencia(incidencia);

		Assertions.assertNotNull(incidenciaReturn,"La plaza devuelta  es null");
		Assertions.assertEquals(1l, incidenciaReturn.getId(),"El id de la plaza debe ser 1");

	}

	@Test
	@DisplayName("Test comprobar confirmacion ambos success")
	void testComprobarConfirmacionSuccess() {
		Incidencia incidencia = new Incidencia(1l);
		Reserva reserva = new Reserva(1l);
		reserva.setEstado(Estado.confirmadaAmbos);	
		incidencia.setReserva(reserva);

		doReturn(Optional.of(reserva)).when(reservaRepository).findById(1l);

		boolean confirmado = incidenciaService.comprobarConfirmacion(incidencia);

		Assertions.assertTrue(confirmado,"La reserva no esta confirmada por ambos");

	}

	@Test
	@DisplayName("Test comprobar confirmacion ambos fail")
	void testComprobarNoConfirmacion() {
		Incidencia incidencia = new Incidencia(1l);
		Reserva reserva = new Reserva(1l);
		reserva.setEstado(Estado.confirmadaUsuario);	
		incidencia.setReserva(reserva);

		doReturn(Optional.of(reserva)).when(reservaRepository).findById(1l);

		boolean confirmado = incidenciaService.comprobarConfirmacion(incidencia);

		Assertions.assertFalse(confirmado,"La reserva si esta confirmada por ambos");

	}

	
	// @Test
	// @DisplayName("Test comprobar cliente incidencia success")
	// void testComprobarClienteIncidencia() {
	// 	Incidencia incidencia = new Incidencia(1l);
	// 	Client creaIncidencia = new Client(1l);
	// 	Client duenoPlaza = new Client(2l);
	// 	Plaza plaza = new Plaza(1l,duenoPlaza);
	// 	creaIncidencia.setEmail("email@prueba.com");
	// 	duenoPlaza.setEmail("dueno@plaza.com");
	// 	incidencia.setUser(creaIncidencia);
	// 	Reserva reserva = new Reserva(1l);
	// 	reserva.setPlaza(plaza);
	// 	//La incidencia la crea el cliente de la reserva
	// 	reserva.setUser(creaIncidencia);
	// 	incidencia.setReserva(reserva);

	// 	doReturn(Optional.of(reserva)).when(reservaRepository).findById(1l);
	// 	doReturn("email@prueba.com").when(SecurityContextHolder.getContext()).getAuthentication().getName();
	// 	boolean confirmado = incidenciaService.comprobarCliente(incidencia);

	// 	Assertions.assertTrue(confirmado,"La reserva si esta confirmada por ambos");

	// }

	// @Test
	// @DisplayName("Test comprobar cliente incidencia fail")
	// void testComprobarClienteIncidenciaNoCoincide() {
	// 	Incidencia incidencia = new Incidencia(1l);
	// 	Client creaIncidencia = new Client(1l);
	// 	Client duenoPlaza = new Client(2l);
	// 	Plaza plaza = new Plaza(1l,duenoPlaza);
	// 	creaIncidencia.setEmail("email@prueba.com");
	// 	duenoPlaza.setEmail("dueno@plaza.com");
	// 	incidencia.setUser(creaIncidencia);
	// 	Reserva reserva = new Reserva(1l);
	// 	reserva.setPlaza(plaza);
	// 	//La incidencia la crea el cliente de la reserva
	// 	reserva.setUser(creaIncidencia);
	// 	reserva.setEstado(Estado.confirmadaUsuario);	
	// 	incidencia.setReserva(reserva);

	// 	doReturn(Optional.of(reserva)).when(reservaRepository).findById(1l);

	// 	boolean confirmado = incidenciaService.comprobarCliente(incidencia);

	// 	Assertions.assertFalse(confirmado,"La reserva si esta confirmada por ambos");
 
	// }


	
}
