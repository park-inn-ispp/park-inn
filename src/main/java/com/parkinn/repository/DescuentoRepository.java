package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.parkinn.model.Descuento;


public interface DescuentoRepository extends JpaRepository<Descuento, Long> {

    Optional<Descuento> findByName(String name);
	
}
