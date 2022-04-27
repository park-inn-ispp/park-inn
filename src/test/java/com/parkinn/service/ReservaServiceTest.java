package com.parkinn.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;

import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.parkinn.model.Client;
import com.parkinn.model.Comision;
import com.parkinn.model.Estado;
import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.model.paypal.PayPalAccesToken;
import com.parkinn.model.paypal.PayPalClasses;
import com.parkinn.repository.ComisionRepository;
import com.parkinn.repository.HorarioRepository;
import com.parkinn.repository.ReservaRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootTest 
public class ReservaServiceTest{

    @Autowired
	private ReservaService reservaService;

	@MockBean
	private ReservaRepository reservaRepository;
    
    @MockBean
	private ComisionRepository comisionRepository;

    @MockBean
	private HorarioRepository horarioRepository;

    @MockBean
    RestTemplate restTemplate;

    @Test
    @DisplayName("Test listar todas las reserva")
    public void testFindAll(){
        Reserva r1 = new Reserva(1l);
        Reserva r2 = new Reserva(2l);

		doReturn(Arrays.asList(r1,r2)).when(reservaRepository).findAll();

		List<Reserva> reservaReturn = reservaService.findAll();

		Assertions.assertEquals(2, reservaReturn.size(),"El tamaño de la lista devuelta debe ser 2");
    }

    @Test
    @DisplayName("Test guardar reserva")
    public void testGuardarReserva(){
        Reserva r = new Reserva(1l);
        Comision c = new Comision();
        c.setPorcentaje(0.1f);

		doReturn(r).when(reservaRepository).save(any());
        doReturn(c).when(comisionRepository).getById(1L);

		Reserva reservaReturn = reservaService.guardarReserva(r);

		Assertions.assertNotNull(reservaReturn,"La reserva devuelta es null");
		Assertions.assertEquals(1l, reservaReturn.getId(),"El id de la reserva debe ser 1");
        Assertions.assertEquals(Estado.pendiente, reservaReturn.getEstado(),"El estado de la reserva debe ser pendiente");
    }

    @Test
    @DisplayName("Test aceptar reserva")
    public void testAceptarReserva(){
        Reserva r = new Reserva(1l);

		doReturn(Optional.of(r)).when(reservaRepository).findById(1L);
        doReturn(r).when(reservaRepository).save(r);

		Reserva reservaReturn = reservaService.aceptarReserva(r.getId());

		Assertions.assertNotNull(reservaReturn,"La reserva devuelta es null");
		Assertions.assertEquals(1l, reservaReturn.getId(),"El id de la reserva debe ser 1");
        Assertions.assertEquals(Estado.aceptada, reservaReturn.getEstado(),"El estado de la reserva debe ser aceptada");
    }

    @Test
    @DisplayName("Test rechazar reserva")
    public void testRechazarReserva(){
        Reserva r = new Reserva(1l);

		doReturn(Optional.of(r)).when(reservaRepository).findById(1L);
        doReturn(r).when(reservaRepository).save(r);

		Reserva reservaReturn = reservaService.rechazarReserva(r.getId());

		Assertions.assertNotNull(reservaReturn,"La reserva devuelta es null");
		Assertions.assertEquals(1l, reservaReturn.getId(),"El id de la reserva debe ser 1");
        Assertions.assertEquals(Estado.rechazada, reservaReturn.getEstado(),"El estado de la reserva debe ser rechazada");
    }

    @Test
    @DisplayName("Test devolver dinero sin fianza")
    public void testdevolverSinFianza(){
        Reserva r = new Reserva(1l);
        r.setFianza(1.0);
        r.setPrecioTotal(5.0);

        PayPalAccesToken paypal= new PayPalAccesToken();
        paypal.setAccessToken("AccesoTest");
        ResponseEntity<PayPalAccesToken> entity1 = ResponseEntity.ok().body(paypal);
        ResponseEntity<Object> entity2 = ResponseEntity.ok().build();

		doReturn(entity1).when(restTemplate).exchange(eq("https://api-m.sandbox.paypal.com/v1/oauth2/token?grant_type=client_credentials"),any(),any(),same(PayPalAccesToken.class));
        doReturn(entity2).when(restTemplate).exchange(eq("https://api-m.sandbox.paypal.com/v1/payments/payouts"),any(),any(),same(Object.class));

		ResponseEntity<Map<String, Object>> res = reservaService.devolverSinFianza(r);

		Assertions.assertEquals(HttpStatus.ACCEPTED, res.getStatusCode(),"La respuesta no es 200 OK");
        Assertions.assertEquals("Reserva cancelada con éxito. Se ha devuelto el importe total sin la fianza al cliente", res.getBody().get("info"),"No se ha cancelado la reserva");
    }

