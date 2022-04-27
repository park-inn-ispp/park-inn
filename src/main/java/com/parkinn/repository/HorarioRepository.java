package com.parkinn.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parkinn.model.Horario;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
	
	@Query(value = "SELECT DISTINCT h.* FROM horarios h WHERE h.plaza_id LIKE :plaza_id", nativeQuery=true)
	 public List<Horario> findHorariosByPlazaId(@Param("plaza_id") Long id) throws DataAccessException;

	 @Query(value = "SELECT DISTINCT h.* FROM horarios h WHERE h.plaza_id LIKE :plaza_id AND h.activo=1 ORDER BY h.fecha_inicio", nativeQuery=true)
	 public List<Horario> findHorariosByPlazaIdSortedByFechaInicio(@Param("plaza_id") Long id) throws DataAccessException;

}
