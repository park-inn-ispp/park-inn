package com.parkinn.web;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.parkinn.model.Parking;
import com.parkinn.repository.ParkingRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// @CrossOrigin(origins = "*",methods = {RequestMethod.GET,RequestMethod.PUT,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.HEAD}, maxAge = 1800)
@RestController
@RequestMapping("/parkings")
public class ParkingsController {

    private final ParkingRepository parkingRepository;

    public ParkingsController(ParkingRepository ParkingRepository) {
        this.parkingRepository = ParkingRepository;
    }

    @GetMapping
    public List<Parking> getParkings() {
        return parkingRepository.findAll();
    }

    @GetMapping("/{id}")
    public Parking getParking(@PathVariable Long id) {
        return parkingRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    @PostMapping
    public ResponseEntity createParking(@RequestBody Parking parking) throws URISyntaxException {
        Parking savedParking = parkingRepository.save(parking);
        return ResponseEntity.created(new URI("/parkings/" + savedParking.getId())).body(savedParking);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateParking(@PathVariable Long id, @RequestBody Parking parking) {
        Parking currentParking = parkingRepository.findById(id).orElseThrow(RuntimeException::new);
        currentParking.setAdress(parking.getAdress());
        currentParking.setDescripcion(parking.getDescripcion());
        currentParking.setAncho(parking.getAncho());
        currentParking.setLargo(parking.getLargo());
        currentParking.setDisponible(parking.isDisponible());
        currentParking.setAireLibre(parking.isAireLibre());
        currentParking.setFianza(parking.getFianza());
        currentParking.setPrecioHora(parking.getPrecioHora());

        currentParking = parkingRepository.save(parking);

        return ResponseEntity.ok(currentParking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteParking(@PathVariable Long id) {
        parkingRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}