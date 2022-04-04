package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.parkinn.model.Incidencia;

public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {


    Optional<Incidencia> findById(Long id);

}
