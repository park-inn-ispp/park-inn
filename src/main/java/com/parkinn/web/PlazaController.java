package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;


import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;
import com.parkinn.service.PlazaService;
import com.parkinn.service.ReservaService;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/plazas")
public class PlazaController {

    @Autowired
    private PlazaService plazaService;
    @Autowired
    private ReservaService reservaService;


    @GetMapping()
    public List<Plaza> filtrarPlazas(@RequestParam(name = "maxPrecioHora", required=false) Double maxPrecioHora, @RequestParam(name = "fechaInicio", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
        @RequestParam(name = "fechaFin", required=false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin, @RequestParam(name = "zona", required=false) String zona) {
        return plazaService.filtrarPlazas(maxPrecioHora, fechaInicio, fechaFin, zona);
    }

    @GetMapping("/all")
    public List<Plaza> getPlazas() {
        return plazaService.findAll();
    }

    @PostMapping
    public ResponseEntity createPlaza(@RequestBody Plaza plaza) throws URISyntaxException {
        Plaza savedPlaza = plazaService.guardarPlaza(plaza);
        return ResponseEntity.created(new URI("/plazas/" + savedPlaza.getId())).body(savedPlaza);
    }

    @PutMapping("/{id}")
    public ResponseEntity updatePlaza(@PathVariable Long id, @RequestBody Plaza plaza) {
        Plaza currentPlaza = plazaService.findById(id);
        currentPlaza.setDireccion(plaza.getDireccion());
        currentPlaza.setDescripcion(plaza.getDescripcion());
        currentPlaza.setAncho(plaza.getAncho());
        currentPlaza.setLargo(plaza.getLargo());
        currentPlaza.setEstaDisponible(plaza.getEstaDisponible());
        currentPlaza.setEsAireLibre(plaza.getEsAireLibre());
        currentPlaza.setFianza(plaza.getFianza());
        currentPlaza.setPrecioHora(plaza.getPrecioHora());

        currentPlaza = plazaService.guardarPlaza(plaza);

        return ResponseEntity.ok(currentPlaza);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePlaza(@PathVariable Long id) {
        plazaService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reservar")
    public ResponseEntity createReserva(@Valid @RequestBody Reserva reserva, @PathVariable Long id) throws URISyntaxException {
        Map<String,Object> response = new HashMap<>();
        response.put("reserva", reserva);
        if(reserva.getFechaInicio().isAfter(reserva.getFechaFin())){
            response.put("error","La fecha de inicio debe ser anterior a la fecha de fin");
            return ResponseEntity.badRequest().body(response);
        }else if(reserva.getFechaInicio().isBefore(LocalDateTime.now())){
            response.put("error","No se pueden realizar reservas en el pasado");
            return ResponseEntity.badRequest().body(response);
        }else{
            Reserva savedReserva = reservaService.guardarReserva(reserva);
            return ResponseEntity.created(new URI("/reservas/" + savedReserva.getId())).body(savedReserva);
        }
    }

    
    @GetMapping("/{id}")
    public Plaza infoPlazaYCliente(@PathVariable Long id){
    	return plazaService.findById(id);
    }
    
    @GetMapping("/plazasDelUsuario/{id}")
    public List<Plaza> PlazasCliente(@PathVariable Long id){
    	return plazaService.findUserById(id);
    }
    
    
    
    public void toJson(List<String> array) throws JSONException{
    	JSONObject featureCollection = new JSONObject();
        featureCollection.put("type", "FeatureCollection");
        JSONObject properties = new JSONObject();
        properties.put("name", "ESPG:4326");
        JSONObject crs = new JSONObject();
        crs.put("type", "name");
        crs.put("properties", properties);
        featureCollection.put("crs", crs);

        JSONArray features = new JSONArray();



        // ciclo for
        for (Plaza obj : plazaService.findAll()) {
            JSONObject feature = new JSONObject();
            feature.put("type", "Feature");
            //JSONArray JSONArrayCoord = new JSONArray();
            JSONObject geometry = new JSONObject();
            JSONObject desc = new JSONObject();
            JSONObject newFeature = new JSONObject();


            //JSONArrayCoord.put(0, Double.parseDouble(obj.getLongitud()));
            //JSONArrayCoord.put(1, Double.parseDouble(obj.getLatitud()));
            JSONArray JSONArrayCoord = new JSONArray("[" + obj.getDireccion() + "]");

            geometry.put("type", "Point");
            geometry.put("coordinates", JSONArrayCoord);
            feature.put("geometry", geometry);
            // proper.put("properties", desc);
            feature.put("properties", desc);
            desc.put("name", obj.getDireccion());


            features.put(feature);
            featureCollection.put("features", features);

            // System.out.println(featureCollection.toString());
            // }

        }
        System.out.println(featureCollection.toString());
	}
    
    
    @GetMapping("/test")
    public List<Plaza> getPlazasGeoJson() {
        return plazaService.findAll();
    }
    
}
