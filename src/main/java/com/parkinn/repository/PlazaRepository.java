package com.parkinn.repository;

import org.springframework.dao.DataAccessException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.parkinn.model.Client;
import com.parkinn.model.Plaza;

public interface PlazaRepository extends JpaRepository<Plaza, Long> {

    @Query(value = "SELECT p FROM Park p WHERE p.direccion LIKE :direccion", nativeQuery=true)
   	public Plaza findByAddress(@Param("direccion")String direccion) throws DataAccessException;
    
    public List<Plaza> findByDireccionAndAdministrador(String direccion, Client administrador);
    
    @Query(value = "SELECT DISTINCT p.* FROM Plazas p WHERE p.user_id LIKE :usuario_id", nativeQuery=true)
    public List<Plaza> findByUserId(@Param("usuario_id") Long id) throws DataAccessException;

}

