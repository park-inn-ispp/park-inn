package com.parkinn.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parkinn.model.Park;

public interface ParkRepository extends JpaRepository<Park, Long> {

    @Query("SELECT p FROM Park p WHERE p.direccion LIKE :direccion")
	Park findByAddress(@Param("direccion")String direccion) throws DataAccessException;
	
}
