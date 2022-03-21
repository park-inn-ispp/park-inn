package com.parkinn.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parkinn.model.Horario;


public interface HorarioRepository extends JpaRepository<Horario, Long> {

	@Query(value = "SELECT DISTINCT h.* FROM HORARIOS h WHERE h.plaza_id LIKE :id",nativeQuery = true)
    public List<Horario> horarios(@Param("id") Long id) throws DataAccessException;
}