    @Test
    @DisplayName("Test devolver todo el dinero")
    public void testdevolverTodo(){
        Reserva r = new Reserva(1l);
        r.setPrecioTotal(5.0);

        PayPalAccesToken paypal= new PayPalAccesToken();
        paypal.setAccessToken("AccesoTest");
        ResponseEntity<PayPalAccesToken> entity1 = ResponseEntity.ok().body(paypal);
        ResponseEntity<Object> entity2 = ResponseEntity.ok().build();

		doReturn(entity1).when(restTemplate).exchange(eq("https://api-m.sandbox.paypal.com/v1/oauth2/token?grant_type=client_credentials"),any(),any(),same(PayPalAccesToken.class));
        doReturn(entity2).when(restTemplate).exchange(eq("https://api-m.sandbox.paypal.com/v1/payments/payouts"),any(),any(),same(Object.class));

		ResponseEntity<Map<String, Object>> res = reservaService.devolverTodo(r);

		Assertions.assertEquals(HttpStatus.ACCEPTED, res.getStatusCode(),"La respuesta no es 200 OK");
        Assertions.assertEquals("Reserva cancelada con éxito. Se ha devuelto el importe total al cliente", res.getBody().get("info"),"No se ha cancelado la reserva");
    }

    @Test
    @DisplayName("Test buscar reserva por id de la plaza")
    public void testFindByPlazaId(){
        Plaza p = new Plaza(1l, null);
		Reserva r1 = new Reserva(1l);
		Reserva r2 = new Reserva(2l);
		Reserva r3 = new Reserva(3l);
        r1.setPlaza(p);
        r2.setPlaza(p);
        r3.setPlaza(p);

		doReturn(Arrays.asList(r1,r2,r3)).when(reservaRepository).findByPlazaId(1l);

		List<Reserva> reservaReturn = reservaService.findByPlazaId(1l);

		Assertions.assertEquals(3, reservaReturn.size(),"El tamaño de la lista devuelta debe ser 3");
    }

    @Test
    @DisplayName("Test buscar reserva por id del usuario")
    public void testFindByUserId(){
        Client client = new Client(1l);
		Reserva r1 = new Reserva(1l);
		Reserva r2 = new Reserva(2l);
		Reserva r3 = new Reserva(3l);
        r1.setUser(client);
        r2.setUser(client);
        r3.setUser(client);

		doReturn(Arrays.asList(r1,r2,r3)).when(reservaRepository).findByUserId(1l);

		List<Reserva> reservaReturn = reservaService.findByUserId(1l);

		Assertions.assertEquals(3, reservaReturn.size(),"El tamaño de la lista devuelta debe ser 3");

    }

    @Test
    @DisplayName("Test buscar reserva por id")
    public void testFindById(){
		Reserva r = new Reserva(1l);

		doReturn(Optional.of(r)).when(reservaRepository).findById(1l);

		Reserva reservaReturn = reservaService.findById(1l);

		Assertions.assertNotNull(reservaReturn,"La reserva devuelta es null");
		Assertions.assertSame(r,reservaReturn, "La reserva obtenida no es igual a la esperada");
    }

    @Test
    @DisplayName("Test horarios no disponibles de una plaza")
    public void testHorariosNoDisponibles(){
        Plaza p = new Plaza(1l, null);
		Reserva r1 = new Reserva(1l);
		Reserva r2 = new Reserva(2l);
        r1.setFechaInicio(LocalDateTime.of(2022, 9, 10, 16, 0, 0));
        r1.setFechaFin(LocalDateTime.of(2022, 9, 10, 20, 0, 0));
        r2.setFechaInicio(LocalDateTime.of(2022, 9, 27, 0, 0, 0));
        r2.setFechaFin(LocalDateTime.of(2022, 9, 28, 0, 0, 0));
        r1.setPlaza(p);
        r2.setPlaza(p);

		doReturn(Arrays.asList(r1,r2)).when(reservaRepository).findByPlazaId(1l);

		List<List<LocalDateTime>> res = reservaService.horariosNoDisponibles(1l,false);

		Assertions.assertSame(3,res.size(), "No hay el número de horarios no disponibles que se esperaban");
    }

    @Test
    @DisplayName("Test reserva tiene colisión")
    public void testReservaTieneColision(){
        Plaza p = new Plaza(1l, null);
		Reserva r1 = new Reserva(1l);
		Reserva r2 = new Reserva(2l);
        Reserva r3 = new Reserva(3l);
        r1.setFechaInicio(LocalDateTime.of(2022, 9, 10, 16, 0, 0));
        r1.setFechaFin(LocalDateTime.of(2022, 9, 10, 20, 0, 0));
        r2.setFechaInicio(LocalDateTime.of(2022, 9, 27, 0, 0, 0));
        r2.setFechaFin(LocalDateTime.of(2022, 9, 28, 0, 0, 0));
        r1.setPlaza(p);
        r2.setPlaza(p);
        
        r3.setFechaInicio(LocalDateTime.of(2022, 9, 12, 12, 0, 0));
        r3.setFechaFin(LocalDateTime.of(2022, 9, 12, 14, 30, 0));
        r3.setPlaza(p);

		doReturn(Arrays.asList(r1,r2)).when(reservaRepository).findByPlazaId(1l);

		Boolean res = reservaService.reservaTieneColision(r3);

		Assertions.assertFalse(res, "La reserva tiene colisión");
    }

