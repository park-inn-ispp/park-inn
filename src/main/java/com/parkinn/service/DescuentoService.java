package com.parkinn.service;

import java.util.List;

import com.parkinn.model.Descuento;
import com.parkinn.repository.DescuentoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




@Service
public class DescuentoService {

	@Autowired
	private DescuentoRepository repository;
	
	public List<Descuento> findAll(){
        return repository.findAll();
    }

	public Descuento guardarDescuento(Descuento descuento){
        Descuento savedDescuento = repository.save(descuento);
        return savedDescuento;
    }
	
	 public Descuento findById(Long id){
	        Descuento descuento = repository.findById(id).orElse(null);
	        return descuento;
	 }

	 public Descuento findByName(String name){
		Descuento descuento = repository.findByName(name).orElse(null);
		return descuento;
 }
	 
	 public void deleteById(Long id){
	        repository.deleteById(id);
	 }
	 
}
