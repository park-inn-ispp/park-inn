package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkinn.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

}