    @Test
    @DisplayName("Test validar reserva antes del pago con éxito")
    public void testErroresNuevaReservaAntesDelPagoSuccess(){
        Plaza p = new Plaza(1l, null);
		Reserva r1 = new Reserva(1l);
		Reserva r2 = new Reserva(2l);
        r1.setFechaInicio(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 10, 16, 0, 0));
        r1.setFechaFin(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 10, 20, 0, 0));
        r2.setFechaInicio(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 27, 0, 0, 0));
        r2.setFechaFin(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 28, 0, 0, 0));
        r1.setPlaza(p);
        r2.setPlaza(p);

        doReturn(Arrays.asList(r2)).when(reservaRepository).findByPlazaId(1l);

		List<String> res = reservaService.erroresNuevaReservaAntesDelPago(r1);

		Assertions.assertEquals(0, res.size(),"La reserva tiene errores");
    }

    @Test
    @DisplayName("Test validar reserva antes del pago sin éxito")
    public void testErroresNuevaReservaAntesDelPagoFail(){
		Reserva r1 = new Reserva(1l);
        r1.setFechaInicio(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 10, 20, 0, 0));
        r1.setFechaFin(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 10, 16, 0, 0));

		List<String> res = reservaService.erroresNuevaReservaAntesDelPago(r1);

		Assertions.assertEquals("La fecha de inicio debe ser anterior a la fecha de fin", res.get(0),"La reserva no tiene error en la fecha");
    }

    @Test
    @DisplayName("Test validar reserva antes del pago sin éxito por fecha en pasado")
    public void testErroresNuevaReservaAntesDelPagoFailPasado(){
		Reserva r1 = new Reserva(1l);
        r1.setFechaInicio(LocalDateTime.of(LocalDateTime.now().getYear()-1, 1, 10, 16, 0, 0));
        r1.setFechaFin(LocalDateTime.of(LocalDateTime.now().getYear()-1, 1, 10, 20, 0, 0));

		List<String> res = reservaService.erroresNuevaReservaAntesDelPago(r1);

		Assertions.assertEquals("No se pueden realizar reservas en el pasado", res.get(0),"La reserva no tiene error en la fecha");
    }

    @Test
    @DisplayName("Test validar reserva antes del pago sin éxito por colisión")
    public void testErroresNuevaReservaAntesDelPagoFailColision(){
        Plaza p = new Plaza(1l, null);
		Horario h = new Horario();
		Reserva r1 = new Reserva(1l);
        r1.setFechaInicio(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 10, 16, 0, 0));
        r1.setFechaFin(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 12, 20, 0, 0));
        h.setFechaInicio(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 9, 0, 0, 0));
        h.setFechaFin(LocalDateTime.of(LocalDateTime.now().getYear()+1, 9, 15, 0, 0, 0));
        r1.setPlaza(p);

        doReturn(Arrays.asList(h)).when(horarioRepository).findHorariosByPlazaId(1l);

		List<String> res = reservaService.erroresNuevaReservaAntesDelPago(r1);

        Assertions.assertEquals(1, res.size(),"La reserva no erorres");
		Assertions.assertEquals("Este horario no está disponible o está ocupado por otra reserva", res.get(0),"La reserva no tiene error de colisión");
    }

    @Test
    @DisplayName("Test confirmar servicio siendo usuario")
    public void testConfirmarServicioPorUsuario(){
        Client user = new Client(1L);
        Client prop = new Client(2L);
        user.setEmail("emailUserTest");
        prop.setEmail("emailPropietarioTest");
        Plaza p = new Plaza(1l, prop);
        Reserva r = new Reserva(1l);
        r.setUser(user);
        r.setEstado(Estado.aceptada);
        r.setPlaza(p);

        doReturn(r).when(reservaRepository).save(r);

        Object res = reservaService.confirmarServicio(r,"emailUserTest","emailPropietarioTest");
    
        if(res instanceof Reserva){
            Reserva reservaReturn = (Reserva) res;
            Assertions.assertNotNull(reservaReturn,"La reserva devuelta es null");
            Assertions.assertEquals(Estado.confirmadaUsuario, reservaReturn.getEstado(),"El estado de la reserva debe ser confirmadaUsuario");
        }else{
            Assertions.fail("No se devuelve un objeto Reserva");
        }
    }

    @Test
    @DisplayName("Test confirmar servicio siendo el propietario")
    public void testConfirmarServicioPorPropietario(){
        Client user = new Client(1L);
        Client prop = new Client(2L);
        user.setEmail("emailUserTest");
        prop.setEmail("emailPropietarioTest");
        Plaza p = new Plaza(1l, prop);
        Reserva r = new Reserva(1l);
        r.setUser(user);
        r.setEstado(Estado.aceptada);
        r.setPlaza(p);

        doReturn(r).when(reservaRepository).save(r);

        Object res = reservaService.confirmarServicio(r,"emailPropietarioTest","emailPropietarioTest");
    
        Assertions.assertNotNull(res,"La reserva devuelta es null");
        if(res instanceof Reserva){
            Reserva reservaReturn = (Reserva) res;
            Assertions.assertEquals(Estado.confirmadaPropietario, reservaReturn.getEstado(),"El estado de la reserva debe ser confirmadaPropietario");
        }else{
            Assertions.fail("No se devuelve un objeto Reserva");
        }
    }

    @Test
    @DisplayName("Test confirmar servicio por ambas partes")
    public void testConfirmarServicioAmbos(){
        Client user = new Client(1L);
        Client prop = new Client(2L);
        user.setEmail("emailUserTest");
        prop.setEmail("emailPropietarioTest");
        Plaza p = new Plaza(1l, prop);
        Reserva r = new Reserva(1l);
        r.setFianza(1.0);
        r.setPrecioTotal(5.0);
        r.setUser(user);
        r.setEstado(Estado.confirmadaUsuario);
        r.setPlaza(p);
        r.setComision(0.1f);
        PayPalAccesToken paypal= new PayPalAccesToken();
        paypal.setAccessToken("AccesoTest");
        ResponseEntity<PayPalAccesToken> entity1 = ResponseEntity.ok().body(paypal);
        ResponseEntity<Object> entity2 = ResponseEntity.ok().build();

		doReturn(entity1).when(restTemplate).exchange(eq("https://api-m.sandbox.paypal.com/v1/oauth2/token?grant_type=client_credentials"),any(),any(),same(PayPalAccesToken.class));
        doReturn(entity2).when(restTemplate).exchange(eq("https://api-m.sandbox.paypal.com/v1/payments/payouts"),any(),any(),same(Object.class));
        doReturn(r).when(reservaRepository).save(r);

        Object res = reservaService.confirmarServicio(r,"emailPropietarioTest","emailPropietarioTest");
    
        Assertions.assertNotNull(res,"La reserva devuelta es null");
        if(res instanceof Reserva){
            Reserva reservaReturn = (Reserva) res;
            Assertions.assertEquals(Estado.confirmadaAmbos, reservaReturn.getEstado(),"El estado de la reserva debe ser confirmadaAmbos");
        }else{
            Assertions.fail("No se devuelve un objeto Reserva");
        }
    }

    @Test
    @DisplayName("Test denegar servicio")
    public void testDenegarServicio(){
        Reserva r = new Reserva(1l);

		doReturn(Optional.of(r)).when(reservaRepository).findById(1L);
        doReturn(r).when(reservaRepository).save(r);

		Reserva reservaReturn = reservaService.denegarServicio(r);

		Assertions.assertNotNull(reservaReturn,"La reserva devuelta es null");
		Assertions.assertEquals(1l, reservaReturn.getId(),"El id de la reserva debe ser 1");
        Assertions.assertEquals(Estado.denegada, reservaReturn.getEstado(),"El estado de la reserva debe ser denegada");
    }

    @Test
    @DisplayName("Test buscar reserva por id del usuario")
    public void testGetPayPal(){
        PayPalAccesToken paypal= new PayPalAccesToken();
        paypal.setAccessToken("AccesoTest");
        PayPalClasses palClasses = new PayPalClasses();
        ResponseEntity<PayPalAccesToken> entity1 = ResponseEntity.ok().body(paypal);
        ResponseEntity<PayPalClasses> entity2 = ResponseEntity.ok().body(palClasses);

		doReturn(entity1).when(restTemplate).exchange(eq("https://api-m.sandbox.paypal.com/v1/oauth2/token?grant_type=client_credentials"),any(),any(),same(PayPalAccesToken.class));
        doReturn(entity2).when(restTemplate).exchange(eq("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + "queryTest"),any(),any(),same(PayPalClasses.class));

        try {
            PayPalClasses res = reservaService.getPayPal("queryTest");
            Assertions.assertNotNull(res,"La reserva devuelta es null");
        } catch (URISyntaxException e) {
            Assertions.fail("Error con la URI de la petición a paypal");
        }
    }
}