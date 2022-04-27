package com.parkinn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parkinn.model.Comision;
import com.parkinn.repository.ComisionRepository;

@Service
public class ComisionService {

	@Autowired
	private ComisionRepository repository;
	
	
	public Comision save(Comision comision){
        Comision savedComision = repository.save(comision);
        return savedComision;
    }
	
	 public Comision findById(Long id){
	        Comision comision = repository.findById(id).orElse(null);
	        return comision;
	 }
	 
	 
}
