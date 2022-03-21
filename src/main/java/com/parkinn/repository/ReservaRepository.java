package com.parkinn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.parkinn.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

}
	

