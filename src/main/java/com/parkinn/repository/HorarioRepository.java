package com.parkinn.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parkinn.model.Horario;
import com.parkinn.model.Plaza;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
	
}
