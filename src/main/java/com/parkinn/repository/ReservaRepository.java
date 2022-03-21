package com.parkinn.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parkinn.model.Plaza;
import com.parkinn.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
	
	 @Query(value = "SELECT DISTINCT r.* FROM Reservas r WHERE r.user_id LIKE :usuario_id", nativeQuery=true)
	   	public List<Reserva> findByUserId(@Param("usuario_id")Long id) throws DataAccessException;
} 