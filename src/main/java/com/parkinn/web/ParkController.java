package com.parkinn.web;

import java.util.List;

import com.parkinn.model.Park;
import com.parkinn.repository.ParkRepository;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parks")
public class ParkController {
    
    private ParkRepository pr;

    public ParkController(ParkRepository pr) {
        this.pr = pr;
    }
    @GetMapping
    public List<Park> getAllParks(){
        return pr.findAll();
    }

    @PostMapping("/{address}")
    public Park infoPlazaYCliente(@PathVariable("address") String address, ModelMap modelMap){
            Park park = pr.findByAddress(address);
            return park;
    }
}