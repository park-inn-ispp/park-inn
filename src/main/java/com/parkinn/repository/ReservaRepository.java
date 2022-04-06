package com.parkinn.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parkinn.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

	 @Query(value = "SELECT DISTINCT r.* FROM Reservas r WHERE r.plaza_id LIKE :plaza_id", nativeQuery=true)
	 public List<Reserva> findByPlazaId(@Param("plaza_id") Long id) throws DataAccessException;
	   	
	 @Query(value = "SELECT DISTINCT r.* FROM Reservas r WHERE r.user_id LIKE :usuario_id", nativeQuery=true)
	   	public List<Reserva> findByUserId(@Param("usuario_id")Long id) throws DataAccessException;
	 
	 public Optional<Reserva> findById(Long id);
} 

